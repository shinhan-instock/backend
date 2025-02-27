package com.pda.community_module.converter;

import com.pda.community_module.domain.Post;
import com.pda.community_module.web.dto.PostResponseDTO;
import com.pda.community_module.web.dto.StockResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class PostConverter {
    public static List<PostResponseDTO.getPostDTO> toPostListEntity(List<Post> posts){
       return posts.stream().map(post -> new PostResponseDTO.getPostDTO(
                post.getId(),
                post.getUser().getNickname(),
                post.getContent(),
                post.getHashtag(),
                post.getSentiment() != null ? post.getSentiment().getSentimentScore() : 50,
                post.getImageUrl(),
                post.getLikes().size(),
                post.getComments().size()
        )).collect(Collectors.toList());
    }
}
