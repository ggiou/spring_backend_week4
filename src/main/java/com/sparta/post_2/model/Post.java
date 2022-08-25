package com.sparta.post_2.model;

import com.sparta.post_2.controller.request.PostRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post")
@Entity
public class Post extends Timestamped {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tittle;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String author;

    @OneToMany(mappedBy = "posts", fetch = FetchType.EAGER,cascade = CascadeType.REMOVE)
    @OrderBy("id asc") //댓글 오름차순 정렬
    private List<Comment> comments;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; //멤버의 id

    public Post(PostRequestDto postrequestDto,String author, Member member){
        this.author = author;
        this.member = member;
        this.tittle = postrequestDto.getTitle();
        this.content = postrequestDto.getContent();
    }

    public void update(PostRequestDto postRequestDto){
        this.tittle = postRequestDto.getTitle();
        this.content = postRequestDto.getContent();
    }
}
