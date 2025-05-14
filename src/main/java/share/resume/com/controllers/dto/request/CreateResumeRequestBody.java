package share.resume.com.controllers.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import share.resume.com.entities.enums.SpecialityEnum;

import java.util.List;

@Getter
@Setter
public class CreateResumeRequestBody {

    @NotEmpty(message = "Please provide companies info")
    @Valid
    private List<CompanyWithStatusDto> companies;

    @NotNull(message = "Please provide your years of experience")
    @Min(value = 0, message = "Years of experience can't be less then null")
    private Integer yearsOfExperience;

    @NotNull(message = "Please provide your speciality")
    private SpecialityEnum speciality;

    @NotNull(message = "Please provide your resume")
    private MultipartFile document;
}
