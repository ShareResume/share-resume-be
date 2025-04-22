package share.resume.com.controllers.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import share.resume.com.controllers.dto.CompanyResponseDto;
import share.resume.com.controllers.dto.DocumentView;
import share.resume.com.entities.CompanyEntity;
import share.resume.com.entities.ResumeEntity;
import share.resume.com.entities.enums.SpecialityEnum;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserResumeResponseBody {
    private List<CompanyResponseDto> companies;
    private SpecialityEnum speciality;
    private Boolean isHrScreeningPassed;
    private Integer yearsOfExperience;
    private DocumentView document;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    public UserResumeResponseBody(ResumeEntity resumeEntity, DocumentView document) {
        this.companies = new ArrayList<>();
        for (CompanyEntity c : resumeEntity.getCompanies()) {
            this.companies.add(new CompanyResponseDto(c));
        }
        speciality = resumeEntity.getSpeciality();
        isHrScreeningPassed = resumeEntity.isHrScreeningPassed();
        yearsOfExperience = resumeEntity.getYearsOfExperience();
        date = resumeEntity.getCreatedAt().toLocalDate();
        this.document = document;
    }
}
