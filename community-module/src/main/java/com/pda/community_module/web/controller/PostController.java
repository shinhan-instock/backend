package com.pda.community_module.web.controller;


import com.pda.community_module.domain.User;
import com.pda.community_module.service.PostService;
import com.pda.core_module.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    // 조건에 맞는 게시글 전체 리스트 보기
    @GetMapping("/")
    @Operation(summary = "전체 게시글 리스트 보기", description = "게시글 팔로잉, 인기, 스크랩, 기본(최신순) 보기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse<?> getPosts(@RequestParam Boolean following,
                                   @RequestParam Boolean popular,
                                   @RequestParam Boolean scrap,
                                   @RequestParam Long user_id){

        return ApiResponse.onSuccess( postService.getPosts(following, popular, scrap, user_id));
    }

    // 개별 게시글 보기
    @GetMapping("/{postId}")
    @Operation(summary = "게시글 개별 보기", description = "게시글 팔로잉, 인기, 스크랩, 기본(최신순) 보기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse<?> getPostsById( @PathVariable Long postId){
        return  ApiResponse.onSuccess(postService.getPostById(postId));

    }

    // 개별 게시글 삭제
    @DeleteMapping("/{id}")
    @Operation(summary = "개별 게시글 삭제", description = "개별 게시글을 삭제하기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse deletePost(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id){
        Long userid = Long.valueOf(authorizationHeader.replace("Bearer ", ""));
        postService.deletePost(userid, id);
        return ApiResponse.onSuccess(null);
    }

    // 나의 게시글 전체 보기
    @GetMapping("/my")
    @Operation(summary = "나의 게시글 보기", description = "내가 작성한 게시글 리스트 보기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse<?> getMyPosts(){

        return ApiResponse.onSuccess(postService.getMyPosts());
    }


    //사용자별 게시글 전체 보기
    @PostMapping("/user")
    @Operation(summary = "사용자별 게시글 전체 보기", description = "특정 사용자가 작성한 게시글 리스트 보기")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse<?> getPostsByUser(

        @RequestBody @Schema(description = "사용자 닉네임", example = "{\"nickname\": \"string\"}") Map<String, String> request) {
        log.info("request={}", request);
        String nickname = request.get("nickname");
        return ApiResponse.onSuccess(postService.getPostsByUser(nickname));
    }


    // 종목별 게시글 전체 보기
    @GetMapping("/stocks/{name}")
    @Operation(summary = "종목별 게시글 전체 보기", description = "특정 종목과 관련된 게시글 리스트 보기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse<?> getPostsByStock(@PathVariable String name){
        return  ApiResponse.onSuccess(postService.getPostsByStock(name));


    }

}
