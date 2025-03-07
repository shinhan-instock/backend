package com.pda.community_module.service;

import com.pda.community_module.web.dto.PostRequestDTO;
import com.pda.community_module.web.dto.PostResponseDTO;

import java.util.List;

public interface PostService {


    List<PostResponseDTO.getPostDTO> getPosts(Boolean following, Boolean popular, Boolean scrap, String userid);

    PostResponseDTO.getPostDTO getPostById(Long postId);

    List<PostResponseDTO.getPostDTO> getMyPosts(String userid);

    List<PostResponseDTO.getPostDTO> getPostsByUser(String nickname);

    List<PostResponseDTO.getPostDTO> getPostsByStock(String name);

    void deletePost(String userid, Long id);

    void editPost(String userid, Long id, PostRequestDTO.EditPostDTO editPostDTO);

    void addLikes(String userid, Long id);

    void deleteLikes(String userid, Long id);

    void addScrap(String userid, Long id);

    void deleteScrap(String userid, Long id);

    Long getLikeByUser(String userid, Long id);

    PostResponseDTO.CreatePostResponseDTO createPost(PostRequestDTO.CreatePostDTO createPostDTO);
}
