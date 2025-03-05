package com.pda.community_module.service;

import com.pda.community_module.converter.UserConverter;
import com.pda.community_module.domain.User;
import com.pda.community_module.repository.UserRepository;
import com.pda.community_module.web.dto.UserRequestDTO;
import com.pda.community_module.web.dto.UserResponseDTO;
import com.pda.core_module.apiPayload.GeneralException;
import com.pda.core_module.apiPayload.code.status.ErrorStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserResponseDTO.getUserDTO login(UserRequestDTO.LoginUserDTO requestDTO) {
        User user = userRepository.findUserByUserIdAndPassword(requestDTO.getUserId(), requestDTO.getPassword())
                .orElseThrow(() -> new GeneralException(ErrorStatus.INVALID_CREDENTIALS));
        return UserConverter.toUserResponseDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO.getUserInfoDTO getUserInfo(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._FORBIDDEN));
        return UserConverter.toUserDetailResponseDTO(user);
    }

    @Override
    @Transactional
    public void updateUser(String userId, UserRequestDTO.UpdateUserDTO requestDTO) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._FORBIDDEN));

        Optional.of(requestDTO.getNickname())
                .filter(nickname -> !user.getNickname().equals(nickname))  // 닉네임이 변경된 경우만
                .filter(userRepository::existsByNickname)  // 닉네임이 중복된 경우
                .ifPresent(nickname -> { throw new GeneralException(ErrorStatus.DUPLICATE_NICKNAME); });

        user.setName(requestDTO.getName());
        user.setNickname(requestDTO.getNickname());
        user.setImageUrl(requestDTO.getImageUrl());
        user.setIntroduction(requestDTO.getIntroduction());
    }

    @Override
    @Transactional
    public List<UserResponseDTO.getUserDTO> searchUser(String userId, String keyword) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._FORBIDDEN));

        List<User> users = userRepository.findByNicknameStartingWith(keyword);
        return UserConverter.toUserResponseDTOList(users);
    }

//    @Override
//    @Transactional
//    public void doFollow(String userId, String nickname) {
//        User user = userRepository.findByUserId(userId)
//                .orElseThrow(() -> new GeneralException(ErrorStatus._FORBIDDEN));
//
//        userRepository.findByUserNickname(nickname)
//                .orElseThrow()
//    }
}
