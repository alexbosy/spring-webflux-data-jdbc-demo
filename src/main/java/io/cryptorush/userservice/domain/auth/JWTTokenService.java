package io.cryptorush.userservice.domain.auth;

import io.cryptorush.userservice.domain.user.UserType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class JWTTokenService implements TokenService {

    @Value("${security.jwt.expiration-time-in-sec}")
    private long expirationTime;

    private final JwtEncoder encoder;

    public String generateToken(String login, UserType type) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationTime))
                .subject(login)
                .claim("scope", type)
                .build();
        JwtEncoderParameters parameters = JwtEncoderParameters.from(claims);
        Jwt jwt = encoder.encode(parameters);
        return jwt.getTokenValue();
    }
}
