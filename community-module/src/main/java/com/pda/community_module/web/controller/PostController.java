package com.pda.community_module.web.controller;


import com.pda.community_module.service.PostService;
import com.pda.community_module.service.WatchListService;
import com.pda.core_module.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    @GetMapping("/")
    @Operation(summary = "게시글 보기", description = "게시글 팔로잉, 인기, 스크랩, 기본(최신순) 보기")
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



}
