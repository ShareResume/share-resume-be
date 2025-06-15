package share.resume.com.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import share.resume.com.controllers.dto.request.CreateResumeRequestBody;
import share.resume.com.controllers.dto.request.UpdateResumeRequestBody;
import share.resume.com.controllers.dto.response.ResumeResponseBody;
import share.resume.com.services.ResumeService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/resumes")
@RequiredArgsConstructor
public class ResumeController {
    private final ResumeService resumeService;

    @PostMapping
    public void create(@ModelAttribute @Valid CreateResumeRequestBody createResumeRequestBody) {
        resumeService.save(createResumeRequestBody);
    }

    @GetMapping
    public ResponseEntity<List<ResumeResponseBody>> getAll() {
        return ResponseEntity.ok(resumeService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResumeResponseBody> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(resumeService.getById(id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        resumeService.delete(id);
    }

    @PatchMapping("/{id}")
    public void update(@PathVariable UUID id, @RequestBody @Valid UpdateResumeRequestBody updateResumeRequestBody) throws Exception {
        resumeService.update(id, updateResumeRequestBody);
    }
}
