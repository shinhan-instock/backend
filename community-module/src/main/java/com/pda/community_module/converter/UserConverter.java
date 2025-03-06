package com.pda.community_module.converter;

import com.pda.community_module.domain.Post;
import com.pda.community_module.domain.User;
import com.pda.community_module.domain.mapping.PostScrap;
import com.pda.community_module.domain.mapping.UserFollows;
import com.pda.community_module.web.dto.UserResponseDTO;

import java.util.stream.Collectors;
import java.util.List;

public class UserConverter {
    public static UserResponseDTO.getUserDTO toUserResponseDTO(User user) {
        return new UserResponseDTO.getUserDTO(
                user.getUserId(),
                user.getNickname(),
                user.getImageUrl(),
                user.getIntroduction()
        );
    }

    public static UserResponseDTO.getUserInfoDTO toUserDetailResponseDTO(User user) {
        return new UserResponseDTO.getUserInfoDTO(
                user.getUserId(),
                user.getName(),
                user.getNickname(),
                user.getImageUrl(),
                user.getIntroduction()
        );
    }

    public static List<UserResponseDTO.getUserDTO> toUserResponseDTOList(List<User> users) {
        return users.stream()
                .map(UserConverter::toUserResponseDTO)
                .collect(Collectors.toList());
    }

    public static UserFollows toUserFollowsEntity(User follower, User following) {
        return UserFollows.builder()
                .follower(follower)
                .following(following)
                .build();
    }
}
