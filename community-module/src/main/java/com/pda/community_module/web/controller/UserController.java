package com.pda.community_module.web.controller;

import com.pda.community_module.service.AccountService;
import com.pda.community_module.service.UserService;
import com.pda.community_module.web.dto.*;
import com.pda.core_module.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AccountService accountService;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "아이디, 비밀번호를 입력해 로그인을 한다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER2001", description = "로그인에 성공했습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "AUTH4001", description = "아이디 또는 비밀번호가 올바르지 않습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    public ApiResponse<?> login(@RequestBody UserRequestDTO.LoginUserDTO requestDTO) {
        return ApiResponse.onSuccess(userService.login(requestDTO));
    }

    @PostMapping("")
    @Operation(summary = "내 계정보기", description = "로그인한 사용자의 회원아이디, 이름, 닉네임, 프로필이미지, 한줄소개를 본다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "내 계정 정보 가져오기 성공", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON4003", description = "토큰 누락 또는 유효하지 않음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<?> getUserInfo(@RequestHeader("Authorization") String authorizationHeader) {
        String userId = String.valueOf(authorizationHeader.replace("Bearer ", ""));
        return ApiResponse.onSuccess(userService.getUserInfo(userId));
    }

    @PutMapping(value = "",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "내 계정수정", description = "로그인한 사용자의 프로필 이미지, 닉네임, 한줄소개, 이름을 수정한다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "내 계정 정보 수정 성공", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON4003", description = "토큰 누락 또는 유효하지 않음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4009", description = "이미 사용 중인 닉네임입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<?> updateUser(@RequestHeader("Authorization") String authorizationHeader,
                                     @ModelAttribute UserRequestDTO.UpdateUserDTO requestDTO) {
        String userId = String.valueOf(authorizationHeader.replace("Bearer ", ""));
        userService.updateUser(userId, requestDTO);
        return ApiResponse.onSuccess(null);
    }


    @GetMapping("/search")
    @Operation(summary = "회원 검색", description = "상단 검색창에서 회원을 검색한다. 검색된 리스트가 보여진다(ex a입력시 a로 시작하는 닉네임)")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "회원 검색 성공", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON4003", description = "토큰 누락 또는 유효하지 않음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<?> searchUser(@RequestParam(value = "keyword") String keyword) {

        return ApiResponse.onSuccess(userService.searchUser(keyword));
    }

    @GetMapping("/follow")
    @Operation(summary = "팔로우리스트보기", description = "내가 팔로우하는 사람들 리스트를 보던가, 남이 팔로우하는 사람들 리스트를 확인한다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "팔로우 리스트 반환 성공", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON4003", description = "토큰 누락 또는 유효하지 않음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "확인하려는 유저가 존재하지 않습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<?> getFollowList(
                                   @RequestParam(value = "following") String nickname) {

        return ApiResponse.onSuccess(userService.getFollowList(nickname));
    }

    @PostMapping("/follow")
    @Operation(summary = "팔로우하기", description = "상대방을 팔로우한다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "팔로우 성공", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON4003", description = "토큰 누락 또는 유효하지 않음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "팔로우하려는 유저가 존재하지 않습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4010", description = "이미 팔로우한 사용자입니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<?> doFollow(@RequestHeader("Authorization") String authorizationHeader,
                                     @RequestParam(value = "Nickname") String nickname) {
        String userId = String.valueOf(authorizationHeader.replace("Bearer ", ""));
        userService.doFollow(userId, nickname);
        return ApiResponse.onSuccess(null);
    }

    @DeleteMapping("/follow")
    @Operation(summary = "팔로우취소", description = "상대방 팔로우 취소")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "팔로우 취소 완료", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON4003", description = "토큰 누락 또는 유효하지 않음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "팔로우 취소 유저가 존재하지 않습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4008", description = "팔로우 정보가 존재하지 않습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<?> unFollow(@RequestHeader("Authorization") String authorizationHeader,
                                   @RequestParam(value = "Nickname") String nickname) {
        String userId = String.valueOf(authorizationHeader.replace("Bearer ", ""));
        userService.unFollow(userId, nickname);
        return ApiResponse.onSuccess(null);
    }
    @GetMapping("/getPK/{userId}")
    public ResponseEntity<UserResponseDTO.UserRealPKResponseDto> getUserByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserByUserId(userId));
    }

    @PostMapping("/account")
    public ResponseEntity<List<AccountResponseDTO>> getUsersStock(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody AccountRequestDTO accountRequestDTO) {
        String myUserId = String.valueOf(authorizationHeader.replace("Bearer ", ""));
        String userId = accountRequestDTO.getUserId();

        List<AccountResponseDTO> response = accountService.getAccount(myUserId, userId);
        return ResponseEntity.ok(response);

    }


    @GetMapping("/influencer")
    @Operation(summary = "인플루언서 조회", description = "인플루언서 리스트로 조회한다")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "팔로우 성공", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON4003", description = "토큰 누락 또는 유효하지 않음", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4001", description = "팔로우하려는 유저가 존재하지 않습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER4010", description = "이미 팔로우한 사용자입니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<?> getInfluencerList() {
        return ApiResponse.onSuccess(userService.getInfluencerList());
    }

}