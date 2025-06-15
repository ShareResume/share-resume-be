package share.resume.com.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import share.resume.com.async.transactions.events.ResumeCreatedEvent;
import share.resume.com.async.transactions.events.ResumeDeletedEvent;
import share.resume.com.controllers.dto.DocumentView;
import share.resume.com.controllers.dto.request.CompanyWithStatusDto;
import share.resume.com.controllers.dto.request.CreateResumeRequestBody;
import share.resume.com.controllers.dto.request.UpdateResumeRequestBody;
import share.resume.com.controllers.dto.response.ResumeResponseBody;
import share.resume.com.entities.*;
import share.resume.com.entities.enums.DocumentAccessTypeEnum;
import share.resume.com.entities.enums.ResumeStatus;
import share.resume.com.entities.enums.RoleEnum;
import share.resume.com.exceptions.EntityNotFoundException;
import share.resume.com.repositories.ResumeRepository;
import share.resume.com.security.dto.UserDetailsDto;
import share.resume.com.services.commands.facade.ResumeUpdateCommandFacade;
import share.resume.com.services.files.FileService;
import share.resume.com.services.integrators.AnonymizerIntegratorService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {
    private final ApplicationEventPublisher eventPublisher;
    private final CompanyService companyService;
    private final ResumeRepository resumeRepository;
    private final FileService fileService;
    private final AnonymizerIntegratorService anonymizerIntegratorService;
    private final ResumeUpdateCommandFacade resumeUpdateCommandFacade;

    @Transactional
    public void save(CreateResumeRequestBody createResumeRequestBody) {
        ResumeEntity resume = new ResumeEntity();
        UserDetailsDto userDetailsDto = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserEntity user = userDetailsDto.getUserEntity();
        resume.setAuthor(user);
        resume.setCreatedAt(LocalDateTime.now());
        resume.setSpeciality(createResumeRequestBody.getSpeciality());
        resume.setYearsOfExperience(createResumeRequestBody.getYearsOfExperience());
        resume.setStatus(ResumeStatus.WAITING_FOR_APPROVE);

        MultipartFile publicCv = createResumeRequestBody.getDocument();
        String directoryName = "user-directory-" + user.getId();
        String publicCvFileName = publicCv.getOriginalFilename();
        DocumentEntity publicCvEnitiy = new DocumentEntity();
        publicCvEnitiy.setAccessType(DocumentAccessTypeEnum.PUBLIC);
        publicCvEnitiy.setResume(resume);
        publicCvEnitiy.setDirectory(directoryName);
        publicCvEnitiy.setName(publicCvFileName);

        MultipartFile privateCvFile = anonymizerIntegratorService.anonymize(publicCv);

        DocumentEntity privateCvEntity = new DocumentEntity();
        privateCvEntity.setAccessType(DocumentAccessTypeEnum.PRIVATE);
        privateCvEntity.setResume(resume);
        privateCvEntity.setDirectory(directoryName);
        privateCvEntity.setName(privateCvFile.getOriginalFilename());
        resume.setDocuments(List.of(publicCvEnitiy, privateCvEntity));

        List<CompanyEntity> companies = companyService.getAllByIds(createResumeRequestBody.getCompanies().stream().map(CompanyWithStatusDto::getId).collect(Collectors.toList()));
        List<ResumesCompaniesEntity> resumesCompaniesEntities = new ArrayList<>();
        for (CompanyEntity company : companies) {
            ResumesCompaniesEntity resumesCompaniesEntity = new ResumesCompaniesEntity();
            resumesCompaniesEntity.setCompany(company);
            resumesCompaniesEntity.setResume(resume);
            resumesCompaniesEntity.setIsHrScreeningPassed(createResumeRequestBody.getCompanies().stream().filter(companyWithStatusDto -> company.getId().equals(companyWithStatusDto.getId())).findFirst().get().getIsHrScreeningPassed());
            resumesCompaniesEntities.add(resumesCompaniesEntity);
        }
        resume.setResumesCompanies(resumesCompaniesEntities);

        eventPublisher.publishEvent(new ResumeCreatedEvent(createResumeRequestBody.getDocument(), publicCvFileName, directoryName, privateCvFile, privateCvFile.getOriginalFilename()));
        resumeRepository.save(resume);
    }

    @Transactional
    public void update(UUID id, UpdateResumeRequestBody updateResumeRequestBody) throws Exception {
        UserDetailsDto userDetailsDto = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        resumeUpdateCommandFacade.execute(userDetailsDto.getRole(), id, updateResumeRequestBody);
    }

    @Transactional
    public ResumeResponseBody getById(UUID id) {
        UserDetailsDto userDetailsDto = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ResumeEntity resume;
        if (RoleEnum.ADMIN.equals(userDetailsDto.getRole())) {
            resume = resumeRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Resume with id: " + id + " not found"));
        } else {
            resume = resumeRepository.findByIdAndAuthor(id, userDetailsDto.getUserEntity())
                    .orElseThrow(() -> new EntityNotFoundException("Resume with id: " + id + " not found for user: " + userDetailsDto.getEmail()));
        }
        List<DocumentView> documentViews = getDocumentsByResume(resume);
        return new ResumeResponseBody(resume, documentViews);
    }

    @Transactional
    public ResumeEntity getByIdEntity(UUID id) {
        UserDetailsDto userDetailsDto = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return resumeRepository.findByIdAndAuthor(id, userDetailsDto.getUserEntity())
                .orElseThrow(() -> new EntityNotFoundException("Resume with id: " + id + " not found for user: " + userDetailsDto.getEmail()));
    }

    @Transactional
    public void delete(UUID id) {
        ResumeEntity resume = getByIdEntity(id);
        resumeRepository.deleteById(id);
        DocumentEntity publicDoc = resume.getDocuments().stream()
                .filter(d -> d.getAccessType() == DocumentAccessTypeEnum.PUBLIC)
                .findAny()
                .get();
        DocumentEntity privateDoc = resume.getDocuments().stream()
                .filter(d -> d.getAccessType() == DocumentAccessTypeEnum.PRIVATE)
                .findAny()
                .get();
        String directoryName = publicDoc.getDirectory();
        eventPublisher.publishEvent(new ResumeDeletedEvent(publicDoc.getName(), privateDoc.getName(), directoryName));
    }

    @Transactional
    public List<ResumeResponseBody> getAll() {
        UserDetailsDto userDetailsDto = (UserDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<ResumeEntity> resumes;
        if (RoleEnum.USER.equals(userDetailsDto.getRole())) {
            resumes = resumeRepository.findAllByAuthor(userDetailsDto.getUserEntity());
        } else {
            resumes = resumeRepository.findAll();
        }
        List<ResumeResponseBody> resumeResponseBodies = new ArrayList<>();
        for (ResumeEntity resume : resumes) {
            List<DocumentView> documentViews = getDocumentsByResume(resume);
            resumeResponseBodies.add(new ResumeResponseBody(resume, documentViews));
        }
        return resumeResponseBodies;
    }

    public List<DocumentView> getDocumentsByResume(ResumeEntity resume) {
        List<DocumentView> documentViews = new ArrayList<>();
        for (DocumentEntity document : resume.getDocuments()) {
            DocumentView documentView = new DocumentView();
            documentView.setAccessType(document.getAccessType());
            documentView.setName(document.getName());
            documentView.setUrl(fileService.getAccessLink(document.getDirectory(), document.getName()));
            documentViews.add(documentView);
        }
        return documentViews;
    }

    public DocumentView getPrivateDocumentByResume(ResumeEntity resume) {
        DocumentEntity document = resume.getDocuments().stream()
                .filter(doc -> DocumentAccessTypeEnum.PRIVATE.equals(doc.getAccessType()))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Private document for resume with id: " + resume.getId() + " not found"));
        DocumentView documentView = new DocumentView();
        documentView.setAccessType(DocumentAccessTypeEnum.PRIVATE);
        documentView.setName(document.getName());
        documentView.setUrl(fileService.getAccessLink(document.getDirectory(), document.getName()));
        return documentView;
    }
}
