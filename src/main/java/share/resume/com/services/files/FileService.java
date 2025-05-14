package share.resume.com.services.files;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void upload(String directory, MultipartFile file, String fileName);

    void delete(String directory, String filename);

    String getAccessLink(String directory, String filename);
}
