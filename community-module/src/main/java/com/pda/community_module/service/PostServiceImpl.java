package com.pda.community_module.service;

import com.pda.community_module.converter.PostConverter;
import com.pda.community_module.domain.Post;
import com.pda.community_module.repository.PostRepository;
import com.pda.community_module.web.dto.PostResponseDTO;
import com.pda.core_module.apiPayload.GeneralException;
import com.pda.core_module.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public List<PostResponseDTO.getPostDTO> getPosts(Boolean following, Boolean popular, Boolean scrap, Long userId) {
        List<Post> posts;

        if (popular) {
            // 인기 게시글 (좋아요 많은 순)
            posts = postRepository.findAllByOrderByLikesDesc();
        } else if (following){
            // 팔로잉한 유저의 게시글
            List<Long> followingIdList= postRepository.findAllFollowingIdsByUserId(userId);
            posts = postRepository.findAllByUserIdIn(followingIdList);
        }
        else if (scrap){
            // 스크랩한 글
            List<Long> postidList=postRepository.findAllPostIdsByUserId(userId);
            posts = postRepository.findAllById(postidList);
        }else {
            // 모든 게시글 (최신순)
            posts = postRepository.findAll();
        }

        return PostConverter.toPostListEntity(posts);
    }

    @Override
    public PostResponseDTO.getPostDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        return PostConverter.toPostEntity(post);
    }

    @Override
    public List<PostResponseDTO.getPostDTO> getMyPosts() {
        // 나인지 확인해서 post 리스트인데 나인지 확인어케하는지 몰라서 수정할거임 나중에
        List<Post> posts = postRepository.findAll();
        return PostConverter.toPostListEntity(posts);
    }

    @Override
    public List<PostResponseDTO.getPostDTO> getPostsByUser(Long userId) {
        List<Post> posts = postRepository.findAllByUserId(userId);
        return PostConverter.toPostListEntity(posts);
    }
}