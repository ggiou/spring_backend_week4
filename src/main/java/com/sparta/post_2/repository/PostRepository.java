package com.sparta.post_2.repository;

import com.sparta.post_2.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long userId);
    List<Post> findAllByOrderByModifiedAtDesc();
}
