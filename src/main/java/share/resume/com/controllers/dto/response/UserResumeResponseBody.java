package share.resume.com.controllers.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import share.resume.com.controllers.dto.CompanyResponseDto;
import share.resume.com.controllers.dto.DocumentView;
import share.resume.com.entities.ResumeEntity;
import share.resume.com.entities.ResumesCompaniesEntity;
import share.resume.com.entities.enums.SpecialityEnum;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserResumeResponseBody {
    private UUID id;
    private List<CompanyResponseDto> companies;
    private SpecialityEnum speciality;
    private Integer yearsOfExperience;
    private DocumentView document;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    public UserResumeResponseBody(ResumeEntity resumeEntity, DocumentView document) {
        this.id = resumeEntity.getId();
        this.companies = new ArrayList<>();
        for (ResumesCompaniesEntity c : resumeEntity.getResumesCompanies()) {
            this.companies.add(new CompanyResponseDto(c));
        }
        speciality = resumeEntity.getSpeciality();
        yearsOfExperience = resumeEntity.getYearsOfExperience();
        date = resumeEntity.getCreatedAt().toLocalDate();
        this.document = document;
    }
}
