package share.resume.com.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import share.resume.com.controllers.dto.request.CreateCommentRequestBody;
import share.resume.com.controllers.dto.request.ReactToCommentRequestBody;
import share.resume.com.controllers.dto.response.CommentResponseBody;
import share.resume.com.services.CommentService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public void save(@RequestBody @Valid CreateCommentRequestBody createCommentRequestBody) {
        commentService.createComment(createCommentRequestBody);
    }

    @PatchMapping
    public void reactToComment(@RequestBody @Valid ReactToCommentRequestBody reactToCommentRequestBody) {
        commentService.reactToComment(reactToCommentRequestBody);
    }

    @GetMapping("/resumes/{id}")
    public List<CommentResponseBody> getAllCommentsOfResume(@PathVariable UUID id) {
        return commentService.getAllByResumeId(id);
    }
}
