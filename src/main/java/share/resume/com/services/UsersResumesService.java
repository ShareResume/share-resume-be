package share.resume.com.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import share.resume.com.controllers.dto.ResumeFilterDto;
import share.resume.com.controllers.dto.response.UserResumeResponseBody;
import share.resume.com.entities.DocumentEntity;
import share.resume.com.entities.ResumeEntity;
import share.resume.com.entities.ResumesCompaniesEntity;
import share.resume.com.entities.enums.DocumentAccessTypeEnum;
import share.resume.com.entities.enums.ResumeStatus;
import share.resume.com.entities.enums.RoleEnum;
import share.resume.com.exceptions.ActionNotAllowed;
import share.resume.com.exceptions.EntityNotFoundException;
import share.resume.com.repositories.ResumeRepository;
import share.resume.com.repositories.criteria.ResumeSpecification;
import share.resume.com.security.dto.UserDetailsDto;
import share.resume.com.services.files.FileService;
import share.resume.com.controllers.dto.DocumentView;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsersResumesService {
    private final ResumeSpecification resumeSpecification;
    private final ResumeRepository resumeRepository;
    private final ResumeService resumeService;
    private final FileService fileService;

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

    @Transactional
    public UserResumeResponseBody getPublicResumeById(UUID id) {
        ResumeEntity resume = resumeRepository.findByIdAndStatus(id, ResumeStatus.APPROVED)
                .orElseThrow(() -> new EntityNotFoundException("Resume with id: " + id + " not found"));
        DocumentEntity publicDocument = resume.getDocuments().stream()
                .filter(document -> DocumentAccessTypeEnum.PRIVATE.equals(document.getAccessType()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Public document not found for resume with id: " + id));
        
        // Convert DocumentEntity to DocumentView
        DocumentView documentView = new DocumentView();
        documentView.setAccessType(publicDocument.getAccessType());
        documentView.setName(publicDocument.getName());
        documentView.setUrl(fileService.getAccessLink(publicDocument.getDirectory(), publicDocument.getName()));
        
        return new UserResumeResponseBody(resume, documentView);
    }

    @Transactional
    public void updateUserPrivateResume(UUID resumeId, MultipartFile newPrivateResume) {
        UserDetailsDto userDetailsDto = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!RoleEnum.ADMIN.equals(userDetailsDto.getRole())) {
            throw new ActionNotAllowed("Only admins can update public resume");
        }
        ResumeEntity resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("Resume with id: " + resumeId + " not found"));
        DocumentEntity privateDocumentToUpdate = resume.getDocuments().stream()
                .filter(documentEntity -> DocumentAccessTypeEnum.PRIVATE.equals(documentEntity.getAccessType()))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("Private document not found for resume with id: " + resumeId));
        fileService.delete(privateDocumentToUpdate.getDirectory(), privateDocumentToUpdate.getName());
        privateDocumentToUpdate.setName("PRIVATE-" + newPrivateResume.getOriginalFilename());
        fileService.upload(privateDocumentToUpdate.getDirectory(), newPrivateResume, privateDocumentToUpdate.getName());
        resumeRepository.save(resume);
    }
}
