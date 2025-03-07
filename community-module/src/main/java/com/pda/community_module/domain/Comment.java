package com.pda.community_module.domain;

import com.pda.community_module.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "comments")
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne  // Post 엔티티와 연관관계 (외래키: post_id)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne  // User 엔티티와 연관관계 (외래키: user_id)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 255)
    private String content;

    // 댓글 내용을 업데이트하는 메서드 추가
    public void updateContent(String content) {
        this.content = content;
    }
}
