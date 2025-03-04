package com.pda.community_module.web.dto;


import com.pda.community_module.domain.Post;
import com.pda.community_module.domain.User;
import jakarta.persistence.*;
import lombok.*;

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
        private String images;
    }





}
