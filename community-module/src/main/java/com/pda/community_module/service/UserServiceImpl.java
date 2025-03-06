package com.pda.community_module.service;

import com.pda.community_module.converter.UserConverter;
import com.pda.community_module.domain.User;
import com.pda.community_module.domain.mapping.UserFollows;
import com.pda.community_module.repository.UserFollowsRepository;
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
    private final UserFollowsRepository userFollowsRepository;

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
    public List<UserResponseDTO.getUserDTO> searchUser(String keyword) {
        List<User> users = userRepository.findByNicknameStartingWith(keyword);
        return UserConverter.toUserResponseDTOList(users);
    }

    @Override
    @Transactional
    public List<UserResponseDTO.getUserDTO> getFollowList( String nickname) {


        User targetUser = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        List<User> users = userRepository.findByIdAndJoinUserFollows(targetUser.getId());
        return UserConverter.toUserResponseDTOList(users);
    }

    @Override
    @Transactional
    public void doFollow(String userId, String nickname) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._FORBIDDEN));

        User followingUser = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        UserFollows userFollowsEntity = UserConverter.toUserFollowsEntity(user, followingUser);
        userFollowsRepository.save(userFollowsEntity);
    }

    @Override
    @Transactional
    public void unFollow(String userId, String nickname) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._FORBIDDEN));

        User followingUser = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        UserFollows userFollowsEntity = userFollowsRepository.findByFollowerIdAndFollowingId(user.getId(), followingUser.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus.NO_FOLLOW_INFO));
        userFollowsRepository.delete(userFollowsEntity);
    }

}
