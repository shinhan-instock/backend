package com.pda.community_module.service;

import com.pda.community_module.web.dto.PostRequestDTO;
import com.pda.community_module.web.dto.PostResponseDTO;

import java.util.List;

public interface PostService {


    List<PostResponseDTO.getPostDTO> getPosts(Boolean following, Boolean popular, Boolean scrap, String userid);

    PostResponseDTO.getPostDTO getPostById(Long postId,String userId);

    List<PostResponseDTO.getPostDTO> getMyPosts(String userid);

    List<PostResponseDTO.getPostDTO> getPostsByUser(String nickname, String userid);

    List<PostResponseDTO.getPostDTO> getPostsByStock(String name, String userid);

    void deletePost(String userid, Long id);

    void editPost(String userid, Long id, PostRequestDTO.EditPostDTO editPostDTO);

    void addLikes(String userid, Long id);

    void deleteLikes(String userid, Long id);

    Long addScrap(String userid, Long id);

    void deleteScrap(String userid, Long id);

    Long getLikeByUser(String userid, Long id);

    PostResponseDTO.CreatePostResponseDTO createPost(PostRequestDTO.CreatePostDTO createPostDTO);

    void finalizePost(Long postId, Long sentimentScore);

    void rollbackPost(Long postId, String reason);
}
