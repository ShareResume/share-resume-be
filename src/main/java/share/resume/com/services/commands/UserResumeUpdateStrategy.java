package share.resume.com.services.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import share.resume.com.controllers.dto.request.UpdateResumeRequestBody;
import share.resume.com.entities.ResumeEntity;
import share.resume.com.exceptions.EntityNotFoundException;
import share.resume.com.repositories.ResumeRepository;
import share.resume.com.security.dto.UserDetailsDto;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserResumeUpdateStrategy implements ResumeUpdateStrategy {
    private final ResumeRepository resumeRepository;

    @Override
    @Transactional
    public void execute(UUID resumeId, UpdateResumeRequestBody requestBody) {
        Boolean isHidden = requestBody.getIsHidden();
        if (isHidden != null) {
            UserDetailsDto userDetailsDto = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ResumeEntity resume = resumeRepository.findByIdAndAuthor(resumeId, userDetailsDto.getUserEntity())
                    .orElseThrow(() -> new EntityNotFoundException("Resume with id: " + resumeId + " not found for user: " + userDetailsDto.getEmail()));
            resume.setHidden(isHidden);
            resumeRepository.save(resume);
        }
    }
}
