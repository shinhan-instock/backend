package com.pda.community_module.service;

import com.pda.community_module.web.dto.AccountRequestDTO;
import com.pda.community_module.web.dto.UserRequestDTO;
import com.pda.community_module.web.dto.UserResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    UserResponseDTO.getUserDTO login(UserRequestDTO.LoginUserDTO requestDTO);
    UserResponseDTO.getUserInfoDTO getUserInfo(String userId);
    @Transactional
    void updateUser(String userId, UserRequestDTO.UpdateUserDTO requestDTO);
    List<UserResponseDTO.getUserDTO> searchUser(String keyword);
    List<UserResponseDTO.getUserDTO> getFollowList(String nickname);
    void doFollow(String userId, String nickname);
    void unFollow(String userId, String nickname);
    UserResponseDTO.UserRealPKResponseDto getUserByUserId(String userId);

    List<UserResponseDTO.getUserDTO> getInfluencerList();

    void changeAccountState(String userId);
}
