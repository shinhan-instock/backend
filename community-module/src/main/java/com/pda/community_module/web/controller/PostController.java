package com.pda.community_module.web.controller;


import com.pda.community_module.service.PostService;
import com.pda.community_module.web.dto.PostRequestDTO;
import com.pda.core_module.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

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
    public ApiResponse<?> getPosts(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam Boolean following,
            @RequestParam Boolean popular,
            @RequestParam Boolean scrap){

        String userid;
        if (authorizationHeader==null) {
            userid = null;
        } else {
            userid = authorizationHeader.replace("Bearer ", "");
        }

        return ApiResponse.onSuccess( postService.getPosts(following, popular, scrap, userid));
    }

    // 개별 게시글 보기
    @GetMapping("/{postId}")
    @Operation(summary = "게시글 개별 보기", description = "게시글 팔로잉, 인기, 스크랩, 기본(최신순) 보기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse<?> getPostsById( @PathVariable Long postId,
                                        @RequestHeader(value = "Authorization", required = false) String authorizationHeader){
        String userid;
        if (authorizationHeader==null) {
            userid = null;
        } else {
            userid = authorizationHeader.replace("Bearer ", "");
        }
        return  ApiResponse.onSuccess(postService.getPostById(postId,userid));

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
        String userid = authorizationHeader.replace("Bearer ", "");
        postService.deletePost(userid, id);
        return ApiResponse.onSuccess(null);
    }

    // 개별 게시글 수정
    @PutMapping(value="/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "개별 게시글 수정", description = "개별 게시글을 수정하기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse editPost(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable Long id, @ModelAttribute PostRequestDTO.EditPostDTO editPostDTO ){
        String userid = authorizationHeader.replace("Bearer ", "");
        postService.editPost(userid, id, editPostDTO);
        return ApiResponse.onSuccess(null);
    }

    // 나의 게시글 전체 보기
    @GetMapping("/my")
    @Operation(summary = "나의 게시글 보기", description = "내가 작성한 게시글 리스트 보기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse<?> getMyPosts( @RequestHeader("Authorization") String authorizationHeader){
        String userid = authorizationHeader.replace("Bearer ", "");
        return ApiResponse.onSuccess(postService.getMyPosts(userid));
    }


    //사용자별 게시글 전체 보기
    @PostMapping("/user")
    @Operation(summary = "사용자별 게시글 전체 보기", description = "특정 사용자가 작성한 게시글 리스트 보기")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse<?> getPostsByUser(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,

        @RequestBody @Schema(description = "사용자 닉네임", example = "{\"nickname\": \"string\"}") Map<String, String> request) {
        log.info("request={}", request);
        String userid;
        if (authorizationHeader==null) {
            userid = null;
        } else {
            userid = authorizationHeader.replace("Bearer ", "");
        }
        String nickname = request.get("nickname");
        return ApiResponse.onSuccess(postService.getPostsByUser(nickname, userid));
    }


    // 종목별 게시글 전체 보기
    @GetMapping("/stocks/{name}")
    @Operation(summary = "종목별 게시글 전체 보기", description = "특정 종목과 관련된 게시글 리스트 보기")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse<?> getPostsByStock(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,@PathVariable String name){
        String userid;
        if (authorizationHeader==null) {
            userid = null;
        } else {
            userid = authorizationHeader.replace("Bearer ", "");
        }
        return  ApiResponse.onSuccess(postService.getPostsByStock(name, userid));


    }

    //게시글 좋아요 등록
    @PostMapping("/{id}/like")
    @Operation(summary = "게시글 좋아요 등록", description = "특정 게시글에 대한 좋아요를 등록")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse<?> addLikes(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id){
        String userid = authorizationHeader.replace("Bearer ", "");
        System.out.println("auth"+authorizationHeader);
        postService.addLikes(userid, id);
        return ApiResponse.onSuccess(null);

    }


    //게시글 좋아요 삭제
    @DeleteMapping("/{id}/like")
    @Operation(summary = "게시글 좋아요 삭제", description = "특정 게시글에 대한 좋아요를 삭제")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse<?> deleteLikes(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id){
        String userid = authorizationHeader.replace("Bearer ", "");
        postService.deleteLikes(userid, id);
        return ApiResponse.onSuccess(null);

    }


    //게시글 좋아요 조회
    @GetMapping("/{id}/like")
    @Operation(summary = "게시글 좋아요 조회", description = "로그인한 사용자가 특정 게시글에 좋아요를 눌렀는지 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse<?> getLikeByUser(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id){
        String userid = authorizationHeader.replace("Bearer ", "");

        return ApiResponse.onSuccess(postService.getLikeByUser(userid, id));

    }



    //게시글 스크랩 등록
    @PostMapping("/{id}/scrap")
    @Operation(summary = "게시글 스크랩 등록", description = "특정 게시글에 대한 스크랩을 등록")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse<?> addScrap(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id){
        String userid = authorizationHeader.replace("Bearer ", "");

        return ApiResponse.onSuccess( postService.addScrap(userid, id));

    }


    //게시글 스크랩 삭제
    @DeleteMapping("{id}/scrap")
    @Operation(summary = "게시글 스크랩 삭제", description = "특정 게시글에 대한 스크랩을 삭제")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse<?> deleteScrap(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long id){
        String userid = authorizationHeader.replace("Bearer ", "");
        postService.deleteScrap(userid, id);
        return ApiResponse.onSuccess(null);

    }

    @PostMapping(value = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "게시글 등록", description = "이미지를 포함한 게시글 등록")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "사용자를 찾을 수 없습니다.")
    })
    public ApiResponse<?> addPost(
            @ModelAttribute PostRequestDTO.CreatePostDTO createPostDTO) {  // DTO로 요청 받음

        return ApiResponse.onSuccess(postService.createPost(createPostDTO));
    }


}
