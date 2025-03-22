package share.resume.com.async.transactions.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResumeDeletedEvent {
    private String publicFileName;
    private String privateFileName;
    private String directory;
}
