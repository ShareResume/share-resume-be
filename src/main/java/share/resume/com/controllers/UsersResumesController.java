package share.resume.com.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import share.resume.com.controllers.dto.ResumeFilterDto;
import share.resume.com.controllers.dto.response.UserResumeResponseBody;
import share.resume.com.services.UsersResumesService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/public-users-resumes")
@RequiredArgsConstructor
public class UsersResumesController {
    private final UsersResumesService usersResumesService;

    @GetMapping
    public List<UserResumeResponseBody> getAll(@ModelAttribute ResumeFilterDto filter) {
        return usersResumesService.getAll(filter);
    }

    @GetMapping("/{resumeId}")
    public UserResumeResponseBody getById(@PathVariable UUID resumeId) {
        return usersResumesService.getPublicResumeById(resumeId);
    }

    @PatchMapping("/{resumeId}")
    public void updateResume(@PathVariable UUID resumeId, @RequestPart MultipartFile file) {
        usersResumesService.updateUserPrivateResume(resumeId, file);
    }
}
