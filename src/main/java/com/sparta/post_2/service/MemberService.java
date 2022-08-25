package com.sparta.post_2.service;

import com.sparta.post_2.controller.request.MemberResponseDto;
import com.sparta.post_2.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import com.sparta.post_2.util.SecurityUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor //생성자 주입
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberResponseDto getMemberInfo(String email){
        return memberRepository.findByNickname(email)
                .map(MemberResponseDto::of)
                .orElseThrow(() -> new RuntimeException("유저 정보가 없습니다."));
    }
    // 현재 SecurityContext 에 있는 유저 정보 가져오기
    @Transactional(readOnly = true)
    public MemberResponseDto getMyInfo() {
        return memberRepository.findById(SecurityUtil.getCurrentMemberId())
                .map(MemberResponseDto::of)
                .orElseThrow(() -> new RuntimeException("로그인 유저 정보가 없습니다."));
    }
}
