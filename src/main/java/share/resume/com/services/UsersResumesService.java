package share.resume.com.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import share.resume.com.controllers.dto.ResumeFilterDto;
import share.resume.com.controllers.dto.response.UserResumeResponseBody;
import share.resume.com.entities.ResumeEntity;
import share.resume.com.entities.enums.ResumeStatus;
import share.resume.com.repositories.ResumeRepository;
import share.resume.com.repositories.criteria.ResumeSpecification;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsersResumesService {
    private final ResumeSpecification resumeSpecification;
    private final ResumeRepository resumeRepository;

    @Transactional
    public List<UserResumeResponseBody> getAll(ResumeFilterDto filter) {
        List<ResumeEntity> resumes = resumeRepository.findAll(resumeSpecification.filterBy(filter));
        return resumes.stream()
                .filter(resume -> ResumeStatus.APPROVED.equals(resume.getStatus()))
                .map(UserResumeResponseBody::new)
                .collect(Collectors.toList());
    }
}
