package share.resume.com.controllers.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;
import share.resume.com.entities.CommentEntity;
import share.resume.com.entities.UserCommentVoteStateEntity;
import share.resume.com.entities.UserEntity;
import share.resume.com.entities.enums.VoteStateEnum;
import share.resume.com.security.dto.UserDetailsDto;

import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
public class CommentResponseBody {
    private UUID id;
    private UUID parentCommentId;
    private UUID resumeId;
    private Integer reactionsRate;
    private String text;
    private VoteStateEnum userVoteState;

    public CommentResponseBody(CommentEntity comment) {
        this.id = comment.getId();
        this.parentCommentId = comment.getParentCommentId();
        this.resumeId = comment.getResume().getId();
        this.reactionsRate = comment.getReactionsRate();
        this.text = comment.getMessage();

        UserDetailsDto userDetailsDto = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userDetailsDto.getUserEntity();
        Optional<UserCommentVoteStateEntity> userCommentVoteState = comment.getUserCommentVoteStates().stream()
                .filter(userCommentVoteStateEntity -> userCommentVoteStateEntity.getUser().equals(user))
                .findAny();
        if (userCommentVoteState.isPresent()) {
            this.userVoteState = userCommentVoteState.get().getVoteState();
        } else {
            this.userVoteState = VoteStateEnum.UNDEFINED;
        }
    }
}
