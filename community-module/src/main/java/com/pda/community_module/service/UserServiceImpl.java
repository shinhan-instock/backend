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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final UserFollowsRepository userFollowsRepository;
    private final S3Service s3Service;

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

        // 닉네임이 null이 아닐 때만 검사하도록 수정
        Optional.ofNullable(requestDTO.getNickname())
                .filter(nickname -> !nickname.equals(user.getNickname()))  // 닉네임이 변경된 경우만
                .filter(userRepository::existsByNickname)  // 닉네임이 중복된 경우
                .ifPresent(nickname -> { throw new GeneralException(ErrorStatus.DUPLICATE_NICKNAME); });

        // 기존 값 유지, 변경된 값만 업데이트
        if (!user.getName().equals(requestDTO.getName())) {
            user.setName(requestDTO.getName());
        }
        if (!user.getNickname().equals(requestDTO.getNickname())) {
            user.setNickname(requestDTO.getNickname());
        }
        if (!user.getIntroduction().equals(requestDTO.getIntroduction())) {
            user.setIntroduction(requestDTO.getIntroduction());
        }

        // 이미지 변경 처리
        if (requestDTO.getImage() != null && !requestDTO.getImage().isEmpty()) {
            s3Service.setUserImage(requestDTO.getImage(), user);
        }
        userRepository.save(user);
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

        userFollowsRepository.findByFollowerIdAndFollowingId(user.getId(), followingUser.getId())
                .ifPresent(f -> {throw new GeneralException(ErrorStatus.ALREADY_FOLLOWING);});

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

    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO.UserRealPKResponseDto getUserByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.USER_NOT_FOUND));

        // 필요한 경우 추가적인 필드를 매핑
        return UserConverter.getUserRealPK(user);
    }

}
