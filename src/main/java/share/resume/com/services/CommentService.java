package share.resume.com.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.resume.com.controllers.dto.request.CreateCommentRequestBody;
import share.resume.com.controllers.dto.request.ReactToCommentRequestBody;
import share.resume.com.controllers.dto.response.CommentResponseBody;
import share.resume.com.entities.CommentEntity;
import share.resume.com.entities.ResumeEntity;
import share.resume.com.entities.UserCommentVoteStateEntity;
import share.resume.com.entities.UserEntity;
import share.resume.com.entities.enums.VoteStateEnum;
import share.resume.com.exceptions.ActionNotAllowed;
import share.resume.com.exceptions.EntityNotFoundException;
import share.resume.com.repositories.CommentRepository;
import share.resume.com.security.dto.UserDetailsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ResumeService resumeService;

    @Transactional
    public void createComment(CreateCommentRequestBody createCommentRequestBody) {
        CommentEntity commentEntity = new CommentEntity();
        UUID resumeId = createCommentRequestBody.getResumeId();
        ResumeEntity resume = resumeService.getByIdEntity(resumeId);
        UUID parentCommentId = createCommentRequestBody.getParentCommentId();
        if (parentCommentId != null && !existsById(parentCommentId)) {
            throw new EntityNotFoundException("Parent comment does not exist");
        }
        commentEntity.setResume(resume);
        commentEntity.setParentCommentId(parentCommentId);
        commentEntity.setMessage(createCommentRequestBody.getText());
        commentEntity.setCreatedAt(LocalDateTime.now());
        commentRepository.save(commentEntity);
    }

    @Transactional
    public void reactToComment(ReactToCommentRequestBody reactToCommentRequestBody) {
        UserDetailsDto userDetailsDto = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userDetailsDto.getUserEntity();
        CommentEntity comment = getById(reactToCommentRequestBody.getCommentId());
        if (comment.getUserCommentVoteStates().stream()
                .anyMatch(userCommentVoteStateEntity -> userCommentVoteStateEntity.getUser().equals(user))) {
            throw new ActionNotAllowed("Cannot react to comment because it is already voted");
        }
        VoteStateEnum voteState = reactToCommentRequestBody.getVoteState();
        if (VoteStateEnum.UNDEFINED.equals(voteState)) {
            throw new ActionNotAllowed("You can't react with undefined vote state");
        }
        int commentRate = comment.getReactionsRate();
        if (VoteStateEnum.UP.equals(voteState)) {
            comment.setReactionsRate(commentRate + 1);
        }
        if (VoteStateEnum.DOWN.equals(voteState) && commentRate > 0) {
            comment.setReactionsRate(commentRate - 1);
        }
        UserCommentVoteStateEntity userCommentVoteStateEntity = new UserCommentVoteStateEntity();
        userCommentVoteStateEntity.setComment(comment);
        userCommentVoteStateEntity.setVoteState(voteState);
        userCommentVoteStateEntity.setUser(user);
        comment.getUserCommentVoteStates().add(userCommentVoteStateEntity);
        commentRepository.save(comment);
    }

    @Transactional
    public List<CommentResponseBody> getAllByResumeId(UUID resumeId) {
        return commentRepository.findAllByResume_Id(resumeId).stream()
                .map(CommentResponseBody::new)
                .toList();
    }

    @Transactional
    public boolean existsById(UUID commentId) {
        return commentRepository.existsById(commentId);
    }

    @Transactional
    public CommentEntity getById(UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id" + commentId + " not found"));
    }
}
