package share.resume.com.controllers.dto.request;

import lombok.Getter;
import lombok.Setter;
import share.resume.com.entities.enums.ResumeEvent;

@Getter
@Setter
public class UpdateResumeRequestBody {
    private Boolean isHidden;
    private ResumeEvent resumeEvent;
}
