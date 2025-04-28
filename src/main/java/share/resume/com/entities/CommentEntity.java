package share.resume.com.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Table(name = "COMMENTS")
@Entity
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "resume_id")
    private ResumeEntity resume;
    private UUID parentCommentId;
    private int reactionsRate;
    private String message;
    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true, mappedBy = "comment")
    private List<UserCommentVoteStateEntity> userCommentVoteStates = new ArrayList<>();
}
