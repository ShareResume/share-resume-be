package share.resume.com.controllers.dto.response;

import lombok.Getter;
import lombok.Setter;
import share.resume.com.controllers.dto.CompanyResponseDto;
import share.resume.com.entities.ResumeEntity;
import share.resume.com.entities.enums.SpecialityEnum;

import java.time.LocalDate;

@Getter
@Setter
public class UserResumeResponseBody {
    private CompanyResponseDto company;
    private SpecialityEnum speciality;
    private Boolean isHrScreeningPassed;
    private Integer yearsOfExperience;
    private LocalDate date;

    public UserResumeResponseBody(ResumeEntity resumeEntity) {
        company = new CompanyResponseDto(resumeEntity.getCompany());
        speciality = resumeEntity.getSpeciality();
        isHrScreeningPassed = resumeEntity.isHrScreeningPassed();
        yearsOfExperience = resumeEntity.getYearsOfExperience();
        date = resumeEntity.getCreatedAt().toLocalDate();
    }
}
