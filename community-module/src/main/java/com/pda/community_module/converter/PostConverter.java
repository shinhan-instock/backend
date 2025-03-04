package com.pda.community_module.converter;

import com.pda.community_module.domain.Post;
import com.pda.community_module.domain.User;
import com.pda.community_module.domain.WatchList;
import com.pda.community_module.domain.mapping.PostLike;
import com.pda.community_module.domain.mapping.PostScrap;
import com.pda.community_module.web.dto.PostRequestDTO;
import com.pda.community_module.web.dto.PostResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class PostConverter {
    public static List<PostResponseDTO.getPostDTO> toPostListDto(List<Post> posts){
       return posts.stream().map(post -> new PostResponseDTO.getPostDTO(
                post.getId(),
                post.getUser().getNickname(),
                post.getContent(),
                post.getHashtag(),
                post.getSentiment() != null ? post.getSentiment().getSentimentScore() : 50,
                post.getImageUrl(),
                post.getLikes().size(),
                post.getComments().size(),
               post.getCreatedAt(),
               post.getUpdateAt()
        )).collect(Collectors.toList());
    }

    public static PostResponseDTO.getPostDTO toPostDto(Post post) {
        return new PostResponseDTO.getPostDTO(
                post.getId(),
                post.getUser().getNickname(),
                post.getContent(),
                post.getHashtag(),
                post.getSentiment() != null ? post.getSentiment().getSentimentScore() : 50,
                post.getImageUrl(),
                post.getLikes().size(),
                post.getComments().size(), post.getCreatedAt(),
                post.getUpdateAt()

        );
    }

    public static Post toPostEntity(User user, PostRequestDTO.EditPostDTO editPostDTO, Post existingPost) {

        return Post.builder()
                .id(existingPost.getId())
                .user(user)
                .content(editPostDTO.getContent())
                .hashtag(editPostDTO.getHashtag())
                .imageUrl(editPostDTO.getImages())
                // 기존값
                .postCount(existingPost.getPostCount())
                .sentiment(existingPost.getSentiment())
                .scraps(existingPost.getScraps())
                .comments(existingPost.getComments())
                .likes(existingPost.getLikes())
                .deleted(existingPost.getDeleted())
                .build();
    }

    public static PostLike toPostLikeEntity(User user, Post post) {
        return PostLike.builder()
                .user(user)
                .post(post)
                .build();
    }
    public static PostScrap toPostScrapEntity(User user, Post post) {
        return PostScrap.builder()
                .user(user)
                .post(post)
                .build();
    }
}
