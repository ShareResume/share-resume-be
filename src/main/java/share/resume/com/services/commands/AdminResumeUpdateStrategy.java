package share.resume.com.services.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import share.resume.com.controllers.dto.request.UpdateResumeRequestBody;
import share.resume.com.entities.enums.ResumeEvent;
import share.resume.com.entities.enums.ResumeStatus;
import share.resume.com.exceptions.ActionNotAllowed;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminResumeUpdateStrategy implements ResumeUpdateStrategy {
    private final StateMachineFactory<ResumeStatus, ResumeEvent> stateMachineFactory;
    private final StateMachinePersister<ResumeStatus, ResumeEvent, UUID> persister;

    @Override
    @Transactional
    public void execute(UUID resumeId, UpdateResumeRequestBody requestBody) throws Exception {
        ResumeEvent resumeEvent = requestBody.getResumeEvent();
        if (resumeEvent == null) {
            throw new ActionNotAllowed("Resume event cannot be null when updating resume state");
        }
        try {
            StateMachine<ResumeStatus, ResumeEvent> stateMachine = stateMachineFactory.getStateMachine();
            persister.restore(stateMachine, resumeId);

            boolean accepted = stateMachine.sendEvent(resumeEvent);

            if (!accepted) {
                throw new ActionNotAllowed("Resume event " + resumeEvent + " not accepted. Transition now allowed");
            }
            persister.persist(stateMachine, resumeId);


        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
}
