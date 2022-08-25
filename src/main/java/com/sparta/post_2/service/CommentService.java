package com.sparta.post_2.service;

import com.sparta.post_2.controller.request.CommentDto;
import com.sparta.post_2.controller.request.CommentRequestDto;
import com.sparta.post_2.controller.request.CommentResponseDto;
import com.sparta.post_2.controller.request.ResponseDto;
import com.sparta.post_2.model.Comment;
import com.sparta.post_2.model.Member;
import com.sparta.post_2.model.Post;
import com.sparta.post_2.repository.CommentRepository;
import com.sparta.post_2.repository.MemberRepository;
import com.sparta.post_2.repository.PostRepository;
import com.sparta.post_2.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

        @Transactional
        public ResponseDto<?> createComment(CommentRequestDto commentDto, Long id) {
            Optional<Post> temp =postRepository.findById(id);//게시물
            if (temp.isEmpty()) {
                return ResponseDto.fail("NULL_POST_ID", "해당 게시물의 ID가 존재하지 않습니다.");
            }
            Optional<Member> current = memberRepository.findById(SecurityUtil.getCurrentMemberId());//일단 꺼내는 거임
            commentDto.setPosts(temp.get());//게시글 지정
            commentDto.setAuthor(current.get().getNickname());//저자 지정
            Comment comment = commentDto.toEntity();//새로운 객체 지정
            //새로운 comment 생성
            commentRepository.save(comment);
            CommentResponseDto responseDto = new CommentResponseDto(comment);
            return ResponseDto.success(responseDto);
        }
        @Transactional
        public ResponseDto<?> modifyComment(CommentDto commentDto, Long id) {
            Optional<Member> current = memberRepository.findById(SecurityUtil.getCurrentMemberId());//일단 꺼내는 거임
            Optional<Comment> comment = commentRepository.findById(id);
            if (comment.isEmpty()) {
                return ResponseDto.fail("NULL_POST_ID", "해당 댓글 ID가 존재하지 않습니다.");
            }
            Comment temp =comment.get();
            Member temp2 =  current.get();//현재 로그인된 사용자의 정보
            if (!temp.getAuthor().equals(temp2.getNickname())) {
                return ResponseDto.fail("WRONG_ACCESS", "해당 댓글의 작성자가 아닙니다.");
            }
            temp.update(commentDto);
            CommentResponseDto commentResponseDto = new CommentResponseDto(temp);
            return ResponseDto.success(commentResponseDto);
        }

        @Transactional
        public ResponseDto<?> deleteComment(Long id) {
            //일단 post 중에서 찾고 하는거제
            Optional<Member> current = memberRepository.findById(SecurityUtil.getCurrentMemberId());//일단 꺼내는 거임
            Optional<Comment> comment = commentRepository.findById(id);
            if (comment.isEmpty()) {
                return ResponseDto.fail("NULL_POST_ID", "해당 댓글의 ID가 존재하지 않습니다.");
            }
            Comment temp =  comment.get();//post의 정보
            Member temp2 = current.get();//현재 로그인된 사용자의 정보
            if (!temp.getAuthor().equals(temp2.getNickname())) {
                return ResponseDto.fail("WRONG_ACCESS", "해당 댓글의 작성자가 아닙니다.");
            } else
                commentRepository.delete(temp);//지웠다잉
            return ResponseDto.success("success");
        }

    }