package share.resume.com.services;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.resume.com.controllers.dto.request.LoginUserRequestBody;
import share.resume.com.security.dto.UserDetailsDto;
import share.resume.com.security.services.JwtService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public Map<String, String> login(LoginUserRequestBody loginUserRequestBody) {
        UserDetailsDto userDetailsDto = userService.getUserByEmail(loginUserRequestBody.getEmail());
        if (!passwordEncoder.matches(loginUserRequestBody.getPassword(), userDetailsDto.getPassword())) {
            throw new BadCredentialsException("Wrong password");
        }
        String accessToken = jwtService.generateAccessToken(userDetailsDto);
        String refreshToken = jwtService.generateRefreshToken(userDetailsDto.getId(), userDetailsDto.getEmail());
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken, "role", userDetailsDto.getRole().name());
    }

    @Transactional
    public Map<String, String> refreshToken(String refreshToken) {
        Claims claims = jwtService.validateToken(refreshToken);
        String email = claims.get("email", String.class);
        UserDetailsDto userDetailsDto = userService.getUserByEmail(email);
        String accessToken = jwtService.generateAccessToken(userDetailsDto);
        return Map.of("accessToken", accessToken);
    }
}
