package io.cryptorush.userservice.graphql.mapper;

import io.cryptorush.userservice.domain.user.User;
import io.cryptorush.userservice.graphql.dto.UserGqlDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserGqlMapper {

    UserGqlDTO toGqlDTO(User User);
}
