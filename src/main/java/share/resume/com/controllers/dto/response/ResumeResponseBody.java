package share.resume.com.controllers.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import share.resume.com.controllers.dto.CompanyResponseDto;
import share.resume.com.controllers.dto.DocumentView;
import share.resume.com.entities.CompanyEntity;
import share.resume.com.entities.ResumeEntity;
import share.resume.com.entities.ResumesCompaniesEntity;
import share.resume.com.entities.enums.ResumeStatus;
import share.resume.com.entities.enums.SpecialityEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ResumeResponseBody {
    private UUID id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private Integer yearsOfExperience;
    private SpecialityEnum speciality;
    private boolean isHidden;
    private List<CompanyResponseDto> companies;
    private List<DocumentView> documents;
    private ResumeStatus resumeStatus;

    public ResumeResponseBody(ResumeEntity resume, List<DocumentView> documents) {
        this.id = resume.getId();
        this.createdAt = resume.getCreatedAt();
        this.yearsOfExperience = resume.getYearsOfExperience();
        this.speciality = resume.getSpeciality();
        this.isHidden = resume.isHidden();
        this.companies = new ArrayList<>();
        for (ResumesCompaniesEntity c : resume.getResumesCompanies()) {
            this.companies.add(new CompanyResponseDto(c));
        }
        this.documents = documents;
        this.resumeStatus = resume.getStatus();
    }
}
