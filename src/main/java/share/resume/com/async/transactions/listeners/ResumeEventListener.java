package share.resume.com.async.transactions.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.multipart.MultipartFile;
import share.resume.com.async.transactions.events.ResumeCreatedEvent;
import share.resume.com.async.transactions.events.ResumeDeletedEvent;
import share.resume.com.services.files.FileService;

@Component
@RequiredArgsConstructor
@Slf4j
public class ResumeEventListener {
    private final FileService fileService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onResumeCreated(ResumeCreatedEvent event) {
        try {
            String directory = event.getDirectoryName();
            MultipartFile cvPublicFile = event.getCvFilePublic();
            fileService.upload(directory, cvPublicFile, cvPublicFile.getOriginalFilename());
            fileService.upload(directory, event.getCvFilePrivate(), event.getCvFilePrivate().getOriginalFilename());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onResumeDeleted(ResumeDeletedEvent event) {
        try {
            String directory = event.getDirectory();
            fileService.delete(directory, event.getPublicFileName());
            fileService.delete(directory, event.getPrivateFileName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
}
