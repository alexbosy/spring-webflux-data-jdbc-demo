package io.cryptorush.userservice.graphql.mapper;

import io.cryptorush.userservice.domain.user.User;
import io.cryptorush.userservice.graphql.dto.SystemUserGraphQlInputDTO;
import io.cryptorush.userservice.graphql.dto.UserGraphQlDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserGqlMapper {

    UserGraphQlDTO toGraphQlDTO(User User);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    User toUser(SystemUserGraphQlInputDTO dto);
}
