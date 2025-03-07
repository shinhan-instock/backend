package com.pda.community_module.web.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
public class PostResponseDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class getPostDTO {
        private Long id;
        private String nickname;
        private String profileImg;
        private String content;
        private String hashtag;
        private Long sentimentScore;
        private String images;
        private int likes;
        private int comments;
        private LocalDateTime created_at;
        private LocalDateTime update_at;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreatePostResponseDTO {
        private Long id;
        private String content;
        private String imageUrl;
        private String hashtag;
        private String nickname;

    }
}


