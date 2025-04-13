package share.resume.com.configs.fsm;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import share.resume.com.entities.enums.ResumeEvent;
import share.resume.com.entities.enums.ResumeStatus;

import java.util.EnumSet;
import java.util.UUID;

@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class ResumeStateMachineConfiguration extends StateMachineConfigurerAdapter<ResumeStatus, ResumeEvent> {
    private final StateMachinePersist<ResumeStatus, ResumeEvent, UUID> persist;

    @Override
    public void configure(StateMachineStateConfigurer<ResumeStatus, ResumeEvent> states) throws Exception {
        states
                .withStates()
                .initial(ResumeStatus.WAITING_FOR_APPROVE)
                .states(EnumSet.allOf(ResumeStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ResumeStatus, ResumeEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(ResumeStatus.WAITING_FOR_APPROVE)
                .target(ResumeStatus.APPROVED)
                .event(ResumeEvent.APPROVED)
                .and()
                .withExternal()
                .source(ResumeStatus.WAITING_FOR_APPROVE)
                .target(ResumeStatus.REJECTED)
                .event(ResumeEvent.REJECTED);
    }

    @Bean
    public StateMachinePersister<ResumeStatus, ResumeEvent, UUID> persister() {
        return new DefaultStateMachinePersister<>(persist);
    }
}
