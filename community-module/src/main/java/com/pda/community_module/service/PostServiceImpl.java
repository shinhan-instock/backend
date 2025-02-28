package com.pda.community_module.service;

import com.pda.community_module.converter.PostConverter;
import com.pda.community_module.domain.Post;
import com.pda.community_module.domain.User;
import com.pda.community_module.domain.WatchList;
import com.pda.community_module.domain.mapping.PostScrap;
import com.pda.community_module.repository.PostRepository;
import com.pda.community_module.repository.PostScrapRepository;
import com.pda.community_module.repository.UserRepository;
import com.pda.community_module.web.dto.PostResponseDTO;
import com.pda.community_module.web.dto.WatchListRequestDTO;
import com.pda.core_module.apiPayload.GeneralException;
import com.pda.core_module.apiPayload.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    private final PostScrapRepository postScrapRepository;

    @Override
    public List<PostResponseDTO.getPostDTO> getPosts(Boolean following, Boolean popular, Boolean scrap, Long userId) {
        List<Post> posts;

        if (popular) {
            // 인기 게시글 (좋아요 많은 순)
            posts = postRepository.findAllByOrderByLikesDesc();
        } else if (following){
            // 팔로잉한 유저의 게시글
            User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
            List<Long> followingIdList = user.getFollowingList().stream()
                    .map(follow -> follow.getFollowing().getId()) //
                    .collect(Collectors.toList());

            posts = postRepository.findAllByUserIdIn(followingIdList);
        }
        else if (scrap){
            // 스크랩한 글
            User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
            List<PostScrap> postScraps = postScrapRepository.findByUser(user); // 내 userId
            posts = postScraps.stream()
                    .map(postScrap -> postScrap.getPost())
                    .collect(Collectors.toList());
        }else {
            // 모든 게시글 (최신순)
            posts = postRepository.findAll();
        }

        return PostConverter.toPostListDto(posts);
    }

    @Override
    public PostResponseDTO.getPostDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        return PostConverter.toPostDto(post);
    }

    @Override
    public List<PostResponseDTO.getPostDTO> getMyPosts() {
        // 나인지 확인해서 post 리스트인데 나인지 확인어케하는지 몰라서 수정할거임 나중에
        List<Post> posts = postRepository.findAll();
        return PostConverter.toPostListDto(posts);
    }

    @Override
    public List<PostResponseDTO.getPostDTO> getPostsByUser(String nickname) {
        User user =  userRepository.findByNickname(nickname);
        List<Post> posts = postRepository.findAllByUserId(user.getId());

        return PostConverter.toPostListDto(posts);
    }

    @Override
    public List<PostResponseDTO.getPostDTO> getPostsByStock(String name) {
        List<Post> posts = postRepository.findAllByHashtag(name);
        return PostConverter.toPostListDto(posts);
    }

    @Override
    public void deletePost(Long userid, Long id) {
        User user = userRepository.findById(userid).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        Post post  = postRepository.findById(id).orElseThrow(()-> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        postRepository.delete(post);
    }
}