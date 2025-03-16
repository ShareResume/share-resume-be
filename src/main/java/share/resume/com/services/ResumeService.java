package share.resume.com.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import share.resume.com.async.transactions.events.ResumeCreatedEvent;
import share.resume.com.controllers.dto.DocumentView;
import share.resume.com.controllers.dto.request.CreateResumeRequestBody;
import share.resume.com.controllers.dto.request.UpdateResumeRequestBody;
import share.resume.com.controllers.dto.response.ResumeResponseBody;
import share.resume.com.entities.CompanyEntity;
import share.resume.com.entities.DocumentEntity;
import share.resume.com.entities.ResumeEntity;
import share.resume.com.entities.UserEntity;
import share.resume.com.entities.enums.DocumentAccessTypeEnum;
import share.resume.com.exceptions.EntityNotFoundException;
import share.resume.com.repositories.ResumeRepository;
import share.resume.com.security.dto.UserDetailsDto;
import share.resume.com.services.files.FileService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {
    private final ApplicationEventPublisher eventPublisher;
    private final CompanyService companyService;
    private final ResumeRepository resumeRepository;
    private final FileService fileService;

    @Transactional
    public void save(CreateResumeRequestBody createResumeRequestBody) {
        ResumeEntity resume = new ResumeEntity();
        UserDetailsDto userDetailsDto = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userDetailsDto.getUserEntity();
        resume.setAuthor(user);
        CompanyEntity company = companyService.getById(createResumeRequestBody.getCompanyId());
        resume.setCompany(company);
        resume.setCreatedAt(LocalDateTime.now());
        resume.setSpeciality(createResumeRequestBody.getSpeciality());
        resume.setHrScreeningPassed(createResumeRequestBody.getIsHrScreeningPassed());
        resume.setYearsOfExperience(createResumeRequestBody.getYearsOfExperience());

        MultipartFile publicCv = createResumeRequestBody.getDocument();
        String directoryName = "user-directory-" + user.getId();
        String publicCvFileName = publicCv.getOriginalFilename();
        DocumentEntity publicCvEnitiy = new DocumentEntity();
        publicCvEnitiy.setAccessType(DocumentAccessTypeEnum.PUBLIC);
        publicCvEnitiy.setResume(resume);
        publicCvEnitiy.setDirectory(directoryName);
        publicCvEnitiy.setName(publicCvFileName);

        /*
        DocumentEntity cvPrivate = new DocumentEntity();
        cvPrivate.setAccessType(DocumentAccessTypeEnum.PRIVATE);
        cvPrivate.setResume(resume);

        resume.setDocuments(List.of(cvPublic, cvPrivate));
        */
        resume.setDocuments(List.of(publicCvEnitiy));
        eventPublisher.publishEvent(new ResumeCreatedEvent(createResumeRequestBody.getDocument(), publicCvFileName, directoryName));
        resumeRepository.save(resume);
    }

    @Transactional
    public void update(UUID id, UpdateResumeRequestBody updateResumeRequestBody) {
        Boolean isHidden = updateResumeRequestBody.getIsHidden();
        if (isHidden != null) {
            ResumeEntity resume = getById(id);
            resume.setHidden(isHidden);
        }
    }

    @Transactional
    public ResumeEntity getById(UUID id) {
        return resumeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resume not found: " + id));
    }

    @Transactional
    public void delete(UUID id) {
        resumeRepository.deleteById(id);
    }

    @Transactional
    public List<ResumeResponseBody> getAll() {
        UserDetailsDto userDetailsDto = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ResumeEntity> resumes = resumeRepository.findAllByAuthor(userDetailsDto.getUserEntity());
        List<ResumeResponseBody> resumeResponseBodies = new ArrayList<>();
        for (ResumeEntity resume : resumes) {
            List<DocumentView> documentViews = new ArrayList<>();
            for (DocumentEntity document : resume.getDocuments()) {
                DocumentView documentView = new DocumentView();
                documentView.setAccessType(document.getAccessType());
                documentView.setName(document.getName());
                documentView.setUrl(fileService.getAccessLink(document.getDirectory(), document.getName()));
                documentViews.add(documentView);
            }
            resumeResponseBodies.add(new ResumeResponseBody(resume, documentViews));
        }
        return resumeResponseBodies;
    }
}
