package com.pda.community_module.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

public class CommentResponseDTO {

    /**
     * 댓글 조회 시 클라이언트에 응답할 정보
     */
    @Getter
    @Setter
    public static class getCommentDTO {
        private Long id;
        private Long postId;
        private String userId;
        private String content;
        // 필요시 BaseEntity의 createdAt, updatedAt도 추가 가능

        // 추가 필드
        private String userNickname;
        private String userImage;
        private LocalDateTime createdAt;  // BaseEntity의 createdAt 필드 사용 (import java.time.LocalDateTime)
    }

    /**
     * 댓글 생성 시 요청 데이터
     */
    @Getter
    @Setter
    public static class createCommentDTO {
        private Long postId;
        private String userId;
        private String content;
    }

    /**
     * 댓글 수정 시 요청 데이터
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class updateCommentDTO {
        private String content;
        private String userId;
    }
}
