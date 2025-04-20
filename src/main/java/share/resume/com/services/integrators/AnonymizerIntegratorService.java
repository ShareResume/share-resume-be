package share.resume.com.services.integrators;

import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import share.resume.com.exceptions.HttpException;
import share.resume.com.util.CustomMultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnonymizerIntegratorService {
    private final APIService apiService;

    @Value("${anonymizer.integrator.url}")
    private String integratorUrl;

    @Value("${anonymizer.integrator.token}")
    private String integratorToken;

    public MultipartFile anonymize(MultipartFile file) {
        Map<String, String> headers = new HashMap<>();
        headers.put("authorization-token", integratorToken);
        headers.put("Content-Type", "multipart/form-data");

        Map<String, Object> body = new HashMap<>();
        body.put("document", file);

        Map<String, Object> response = apiService.sendPostRequest(
                integratorUrl,
                Collections.emptyMap(),
                headers,
                body,
                MediaType.parse("multipart/form-data")
        );

        String responseFile = (String) response.get("file");
        if (responseFile == null) {
            throw new HttpException("Anonymizer integrator service returned null response");
        }

        return new CustomMultipartFile(
                "PRIVATE-" + file.getOriginalFilename(),
                "PRIVATE-" + file.getOriginalFilename(),
                file.getContentType(),
                responseFile.getBytes()
        );
    }
}

