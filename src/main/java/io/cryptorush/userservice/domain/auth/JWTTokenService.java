package io.cryptorush.userservice.domain.auth;

import io.cryptorush.userservice.domain.user.UserType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class JWTTokenService implements TokenService {

    @Value("${security.jwt.expiration-time-in-sec}")
    private long expirationTime;

    private final JwtEncoder encoder;

    public JWTTokenService(JwtEncoder encoder) {
        this.encoder = encoder;
    }

    public String generateToken(String login, UserType type) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expirationTime))
                .subject(login)
                .claim("scope", type)
                .build();
        return encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
