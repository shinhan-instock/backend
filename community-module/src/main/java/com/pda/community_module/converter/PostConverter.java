package com.pda.community_module.converter;

import com.pda.community_module.domain.File;
import com.pda.community_module.domain.Post;
import com.pda.community_module.domain.User;
import com.pda.community_module.domain.mapping.PostLike;
import com.pda.community_module.domain.mapping.PostScrap;
import com.pda.community_module.service.S3Service;
import com.pda.community_module.web.dto.PostRequestDTO;
import com.pda.community_module.web.dto.PostResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public class PostConverter {
    public static List<PostResponseDTO.toPostDTO> toPostListDto(List<Post> posts) {
        return posts.stream()
//                .sorted(Comparator.comparing(Post::getCreatedAt).reversed()) // 최신순 정렬
                .map(post -> new PostResponseDTO.toPostDTO(
                        post.getId(),
                        post.getUser().getNickname(),
                        (post.getUser().getFile() != null ? post.getUser().getFile().getUrl() : null),
                        post.getContent(),
                        post.getHashtag(),
                        post.getSentiment() != null ? post.getSentiment().getSentimentScore() : 50,
                        post.getFile() != null ? post.getFile().getUrl() : null,
                        post.getLikes().size(),
                        post.getComments().size(),
                        post.getCreatedAt(),
                        post.getUpdateAt()
                ))
                .collect(Collectors.toList());
    }

    public static List<PostResponseDTO.getPostDTO> getPostListDto(List<Post> posts, String userId) {
        return posts.stream()
//                .sorted(Comparator.comparing(Post::getCreatedAt).reversed()) // 최신순 정렬
                .map(post -> new PostResponseDTO.getPostDTO(
                        post.getId(),
                        post.getUser().getNickname(),
                        (post.getUser().getFile() != null ? post.getUser().getFile().getUrl() : null),
                        post.getContent(),
                        post.getHashtag(),
                        post.getSentiment() != null ? post.getSentiment().getSentimentScore() : 50,
                        post.getFile() != null ? post.getFile().getUrl() : null,
                        post.getLikes().size(),
                        post.getComments().size(),
                        post.getCreatedAt(),
                        post.getUpdateAt(),
                        post.getLikes().stream()
                                .anyMatch(like -> like.getUser().getUserId().equals(userId)),
                        post.getScraps().stream()
                                .anyMatch(scrap -> scrap.getUser().getUserId().equals(userId))
                ))
                .collect(Collectors.toList());
    }



    public static PostResponseDTO.toPostDTO toPostDto(Post post) {
        return new PostResponseDTO.toPostDTO(
                post.getId(),
                post.getUser().getNickname(),
                (post.getUser().getFile() != null ? post.getUser().getFile().getUrl() : null),
                post.getContent(),
                post.getHashtag(),
                post.getSentiment() != null ? post.getSentiment().getSentimentScore() : 50,
                (post.getFile() != null ? post.getFile().getUrl() : null),
                post.getLikes().size(),
                post.getComments().size(), post.getCreatedAt(),
                post.getUpdateAt()

        );
    }

    public static Post toPostEntity(User user, PostRequestDTO.EditPostDTO editPostDTO, Post existingPost, S3Service s3Service) {
        // 기존 Post의 File 객체 가져오기
        File existingFile = existingPost.getFile();

        // 이미지가 변경되었는지 확인
        if (editPostDTO.getFile() != null && !editPostDTO.getFile().isEmpty()) {
            if (existingFile != null) {
                // 기존 이미지가 있으면 업데이트
                String newImageUrl = s3Service.uploadFile(editPostDTO.getFile());
                existingFile.setUrl(newImageUrl);
            } else {
                // 기존 이미지가 없으면 새로 생성
                existingFile = s3Service.setPostImage(editPostDTO.getFile(), existingPost);
            }
        }

        return Post.builder()
                .id(existingPost.getId())
                .user(user)
                .content(editPostDTO.getContent() != null ? editPostDTO.getContent() : existingPost.getContent()) // 기존값 유지
                .hashtag(editPostDTO.getHashtag() != null ? editPostDTO.getHashtag() : existingPost.getHashtag()) // 기존값 유지
                .file(existingFile) // 기존 파일 유지 또는 업데이트
                .postCount(existingPost.getPostCount()) // 유지
                .sentiment(existingPost.getSentiment()) // 유지
                .scraps(existingPost.getScraps()) // 유지
                .comments(existingPost.getComments()) // 유지
                .likes(existingPost.getLikes()) // 유지
                .deleted(existingPost.getDeleted()) // 유지
//                .createdAt(existingPost.getCreatedAt())
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

    public static PostResponseDTO.CreatePostResponseDTO toPostResponseDTO(Post post) {
        return PostResponseDTO.CreatePostResponseDTO.builder()
                .id(post.getId())
                .content(post.getContent())
                .imageUrl(post.getFile() != null ? post.getFile().getUrl() : null)  // 파일이 없을 경우 NPE 방지
                .hashtag(post.getHashtag())
                .nickname(post.getUser().getNickname())
                .build();
    }

}

