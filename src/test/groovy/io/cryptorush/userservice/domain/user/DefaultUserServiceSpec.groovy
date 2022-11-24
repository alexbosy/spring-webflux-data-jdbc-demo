package io.cryptorush.userservice.domain.user

import io.cryptorush.userservice.domain.user.validation.EmailIsTakenException
import io.cryptorush.userservice.domain.user.validation.InvalidUserTypeException
import io.cryptorush.userservice.domain.user.validation.LoginIsTakenException
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class DefaultUserServiceSpec extends Specification {

    def userRepository = Mock(UserRepository)
    def userValidator = new UserValidator(userRepository)
    def passwordEncoder = Mock(PasswordEncoder)

    def userService = new DefaultUserService(userRepository, userValidator, passwordEncoder)

    def "create new system user, success case"() {
        given:
        def login = "some login"
        def email = "some email"
        def password = "some password"
        def name = "some name"
        def surname = "some surname"
        def type = supportedType
        def user = new User(login: login, email: email, type: type, password: password, name: name, surname: surname)

        and: "specified login or email is not taken"
        userRepository.findByLoginOrEmail(login, email) >> Optional.empty()

        and:
        passwordEncoder.encode(password) >> "encrypted password"

        when:
        userService.createSystemUser(user)

        then:
        1 * userRepository.save({ User u ->
            u.password == "encrypted password" &&
                    u.login == login &&
                    u.name == name &&
                    u.surname == surname &&
                    u.email == email &&
                    u.type == type
        } as User) >> new User(id: 666L)

        where:
        supportedType << [UserType.ADMIN, UserType.MANAGER]
    }

    def "create new system user, error case with unsupported user type"() {
        given:
        def user = new User(type: UserType.CUSTOMER)

        when:
        userService.createSystemUser(user)

        then:
        def e = thrown(InvalidUserTypeException)
        e.message == "Forbidden user type"
        e.fieldName == "type"
    }

    def "create new system user, error case with taken login"() {
        given:
        def user = new User(login: "taken login", type: UserType.ADMIN)

        and: "specified login is taken"
        userRepository.findByLoginOrEmail("taken login", _) >> Optional.of(user)

        when:
        userService.createSystemUser(user)

        then:
        def e = thrown(LoginIsTakenException)
    }

    def "create new system user, error case with taken email"() {
        given:
        def user = new User(email: "taken email", type: UserType.ADMIN)

        and: "specified email is taken"
        userRepository.findByLoginOrEmail(_, "taken email",) >> Optional.of(new User(login: "some login", email:
                "taken email"))

        when:
        userService.createSystemUser(user)

        then:
        def e = thrown(EmailIsTakenException)
    }
}
