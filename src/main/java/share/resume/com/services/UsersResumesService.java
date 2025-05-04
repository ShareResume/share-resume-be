package share.resume.com.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import share.resume.com.controllers.dto.ResumeFilterDto;
import share.resume.com.controllers.dto.response.UserResumeResponseBody;
import share.resume.com.entities.ResumeEntity;
import share.resume.com.entities.ResumesCompaniesEntity;
import share.resume.com.entities.enums.ResumeStatus;
import share.resume.com.exceptions.EntityNotFoundException;
import share.resume.com.repositories.ResumeRepository;
import share.resume.com.repositories.criteria.ResumeSpecification;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsersResumesService {
    private final ResumeSpecification resumeSpecification;
    private final ResumeRepository resumeRepository;
    private final ResumeService resumeService;

    @Transactional
    public List<UserResumeResponseBody> getAll(ResumeFilterDto filter) {
        List<ResumeEntity> resumes = resumeRepository.findAll(resumeSpecification.filterBy(filter));
        resumes.forEach(resume -> {
            List<ResumesCompaniesEntity> filteredCompanies = resume.getResumesCompanies().stream()
                    .filter(rc -> (filter.getCompanyId() == null || rc.getCompany().getId().equals(filter.getCompanyId())) &&
                            (filter.getIsHrScreeningPassed() == null || rc.getIsHrScreeningPassed().equals(filter.getIsHrScreeningPassed())))
                    .collect(Collectors.toList());
            resume.setResumesCompanies(filteredCompanies);
        });
        return resumes.stream()
                .filter(resume -> ResumeStatus.APPROVED.equals(resume.getStatus()))
                .map(resume -> new UserResumeResponseBody(resume, resumeService.getPrivateDocumentByResume(resume)))
                .collect(Collectors.toList());
    }

    @Transactional
    public ResumeEntity getById(UUID id) {
        return resumeRepository.findByIdAndStatus(id, ResumeStatus.APPROVED)
                .orElseThrow(() -> new EntityNotFoundException("Resume with id: " + id + " not found"));
    }
}
