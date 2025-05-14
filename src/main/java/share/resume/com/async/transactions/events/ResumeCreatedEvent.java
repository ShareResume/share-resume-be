package share.resume.com.async.transactions.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
public class ResumeCreatedEvent {
    private MultipartFile cvFilePublic;
    private String cvPublicFilename;
    private String directoryName;

    private MultipartFile cvFilePrivate;
    private String cvPrivateFilename;
}
