package share.resume.com.controllers.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BookmarkResumeRequestBody {

    @NotNull(message = "Please provide resume id to bookmark it")
    private UUID resumeId;
}
