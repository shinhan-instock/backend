package com.pda.community_module.web.dto;

import lombok.*;

public class UserResponseDTO {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class getUserDTO {
        private String userId;
        private String nickname;
        private String imageUrl;
        private String introduction;
        private boolean isInfluencer;
        private boolean openAccount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class getUserInfoDTO {
        private String userId;
        private String name;
        private String nickname;
        private String imageUrl;
        private String introduction;
        private boolean isInfluencer;
        private boolean openAccount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserRealPKResponseDto {
        private Long id;        // 실제 DB에서 사용하는 PK
    }
}
