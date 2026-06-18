package com.irinakomarchenko.messagingplatform.user.mapper;

import com.irinakomarchenko.messagingplatform.user.config.CentralMapperConfig;
import com.irinakomarchenko.messagingplatform.user.dto.response.UserResponse;
import com.irinakomarchenko.messagingplatform.user.dto.response.UserSearchResponse;
import com.irinakomarchenko.messagingplatform.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface UserMapper {

    UserResponse toUserResponse(User user);

    UserSearchResponse toUserSearchResponse(User user);
}
