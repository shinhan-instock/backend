package com.pda.community_module.converter;

import com.pda.community_module.domain.File;
import com.pda.community_module.domain.Post;
import com.pda.community_module.domain.User;
import com.pda.community_module.domain.mapping.PostScrap;
import com.pda.community_module.domain.mapping.UserFollows;
import com.pda.community_module.web.dto.UserResponseDTO;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.List;

public class UserConverter {
    public static UserResponseDTO.getUserDTO toUserResponseDTO(User user) {
        return new UserResponseDTO.getUserDTO(
                user.getUserId(),
                user.getNickname(),
                user.getFile().getUrl(),
                user.getIntroduction()
        );
    }

    public static UserResponseDTO.getUserInfoDTO toUserDetailResponseDTO(User user) {
        String imgUrl = null;
        Optional<File> userFileOptional = Optional.ofNullable(user.getFile());
        if (userFileOptional.isPresent()) {
            imgUrl = userFileOptional.get().getUrl();
        }
//        return new UserResponseDTO.getUserInfoDTO(
//                user.getUserId(),
//                user.getName(),
//                user.getNickname(),
//                user.getFile().getUrl(),
//                user.getIntroduction()
//        );
        return UserResponseDTO.getUserInfoDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .nickname(user.getNickname())
                .imageUrl(imgUrl)
                .introduction(user.getIntroduction())
                .build();
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

    public static UserResponseDTO.UserRealPKResponseDto getUserRealPK(User user) {
        return UserResponseDTO.UserRealPKResponseDto.builder()
                .id(user.getId())
                .build();
    }
}
