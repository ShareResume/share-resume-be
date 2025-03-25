package share.resume.com.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import share.resume.com.entities.enums.VoteStateEnum;

import java.util.UUID;

@Getter
@Setter
@Table(name = "USER_COMMENT_VOTE_STATE")
@Entity
public class UserCommentVoteStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private UserEntity user;

    @ManyToOne
    private CommentEntity comment;

    @Enumerated(EnumType.STRING)
    private VoteStateEnum voteState;
}
