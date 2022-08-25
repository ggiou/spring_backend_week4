package com.sparta.post_2.repository;

import com.sparta.post_2.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long>{
    List<Comment> findAllByOrderByModifiedAtDesc();
}
