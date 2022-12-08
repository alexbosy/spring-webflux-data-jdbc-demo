package io.cryptorush.userservice.rest.user.mapper;

import io.cryptorush.userservice.domain.user.User;
import io.cryptorush.userservice.rest.user.dto.UserCreatedResponseDTO;
import io.cryptorush.userservice.rest.user.dto.UserCreationRequestDTO;
import io.cryptorush.userservice.rest.user.dto.UserFullResponseDTO;
import io.cryptorush.userservice.rest.user.dto.UserUpdateRequestDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SystemUserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    User toUser(UserCreationRequestDTO dto);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "customer", ignore = true)
    User toUser(long id, UserUpdateRequestDTO dto);

    UserCreatedResponseDTO toCreatedResponseDTO(User user);

    UserFullResponseDTO toFullResponseDTO(User user);
}
