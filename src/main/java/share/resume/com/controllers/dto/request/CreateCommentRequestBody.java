package share.resume.com.controllers.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateCommentRequestBody {

    @NotNull(message = "Resume id must be provided")
    private UUID resumeId;
    private UUID parentCommentId;

    @NotBlank(message = "Provide text of comment")
    @Size(min = 5, max = 1000, message = "Min 5 symbols, max 1000")
    private String text;
}
