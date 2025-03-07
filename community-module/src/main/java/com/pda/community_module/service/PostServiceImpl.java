package com.pda.community_module.service;

import com.pda.community_module.converter.PostConverter;
import com.pda.community_module.converter.WatchListConverter;
import com.pda.community_module.domain.*;
import com.pda.community_module.domain.mapping.PostLike;
import com.pda.community_module.domain.mapping.PostScrap;
import com.pda.community_module.repository.*;
import com.pda.community_module.web.dto.PostRequestDTO;
import com.pda.community_module.web.dto.PostResponseDTO;
import com.pda.core_module.apiPayload.GeneralException;
import com.pda.core_module.apiPayload.code.status.ErrorStatus;
import com.pda.core_module.events.CheckStockEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final PostScrapRepository postScrapRepository;
    private final S3Service s3Service;
    private final PostLikeRepository postLikeRepository;
    private final StringRedisTemplate redisTemplate;
    private final SentimentRepository sentimentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

//    private final SentimentService sentimentService;

    @Override
    public List<PostResponseDTO.getPostDTO> getPosts(Boolean following, Boolean popular, Boolean scrap, String userid) {
        List<Post> posts;

        // userid가 null일 경우 팔로잉, 스크랩한 글만 빈 리스트 반환
        if (userid == null) {
            if (popular) {
                // 인기 게시글 (좋아요 많은 순)
                posts = postRepository.findAllByOrderByLikesDesc();
            } else if (scrap || following) {
                // 팔로잉한 글이나 스크랩한 글일 때 빈 리스트 반환
                posts = new ArrayList<>();
            } else {
                // 모든 게시글 (최신순)
                posts = postRepository.findAll();
            }
        } else {
            // userid가 null이 아닌 경우, 정상적으로 사용자 정보 조회
            User user = userRepository.findByUserId(userid).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

            if (popular) {
                // 인기 게시글 (좋아요 많은 순)
                posts = postRepository.findAllByOrderByLikesDesc();
            } else if (following) {
                // 팔로잉한 유저의 게시글
                List<Long> followingIdList = user.getFollowingList().stream()
                        .map(follow -> follow.getFollowing().getId())
                        .collect(Collectors.toList());

                posts = postRepository.findAllByUserIdIn(followingIdList);
            } else if (scrap) {
                // 스크랩한 글
                List<PostScrap> postScraps = postScrapRepository.findByUser(user); // 내 userId
                posts = postScraps.stream()
                        .map(postScrap -> postScrap.getPost())
                        .collect(Collectors.toList());
            } else {
                // 모든 게시글 (최신순)
                posts = postRepository.findAll();
            }
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
    public List<PostResponseDTO.getPostDTO> getMyPosts(String userid) {
        User user = userRepository.findByUserId(userid).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        List<Post> posts = postRepository.findAllByUserId(user.getId());
        return PostConverter.toPostListDto(posts);
    }

    @Override
    public List<PostResponseDTO.getPostDTO> getPostsByUser(String nickname) {
        User user =  userRepository.findByNickname(nickname).orElseThrow(()->new GeneralException(ErrorStatus.USER_NOT_FOUND));
        List<Post> posts = postRepository.findAllByUserId(user.getId());

        return PostConverter.toPostListDto(posts);
    }

    @Override
    public List<PostResponseDTO.getPostDTO> getPostsByStock(String name) {
        List<Post> posts = postRepository.findAllByHashtag(name);
        return PostConverter.toPostListDto(posts);
    }

    @Transactional
    @Override
    public void deletePost(String userid, Long id) {
        User user = userRepository.findByUserId(userid).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        Post post = postRepository.findById(id).orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        if (!post.getUser().getUserId().equals(user.getUserId())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }
        post.markAsDeleted();
        postRepository.save(post);
    }

    @Transactional
    @Override
    public void editPost(String userid, Long id, PostRequestDTO.EditPostDTO editPostDTO) {
        User user = userRepository.findByUserId(userid).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        Post post = postRepository.findById(id).orElseThrow(() -> new GeneralException(ErrorStatus.POST_NOT_FOUND));
        if (!post.getUser().getUserId().equals(user.getUserId())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }

        Post updatedPost = PostConverter.toPostEntity(user, editPostDTO, post, s3Service);
        postRepository.save(updatedPost);

    }

    @Transactional
    @Override
    public void addLikes(String userid, Long id) {
        User user = userRepository.findByUserId(userid).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
    Post post = postRepository.findById(id).orElseThrow(()->new GeneralException(ErrorStatus.POST_NOT_FOUND));
    PostLike postLikeEntity = PostConverter.toPostLikeEntity(user, post);
        postLikeRepository.save(postLikeEntity);
    }

    @Transactional
    @Override
    public void deleteLikes(String userid, Long id) {
        User user = userRepository.findByUserId(userid).orElseThrow(()->new GeneralException(ErrorStatus.USER_NOT_FOUND));
        PostLike postLike = postLikeRepository.findById(id).orElseThrow(()->new GeneralException(ErrorStatus._BAD_REQUEST));
        if (!postLike.getUser().getUserId().equals(user.getUserId())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }
        postLikeRepository.deleteById(id);

    }

    @Transactional
    @Override
    public void addScrap(String userid, Long id) {
        User user = userRepository.findByUserId(userid).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        Post post = postRepository.findById(id).orElseThrow(()->new GeneralException(ErrorStatus.POST_NOT_FOUND));
        PostScrap postScrapEntity = PostConverter.toPostScrapEntity(user, post);
        postScrapRepository.save(postScrapEntity);

    }

    @Transactional
    @Override
    public void deleteScrap(String userid, Long id) {
        User user = userRepository.findByUserId(userid).orElseThrow(()->new GeneralException(ErrorStatus.USER_NOT_FOUND));
        PostScrap postScrap = postScrapRepository.findById(id).orElseThrow(()->new GeneralException(ErrorStatus._BAD_REQUEST));
        if (!postScrap.getUser().getUserId().equals(user.getUserId())) {
            throw new GeneralException(ErrorStatus._FORBIDDEN);
        }
        postScrapRepository.deleteById(id);
    }

    @Override
    public Long getLikeByUser(String userid, Long id) {
        User user = userRepository.findByUserId(userid).orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));
        List<PostLike> postLikes = postLikeRepository.findAllByPostId(id);

        PostLike userPostLike = postLikes.stream()
                .filter(postLike -> postLike.getUser().getId().equals(user.getId()))
                .findFirst()
                .orElse(null);
        Long likeId = userPostLike != null ? userPostLike.getId() : null;


        return likeId;
    }

    @Override
    @Transactional
    public PostResponseDTO.CreatePostResponseDTO createPost(PostRequestDTO.CreatePostDTO createPostDTO) {
        User user = userRepository.findByUserId(createPostDTO.getUserId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 📌 먼저 Post 객체 생성 및 저장
        Post post = Post.builder()
                .user(user)
                .content(createPostDTO.getContent())
                .hashtag(createPostDTO.getHashtag())
                .deleted(false)
                .build();

        String key = "stocks:popular";
        String hashtag = createPostDTO.getHashtag();
        Double score = redisTemplate.opsForZSet().score(key, hashtag);

        if (score != null) {
            redisTemplate.opsForZSet().incrementScore(key, hashtag, 1);
        }
        postRepository.save(post);

        String imageUrl = null;
        if (createPostDTO.getFile() != null && !createPostDTO.getFile().isEmpty()) {
            File file = s3Service.setPostImage(createPostDTO.getFile(), post);
            imageUrl = file.getUrl();
        }

        if (post.getHashtag() != null && !post.getHashtag().isEmpty()) {
            CheckStockEvent checkStockEvent = CheckStockEvent.builder()
                    .postId(post.getId())
                    .hashtag(post.getHashtag())
                    .userId(user.getId())
                    .correlationId(UUID.randomUUID().toString())
                    .build();
            kafkaTemplate.send("community.check-stock-topic", checkStockEvent);
            System.out.println(post.getHashtag()+"  : community -> stock으로 검증 publish");
        }

        return PostConverter.toPostResponseDTO(post);
    }


    @Override
    @Transactional
    public void finalizePost(Long postId, Long sentimentScore) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            Sentiment sentiment = Sentiment.builder()
                    .post(post)
                    .sentimentScore(sentimentScore)
                    .build();

//            Long analyzedSentimentScore = sentimentService.analyzeSentiment(post.getContent());
//            Sentiment sentiment = Sentiment.builder()
//                    .post(post)
//                    .sentimentScore(analyzedSentimentScore)
//                    .build();

            sentimentRepository.save(sentiment);
//            post.setFinalized(true); // 최종 상태 플래그
            postRepository.save(post);
            System.out.println("***** 글 작성 완료. 감정 점수 부여 완료");
        } else {
            throw new RuntimeException("***** 글 작성 실패");
        }
    }

    @Override
    @Transactional
    public void rollbackPost(Long postId, String reason) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            postRepository.delete(post);
            System.out.println("DB에서 글 삭제 postId: " + postId);
        } else {
            throw new RuntimeException("글을 찾을 수 없습니다. 글 작성 취소");
        }
    }


}
