package com.pda.piggyBank_module.web.dto;


import lombok.*;

public class UserResponseDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserRealPKResponseDto {
        private Long id;        // 실제 DB에서 사용하는 PK
    }
}
