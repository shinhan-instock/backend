package com.pda.community_module.converter;

import com.pda.community_module.domain.File;
import com.pda.community_module.domain.Post;
import com.pda.community_module.domain.User;

public class FileConverter {
    public static File toFile(String pictureUrl, User user, Post post) {
        return File.builder()
                .url(pictureUrl)
                .user(user)
                .post(post)
                .build();
    }
}