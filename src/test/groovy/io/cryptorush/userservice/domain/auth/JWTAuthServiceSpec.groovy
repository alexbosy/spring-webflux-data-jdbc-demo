package io.cryptorush.userservice.domain.auth

import io.cryptorush.userservice.domain.user.UserAuthModel
import io.cryptorush.userservice.domain.user.UserRepository
import io.cryptorush.userservice.domain.user.UserType
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

class JWTAuthServiceSpec extends Specification {

    def userRepository = Mock(UserRepository)
    def passwordEncoder = Mock(PasswordEncoder)
    def tokenService = Mock(TokenService)

    def service = new JWTAuthService(userRepository, passwordEncoder, tokenService)

    def "authenticate user by supplied login and password, success case"() {
        given:
        def login = "login"
        def suppliedPassword = "password"
        def encodedPass = "encoded password"
        userRepository.findUserAuthModelByLogin(login) >> Optional.of(new UserAuthModel(encodedPass, UserType.ADMIN))
        passwordEncoder.matches(suppliedPassword, encodedPass) >> true

        when:
        def result = service.authenticate(login, suppliedPassword)

        then:
        1 * tokenService.generateToken(login, UserType.ADMIN) >> "some JWT token"
        result == "some JWT token"
    }

    def "authenticate user by supplied login and password, error case, when login is wrong"() {
        given:
        userRepository.findUserAuthModelByLogin("wrong login") >> Optional.empty()

        when:
        def result = service.authenticate("wrong login", _ as String)

        then:
        def e = thrown(AuthException)
        e.message == "Wrong login or password"
    }

    def "authenticate user by supplied login and password, error case, when password is wrong"() {
        given:
        def login = "login"
        def suppliedPassword = "password"
        def encodedPass = "encoded password"
        userRepository.findUserAuthModelByLogin(login) >> Optional.of(new UserAuthModel(encodedPass, UserType.ADMIN))
        passwordEncoder.matches(suppliedPassword, encodedPass) >> false

        when:
        def result = service.authenticate(login, suppliedPassword)

        then:
        def e = thrown(AuthException)
        e.message == "Wrong login or password"
    }
}
