package com.pda.community_module.service;

import com.pda.community_module.web.dto.PostResponseDTO;

import java.util.List;

public interface PostService {


    List<PostResponseDTO.getPostDTO> getPosts(Boolean following, Boolean popular, Boolean scrap, Long user_id);
}
