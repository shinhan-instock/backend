package com.pda.community_module.web.dto;


import com.pda.community_module.domain.Post;
import com.pda.community_module.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class PostRequestDTO {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EditPostDTO {
        private String content;
        private String hashtag;
        private MultipartFile file;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreatePostDTO {
         String userId;
         String content;
         MultipartFile file;
         String hashtag;
    }


}
