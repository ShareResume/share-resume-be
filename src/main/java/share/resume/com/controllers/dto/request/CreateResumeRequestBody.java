package share.resume.com.controllers.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import share.resume.com.entities.enums.SpecialityEnum;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateResumeRequestBody {

    @NotNull(message = "Please provide information about hr screening")
    private Boolean isHrScreeningPassed;

    @NotEmpty(message = "Please provide companies ids")
    private List<UUID> companiesIds;

    @NotNull(message = "Please provide your years of experience")
    @Min(value = 0, message = "Years of experience can't be less then null")
    private Integer yearsOfExperience;

    @NotNull(message = "Please provide your speciality")
    private SpecialityEnum speciality;

    @NotNull(message = "Please provide your resume")
    private MultipartFile document;
}
