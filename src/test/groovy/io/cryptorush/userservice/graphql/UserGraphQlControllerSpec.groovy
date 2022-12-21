package io.cryptorush.userservice.graphql

import io.cryptorush.userservice.domain.user.User
import io.cryptorush.userservice.domain.user.UserService
import io.cryptorush.userservice.domain.user.UserType
import io.cryptorush.userservice.graphql.dto.SystemUserGraphQlInputDTO
import io.cryptorush.userservice.graphql.mapper.UserGqlMapperImpl
import reactor.core.scheduler.Schedulers
import spock.lang.Specification

class UserGraphQlControllerSpec extends Specification {

    def scheduler = Schedulers.immediate()
    def userGqlMapper = new UserGqlMapperImpl()
    def userService = Mock(UserService)

    def controller = new UserGraphQlController(scheduler, userService, userGqlMapper)

    def id = 100L
    def user = new User(id: id, login: "some login", name: "some name", surname: "some surname", email:
            "some email", type: UserType.MANAGER)

    def "get all users"() {
        given:
        userService.getAllSystemUsers(0, 10) >> [user]

        when:
        def res = controller.allUsers(0, 10).block()

        then:
        def dto = res.first()
        dto.id() == id
        dto.login() == "some login"
        dto.name() == "some name"
        dto.surname() == "some surname"
        dto.email() == "some email"
        dto.type() == UserType.MANAGER
    }

    def "get user by id"() {
        given:
        userService.getById(id) >> user

        when:
        def dto = controller.userById(id).block()

        then:
        dto.id() == id
        dto.login() == "some login"
        dto.name() == "some name"
        dto.surname() == "some surname"
        dto.email() == "some email"
        dto.type() == UserType.MANAGER
    }

    def "create new system user"() {
        given:
        def dto = new SystemUserGraphQlInputDTO(login: "login", name: "name", surname: "surname", email: "email",
                password: "pass", type: UserType.ADMIN)

        when:
        def res = controller.createSystemUser(dto).block()

        then:
        1 * userService.createSystemUser({ User u ->
            u.login == "login" &&
                    u.name == "name" &&
                    u.surname == "surname" &&
                    u.email == "email" &&
                    u.password == "pass" &&
                    u.type == UserType.ADMIN
        }) >> new User(id: id, login: "login", name: "name", surname: "surname", email: "email",
                password: "pass", type: UserType.ADMIN)
        res.id() == id
        res.login() == "login"
        res.name() == "name"
        res.surname() == "surname"
        res.email() == "email"
        res.type() == UserType.ADMIN
    }
}
