package share.resume.com.controllers.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import share.resume.com.entities.enums.VoteStateEnum;

import java.util.UUID;

@Getter
@Setter
public class ReactToCommentRequestBody {

    @NotNull(message = "Comment id must not be null")
    private UUID commentId;

    @NotNull(message = "You must provide vote state")
    private VoteStateEnum voteState;
}
