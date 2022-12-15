package io.cryptorush.userservice.rest.auth

import io.cryptorush.userservice.domain.auth.AuthService
import io.cryptorush.userservice.rest.auth.dto.AuthRequestDTO
import reactor.core.scheduler.Schedulers
import spock.lang.Specification

class AuthControllerSpec extends Specification {

    def scheduler = Schedulers.immediate()
    def authService = Mock(AuthService)

    def controller = new AuthController(scheduler, authService)

    def "POST /auth - authenticate and get JWT token"() {
        given:
        def login = "some login"
        def password = "some password"
        def requestDTO = new AuthRequestDTO(login: login, password: password)
        authService.authenticate(login, password) >> "JWT token"

        when:
        def res = controller.auth(requestDTO).block()

        then:
        res.token() == "JWT token"
    }
}
