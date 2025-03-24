package share.resume.com.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.resume.com.controllers.dto.request.BookmarkResumeRequestBody;
import share.resume.com.controllers.dto.response.ResumeResponseBody;
import share.resume.com.entities.ResumeEntity;
import share.resume.com.entities.UserEntity;
import share.resume.com.entities.UsersFavoritesResumesEntity;
import share.resume.com.exceptions.ActionNotAllowed;
import share.resume.com.repositories.UsersFavoritesResumesRepository;
import share.resume.com.security.dto.UserDetailsDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsersFavoritesResumesService {
    private final UsersFavoritesResumesRepository usersFavoritesResumesRepository;
    private final ResumeService resumeService;

    @Transactional
    public void bookmarkResume(BookmarkResumeRequestBody bookmarkResumeRequestBody) {
        UserDetailsDto userDetailsDto = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = userDetailsDto.getUserEntity();
        ResumeEntity resume = resumeService.getById(bookmarkResumeRequestBody.getResumeId());
        Optional<UsersFavoritesResumesEntity> usersFavoritesResumesEntity = usersFavoritesResumesRepository.findByUser_IdAndResume_Id(user.getId(), resume.getId());
        if (usersFavoritesResumesEntity.isPresent()) {
            throw new ActionNotAllowed("You can not bookmark resume with id " + bookmarkResumeRequestBody.getResumeId() + " twice");
        }
        UsersFavoritesResumesEntity usersFavoritesResumes = new UsersFavoritesResumesEntity();
        usersFavoritesResumes.setUser(user);
        usersFavoritesResumes.setResume(resume);
        usersFavoritesResumesRepository.save(usersFavoritesResumes);
    }

    @Transactional
    public void removeResumeFromFavorites(UUID bookmarkedResumeId) {
        usersFavoritesResumesRepository.deleteById(bookmarkedResumeId);
    }

    @Transactional
    public List<ResumeResponseBody> getFavoriteResumes() {
        UserDetailsDto userDetailsDto = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = userDetailsDto.getUserEntity();
        List<UsersFavoritesResumesEntity> usersFavoritesResumesEntities = usersFavoritesResumesRepository.findByUser_Id(user.getId());
        return usersFavoritesResumesEntities.stream()
                .map(UsersFavoritesResumesEntity::getResume)
                .map(resume -> new ResumeResponseBody(resume, resumeService.getDocumentsByResume(resume)))
                .toList();
    }
}
