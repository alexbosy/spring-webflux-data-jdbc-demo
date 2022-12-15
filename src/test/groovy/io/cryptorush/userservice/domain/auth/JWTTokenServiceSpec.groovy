package io.cryptorush.userservice.domain.auth

import io.cryptorush.userservice.domain.user.UserType
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import spock.lang.Specification

import java.time.Clock
import java.time.Instant

class JWTTokenServiceSpec extends Specification {

    def encoder = Mock(JwtEncoder)
    def service = new JWTTokenService(encoder)

    def clock = Mock(Clock)
    def currentTime = Instant.now()

    def setup() {
        service.setExpirationTime(100L)
        service.setClock(clock)
    }

    def "generate JWT token"() {
        given:
        def login = "some login"
        clock.instant() >> currentTime
        def resultJWT = Jwt.withTokenValue("some JWT token")
                .header("header", "value")
                .claim("scope", UserType.ADMIN)
                .build()

        when:
        def res = service.generateToken(login, UserType.ADMIN)

        then:
        1 * encoder.encode({ JwtEncoderParameters p ->
            p.claims.subject == login &&
                    p.claims.getClaim("scope") == UserType.ADMIN &&
                    p.claims.issuedAt == currentTime &&
                    p.claims.expiresAt == currentTime + 100L
        } as JwtEncoderParameters) >> resultJWT
        res == "some JWT token"
    }
}
