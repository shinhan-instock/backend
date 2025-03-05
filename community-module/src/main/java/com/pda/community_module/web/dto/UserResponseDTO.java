package com.pda.community_module.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    }
}
