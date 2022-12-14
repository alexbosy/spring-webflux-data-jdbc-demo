package io.cryptorush.userservice.domain.auth;

import io.cryptorush.userservice.domain.user.UserAuthModel;
import io.cryptorush.userservice.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class JWTAuthService implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public String authenticate(String login, String suppliedPassword) {
        UserAuthModel userAuthModel = userRepository.findUserAuthModelByLogin(login).orElseThrow(AuthException::new);
        if (!passwordEncoder.matches(suppliedPassword, userAuthModel.password())) {
            throw new AuthException();
        }
        return tokenService.generateToken(login, userAuthModel.type());
    }
}
