package com.sparta.post_2.service;

import com.sparta.post_2.controller.request.CommentResponseDto;
import com.sparta.post_2.controller.request.PostResponseDto;
import com.sparta.post_2.controller.request.ResponseDto;
import com.sparta.post_2.model.Comment;
import com.sparta.post_2.model.Post;
import com.sparta.post_2.repository.CommentRepository;
import com.sparta.post_2.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AllService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost(){
        List<Post> list = postRepository.findAllByOrderByModifiedAtDesc();
        List<PostResponseDto> dtolist = new ArrayList<>();

        for(Post temp : list){
            PostResponseDto postResponseDto = new PostResponseDto(temp);
            dtolist.add(postResponseDto);
        }
        return ResponseDto.success(dtolist);
    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getPost(Long id){ //원하는 id 게시글 찾기
        Optional<Post> optionalPost = postRepository.findById(id);

        if(optionalPost.isEmpty()){
            return ResponseDto.fail("NULL_POST_ID", "해당 게시글 ID가 존재하지 않습니다.");
        }
        PostResponseDto postResponseDto = new PostResponseDto(optionalPost.get());
        return ResponseDto.success(postResponseDto);
    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllComment(){
        List<Comment> list = commentRepository.findAllByOrderByModifiedAtDesc();
        List<CommentResponseDto> dtolist = new ArrayList<>();
        for(Comment temp : list){
            CommentResponseDto commentResponseDto = new CommentResponseDto(temp);
            dtolist.add(commentResponseDto);
        }
        return ResponseDto.success(dtolist);
    }
}
