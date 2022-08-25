package com.sparta.post_2.service;

import com.sparta.post_2.controller.request.*;
import com.sparta.post_2.model.Member;
import com.sparta.post_2.model.RefreshToken;
import com.sparta.post_2.repository.MemberRepository;
import com.sparta.post_2.repository.RefreshTokenRepository;
import com.sparta.post_2.security.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public ResponseDto<?> signup(MemberRequestDto memberRequestDto) {
        if (memberRepository.existsByNickname(memberRequestDto.getNickname())) {
            return ResponseDto.fail("Sign-in Error", "이미 가입된 닉네임입니다.");
        } else if (!memberRequestDto.getPasswordComfirm().equals(memberRequestDto.getPassword())) {
            return ResponseDto.fail("Sign-in Error", "비밀번호와 비밀번호 확인값이 같지 않습니다.");
        } else {
            Member member = memberRequestDto.toMember(passwordEncoder);
            memberRepository.save(member);
            return ResponseDto.success(new MemberDto(member));
        }
    }

    @Transactional
    public ResponseDto<?> login2(LoginDto memberRequestDto) {
        //일단 db에 없으면 아래의 논리를 거칠 필요가 없음
        Optional<?> member = memberRepository.findByNickname(memberRequestDto.getNickname());
        if (!member.isPresent()) {
            return ResponseDto.fail("Log-in Error", "유저 정보가 없습니다.");
        }
        Member temp2 = (Member) member.get();
        if (!passwordEncoder.matches(memberRequestDto.getPassword(), temp2.getPassword())) {//맞지 않으면!
            return ResponseDto.fail("Log-in Error", "비밀번호가 틀립니다.");

        }
        System.out.println("dsdsdssddssddssd");
        TokenDto tokenDto = login(memberRequestDto);
        System.out.println(tokenDto.getAccessToken());

        return ResponseDto.success(new MemberDto(temp2));
    }

    @Transactional
    public TokenDto login(LoginDto memberRequestDto) {
        // 1. Login ID/PW 를 기반으로 AuthenticationToken 생성

        UsernamePasswordAuthenticationToken authenticationToken = memberRequestDto.toAuthentication();

        // 2. 실제로 검증 (사용자 비밀번호 체크) 이 이루어지는 부분
        //    authenticate 메서드가 실행이 될 때 CustomUserDetailsService 에서 만들었던 loadUserByUsername 메서드가 실행됨
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);

        // 4. RefreshToken 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(authentication.getName())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }


    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("Refresh Token 이 유효하지 않습니다.");
        }
        // 2. Access Token 에서 Member ID 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());
        // 3. 저장소에서 Member ID 를 기반으로 Refresh Token 값 가져옴
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃 된 사용자입니다."));
        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            throw new RuntimeException("토큰의 유저 정보가 일치하지 않습니다.");
        }
        // 5. 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.generateTokenDto(authentication);
        // 6. 저장소 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateValue(tokenDto.getRefreshToken());

        refreshTokenRepository.save(newRefreshToken);
        // 토큰 발급
        return tokenDto;
    }
}
