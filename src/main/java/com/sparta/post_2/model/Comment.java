package com.sparta.post_2.model;

import com.sparta.post_2.controller.request.CommentDto;
import com.sparta.post_2.controller.request.CommentRequestDto;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment")
@Builder
@Entity
public class Comment extends Timestamped {
    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post posts;

    public void update(CommentDto commentDto){
        this.comment = commentDto.getComment();
    }

}
