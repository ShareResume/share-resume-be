package share.resume.com.services.fsm;

import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.StateMachineContext;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import share.resume.com.entities.ResumeEntity;
import share.resume.com.entities.enums.ResumeEvent;
import share.resume.com.entities.enums.ResumeStatus;
import share.resume.com.exceptions.EntityNotFoundException;
import share.resume.com.repositories.ResumeRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResumeStateMachinePersist implements StateMachinePersist<ResumeStatus, ResumeEvent, UUID> {
    private final ResumeRepository resumeRepository;

    @Override
    @Transactional
    public void write(StateMachineContext<ResumeStatus, ResumeEvent> context, UUID resumeId) {
        ResumeEntity resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("Resume with id " + resumeId + " not found while updating state machine state"));
        resume.setStatus(context.getState());
        resumeRepository.save(resume);
    }

    @Override
    @Transactional
    public StateMachineContext<ResumeStatus, ResumeEvent> read(UUID resumeId) {
        ResumeEntity resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new EntityNotFoundException("Resume with id " + resumeId + " not found while updating state machine state"));
        return new DefaultStateMachineContext<>(resume.getStatus(), null, null, null);
    }

}
