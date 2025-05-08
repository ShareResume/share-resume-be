package share.resume.com.services.integrators;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import share.resume.com.exceptions.HttpException;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class APIService {
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public Map<String, Object> sendGetRequest(String url, Map<String, Object> requestParameters, Map<String, String> headers) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (Map.Entry<String, Object> entry : requestParameters.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
        }
        String finalUrl = urlBuilder.build().toString();

        log.info("Sending GET request to " + finalUrl);

        Headers requestHeaders = Headers.of(headers);
        Request request = new Request.Builder()
                .url(finalUrl)
                .headers(requestHeaders)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                log.error("Error sending GET request to url: {}, response: {}", finalUrl, responseBody);
                throw new HttpException("Response isn't successful for url request: " + finalUrl);
            }
            log.info("Response is successful for url request: " + finalUrl);
            return objectMapper.readValue(responseBody, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new HttpException("Error during request sending");
        }
    }

    public Map<String, Object> sendPostRequest(
            String url,
            Map<String, Object> requestParameters,
            Map<String, String> headers,
            Map<String, Object> body,
            MediaType mediaType) {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        for (Map.Entry<String, Object> entry : requestParameters.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
        }
        String finalUrl = urlBuilder.build().toString();

        log.info("Sending POST request to " + finalUrl);

        Headers requestHeaders = Headers.of(headers);
        RequestBody requestBody;
        boolean isMultipart = false;
        if (org.springframework.http.MediaType.MULTIPART_FORM_DATA.toString().equals(mediaType.toString())) {
            isMultipart = true;
            MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            for (Map.Entry<String, Object> entry : body.entrySet()) {
                if (entry.getValue() instanceof MultipartFile) {
                    MultipartFile file = (MultipartFile) entry.getValue();
                    try {
                        multipartBuilder.addFormDataPart(
                                entry.getKey(),
                                file.getOriginalFilename(),
                                RequestBody.create(file.getBytes(), MediaType.parse(file.getContentType()))
                        );
                    } catch (Exception e) {
                        log.error("Error reading file data", e);
                        throw new HttpException("Error processing file: " + file.getOriginalFilename());
                    }
                } else {
                    multipartBuilder.addFormDataPart(entry.getKey(), String.valueOf(entry.getValue()));
                }
            }
            requestBody = multipartBuilder.build();
        } else {
            try {
                String jsonBody = objectMapper.writeValueAsString(body);
                requestBody = RequestBody.create(jsonBody, mediaType);
            } catch (Exception e) {
                log.error("Error serializing request body", e);
                throw new HttpException("Error serializing request body");
            }
        }

        Request request = new Request.Builder()
                .url(finalUrl)
                .headers(requestHeaders)
                .post(requestBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            if (!response.isSuccessful()) {
                log.error("Error sending POST request to url: {}, response: {}", finalUrl, responseBody);
                throw new HttpException("Response isn't successful for url request: " + finalUrl);
            }
            log.info("Response is successful for url request: " + finalUrl);
            if (isMultipart) {
                return Map.of("file", responseBody);
            }
            return objectMapper.readValue(responseBody, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new HttpException("Error during request sending to url: " + url);
        }
    }

}
