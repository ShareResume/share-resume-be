package share.resume.com.controllers.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserRequestBody {

    @Email(message = "Wrong email format")
    @NotBlank(message = "Wrong email format")
    private String email;

    @NotBlank(message = "Provide the password please")
    @Size(min = 8, max = 255, message = "Password length must be from 8 to 255 symbols")
    private String password;
}
