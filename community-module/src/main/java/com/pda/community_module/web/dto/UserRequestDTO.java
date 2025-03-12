package com.pda.community_module.web.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

public class UserRequestDTO {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginUserDTO {
        private String userId;
        private String password;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Setter
    public static class UpdateUserDTO {
        private String name;
        private String nickname;
        private MultipartFile image;
        private String introduction;
    }
}
