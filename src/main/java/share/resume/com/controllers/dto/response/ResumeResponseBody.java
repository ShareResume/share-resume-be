package share.resume.com.controllers.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import share.resume.com.controllers.dto.CompanyResponseDto;
import share.resume.com.controllers.dto.DocumentView;
import share.resume.com.entities.ResumeEntity;
import share.resume.com.entities.enums.SpecialityEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ResumeResponseBody {
    private UUID id;
    private UUID authorId;
    private String authorEmail;
    private boolean isHrScreeningPassed;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    private Integer yearsOfExperience;
    private SpecialityEnum speciality;
    private boolean isHidden;
    private CompanyResponseDto company;
    private List<DocumentView> documents;

    public ResumeResponseBody(ResumeEntity resume, List<DocumentView> documents) {
        this.id = resume.getId();
        this.authorId = resume.getAuthor().getId();
        this.authorEmail = resume.getAuthor().getEmail();
        this.isHrScreeningPassed = resume.isHrScreeningPassed();
        this.createdAt = resume.getCreatedAt();
        this.yearsOfExperience = resume.getYearsOfExperience();
        this.speciality = resume.getSpeciality();
        this.isHidden = resume.isHidden();
        this.company = new CompanyResponseDto(resume.getCompany());
        this.documents = documents;
    }
}
