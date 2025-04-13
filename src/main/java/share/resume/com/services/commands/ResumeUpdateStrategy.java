package share.resume.com.services.commands;

import share.resume.com.controllers.dto.request.UpdateResumeRequestBody;
import share.resume.com.entities.ResumeEntity;

import java.util.UUID;

public interface ResumeUpdateStrategy {
    void execute(UUID resumeId, UpdateResumeRequestBody requestBody) throws Exception;
}
