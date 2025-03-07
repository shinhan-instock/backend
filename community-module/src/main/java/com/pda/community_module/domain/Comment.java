package com.pda.community_module.domain;

import com.pda.community_module.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "comment")
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

    // 소프트 딜리트를 위한 삭제 플래그 추가 (기본값 false)
    @Builder.Default
    @Column(nullable = false)
    private Boolean deleted = false;


    // 댓글 내용을 업데이트하는 메서드 추가
    public void updateContent(String content) {
        this.content = content;
    }

    // 댓글의 내용과 작성자(User)를 업데이트하는 메서드
    public void update(String newContent, User newUser) {
        // 내용 업데이트
        if (newContent != null && !newContent.isEmpty()) {
            this.content = newContent;
        }
        // 작성자 업데이트
        if (newUser != null) {
            this.user = newUser;
        }
    }

    // 댓글 삭제 시, 삭제 요청을 보낸 user와 댓글의 작성자를 체크하여 삭제하는 update 메서드
    public void delete(User requestUser) {
        // 삭제 요청한 사용자가 댓글 작성자와 일치하는지 확인
        if (!this.user.getId().equals(requestUser.getId())) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }
        // soft delete의 경우 삭제 플래그 업데이트 (물리적 삭제를 원한다면 repository.delete(this) 호출)
        this.deleted = true;
    }
}
