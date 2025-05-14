package share.resume.com.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import share.resume.com.controllers.dto.request.BookmarkResumeRequestBody;
import share.resume.com.controllers.dto.response.ResumeResponseBody;
import share.resume.com.services.UsersFavoritesResumesService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user-favorites-resumes")
@RequiredArgsConstructor
public class UserFavoritesResumesController {
    private final UsersFavoritesResumesService usersFavoritesResumesService;

    @GetMapping
    public ResponseEntity<List<ResumeResponseBody>> getUserFavoritesResumes() {
        return new ResponseEntity<>(usersFavoritesResumesService.getFavoriteResumes(), HttpStatus.OK);
    }

    @PostMapping
    public void bookmark(@RequestBody @Valid BookmarkResumeRequestBody bookmarkResumeRequestBody) {
        usersFavoritesResumesService.bookmarkResume(bookmarkResumeRequestBody);
    }

    @DeleteMapping("/{resumeId}")
    public void removeFromBookmarked(@PathVariable UUID resumeId) {
        usersFavoritesResumesService.removeResumeFromFavorites(resumeId);
    }
}
