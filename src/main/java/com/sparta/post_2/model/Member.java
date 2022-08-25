package com.sparta.post_2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor // 기본생성자 생성
@Table(name = "member")
@Entity //DB 테이블 역할
public class Member extends Timestamped {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nickname;

    @OneToOne(mappedBy = "member")
    private Post post;

    @JsonIgnore
    private String password;

    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Builder
    public Member(String nickname, String password, Authority authority){
        this.nickname = nickname;
        this.password = password;
        this.authority = authority;
    }


}
