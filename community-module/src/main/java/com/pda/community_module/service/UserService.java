package com.pda.community_module.service;

import com.pda.community_module.web.dto.UserRequestDTO;
import com.pda.community_module.web.dto.UserResponseDTO;

import java.util.List;

public interface UserService {
    UserResponseDTO.getUserDTO login(UserRequestDTO.LoginUserDTO requestDTO);
    UserResponseDTO.getUserInfoDTO getUserInfo(String userId);
    void updateUser(String userId, UserRequestDTO.UpdateUserDTO requestDTO);
    List<UserResponseDTO.getUserDTO> searchUser(String keyword);
    List<UserResponseDTO.getUserDTO> getFollowList(String nickname);
    void doFollow(String userId, String nickname);
    void unFollow(String userId, String nickname);
    UserResponseDTO.UserRealPKResponseDto getUserByUserId(String userId);
}
