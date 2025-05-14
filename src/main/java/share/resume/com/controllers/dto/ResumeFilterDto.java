package share.resume.com.controllers.dto;

import lombok.Getter;
import lombok.Setter;
import share.resume.com.entities.enums.SpecialityEnum;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ResumeFilterDto {
    private UUID companyId;
    private SpecialityEnum speciality;
    private Boolean isHrScreeningPassed;
    private RangeDto yearOfExperienceRange;
    private LocalDate date;
}
