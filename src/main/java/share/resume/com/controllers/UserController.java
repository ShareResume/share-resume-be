package share.resume.com.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import share.resume.com.controllers.dto.request.CreateUserRequestBody;
import share.resume.com.security.dto.UserDetailsDto;
import share.resume.com.services.UserDataDeletionService;
import share.resume.com.services.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserDataDeletionService userDataDeletionService;

    @PostMapping
    public void createUser(@RequestBody @Valid CreateUserRequestBody createUserRequestBody) {
        userService.createUser(createUserRequestBody);
    }

    @DeleteMapping("/me")
    public void deleteMyData() {
        UserDetailsDto userDetailsDto = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        userDataDeletionService.deleteUserData(userDetailsDto.getId());
    }
}
