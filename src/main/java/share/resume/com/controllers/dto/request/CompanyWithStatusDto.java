package share.resume.com.controllers.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CompanyWithStatusDto {

    @NotNull(message = "Please provide resume id")
    private UUID id;

    @NotNull(message = "Please provide information about hr screening")
    private Boolean isHrScreeningPassed;
}
