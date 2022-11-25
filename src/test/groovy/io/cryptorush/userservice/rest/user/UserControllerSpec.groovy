package io.cryptorush.userservice.rest.user

import io.cryptorush.userservice.domain.user.User
import io.cryptorush.userservice.domain.user.UserService
import io.cryptorush.userservice.domain.user.UserType
import io.cryptorush.userservice.rest.user.dto.UserRequestDTO
import reactor.core.scheduler.Schedulers
import spock.lang.Specification

class UserControllerSpec extends Specification {

    def scheduler = Schedulers.immediate()
    def userService = Mock(UserService)
    def controller = new UserController(scheduler, userService)

    def login = "login"
    def name = "name"
    def surname = "surname"
    def email = "email"
    def password = "password"
    def type = "ADMIN"

    def "POST /user - create user handler"() {
        given:
        def userRequestDTO = new UserRequestDTO(login: login, name: name, surname: surname, email: email, password:
                password, type: type)

        when:
        def result = controller.createUser(userRequestDTO).block()

        then:
        1 * userService.createSystemUser({ User u ->
            u.login == userRequestDTO.login &&
                    u.name == userRequestDTO.name &&
                    u.surname == userRequestDTO.surname &&
                    u.email == userRequestDTO.email &&
                    u.password == userRequestDTO.password &&
                    u.type == userRequestDTO.type
        } as User) >> new User(id: 12345L, login: login)
        result.id == 12345L
        result.login == login
    }

    def "GET /user/{id} - get user by id"() {
        given:
        def id = 100L

        when:
        def result = controller.getUser(id).block()

        then:
        1 * userService.getById(id) >> new User(id: id, login: login, name: name, surname: surname, email: email,
                password: "encrypted", type: UserType.MANAGER)
        result.id == id
        result.login == login
        result.name == name
        result.surname == surname
        result.email == email
        result.type == UserType.MANAGER
    }
}