package share.resume.com.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import share.resume.com.controllers.dto.request.LoginUserRequestBody;
import share.resume.com.services.AuthService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginUserRequestBody loginUserRequestBody) {
        return new ResponseEntity<>(authService.login(loginUserRequestBody), HttpStatus.OK);
    }

    @PostMapping("/access-token")
    public ResponseEntity<Map<String, String>> getAccessToken(@RequestHeader String refreshToken) {
        return new ResponseEntity<>(authService.refreshToken(refreshToken), HttpStatus.OK);
    }
}
