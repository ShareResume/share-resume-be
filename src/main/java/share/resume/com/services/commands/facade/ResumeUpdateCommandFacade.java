package share.resume.com.services.commands.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import share.resume.com.controllers.dto.request.UpdateResumeRequestBody;
import share.resume.com.entities.enums.RoleEnum;
import share.resume.com.services.commands.AdminResumeUpdateStrategy;
import share.resume.com.services.commands.UserResumeUpdateStrategy;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResumeUpdateCommandFacade {
    private final AdminResumeUpdateStrategy adminResumeUpdateStrategy;
    private final UserResumeUpdateStrategy userResumeUpdateStrategy;

    public void execute(RoleEnum roleEnum, UUID resumeId, UpdateResumeRequestBody updateResumeRequestBody) throws Exception {
        if (RoleEnum.ADMIN.equals(roleEnum)) {
            adminResumeUpdateStrategy.execute(resumeId, updateResumeRequestBody);
        } else {
            userResumeUpdateStrategy.execute(resumeId, updateResumeRequestBody);
        }
    }
}
