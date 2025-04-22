package share.resume.com.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import share.resume.com.controllers.dto.ResumeFilterDto;
import share.resume.com.controllers.dto.response.UserResumeResponseBody;
import share.resume.com.services.UsersResumesService;

import java.util.List;

@RestController
@RequestMapping("/api/public-users-resumes")
@RequiredArgsConstructor
public class UsersResumesController {
    private final UsersResumesService usersResumesService;

    @GetMapping
    public List<UserResumeResponseBody> getAll(@ModelAttribute ResumeFilterDto filter) {
        return usersResumesService.getAll(filter);
    }
}
