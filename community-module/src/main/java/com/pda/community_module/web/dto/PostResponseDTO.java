package com.pda.community_module.web.dto;

import lombok.*;

@Getter
public class PostResponseDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class getPostDTO {
        private Long id;
        private String nickname;
        private String content;
        private String hashtag;
        private Long sentimentScore;
        private String images;
        private int likes;
        private int comments;
    }
}
