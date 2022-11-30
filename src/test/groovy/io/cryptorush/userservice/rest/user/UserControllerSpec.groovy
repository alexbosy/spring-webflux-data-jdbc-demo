package io.cryptorush.userservice.rest.user

import io.cryptorush.userservice.domain.user.User
import io.cryptorush.userservice.domain.user.UserService
import io.cryptorush.userservice.domain.user.UserType
import io.cryptorush.userservice.rest.user.dto.UserCreationRequestDTO
import io.cryptorush.userservice.rest.user.dto.UserUpdateRequestDTO
import org.springframework.http.HttpStatus
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

    def "POST /user - create new user"() {
        given:
        def userRequestDTO = new UserCreationRequestDTO(login: login, name: name, surname: surname, email: email, password:
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

    def "DELETE /user/{id} - hard delete user by id"() {
        given:
        def id = 100L

        when:
        def result = controller.deleteUser(id).block()

        then:
        1 * userService.deleteById(id)
        result.statusCode == HttpStatus.NO_CONTENT
    }

    def "PUT /user/{id} - update user by id"() {
        given:
        def id = 666L
        def userDTO = new UserUpdateRequestDTO(login: login, name: name, surname: surname, email: email, type: type)

        when:
        def result = controller.updateUser(id, userDTO).block()

        then:
        1 * userService.updateUser({ User user ->
            user.id == id
        } as User) >> new User(id: 666L, login: login, name: name, surname: surname, email: email, type: UserType.ADMIN)
        result.id == id
        result.login == login
        result.name == name
        result.surname == surname
        result.email == email
        result.type == UserType.ADMIN
    }

    def "GET /users?offset={offset}&limit={limit} - get users list with specified offset and limit"() {
        given:
        def offset = 0
        def limit = 1

        when:
        def result = controller.getUsers(offset, limit).block()

        then:
        1 * userService.getAllUsers(offset, limit) >> [new User(id: 666L, login: login, name: name,
                surname: surname, email: email, type: UserType.MANAGER)]
        result.size() == 1
        def userDTO = result[0]
        userDTO.id == 666L
        userDTO.login == login
        userDTO.name == name
        userDTO.surname == surname
        userDTO.email == email
        userDTO.type == UserType.MANAGER
    }
}
