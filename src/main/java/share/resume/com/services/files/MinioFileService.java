package share.resume.com.services.files;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import share.resume.com.exceptions.FileException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioFileService implements FileService {
    private final MinioClient minioClient;

    @Value("${minio.download.link}")
    private String downloadLink;

    @Override
    public void upload(String directory, MultipartFile file, String fileName) {
        try {
            BucketExistsArgs bucketExistsArgs = BucketExistsArgs.builder()
                    .bucket(directory)
                    .build();
            if (!minioClient.bucketExists(bucketExistsArgs)) {
                MakeBucketArgs makeBucketArgs = MakeBucketArgs.builder()
                        .bucket(directory)
                        .build();
                minioClient.makeBucket(makeBucketArgs);
            }
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(directory)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .object(fileName)
                    .build();
            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new FileException("Error during uploading file to MinIO");
        }
    }

    @Override
    public void delete(String directory, String filename) {
        try {
            RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder()
                    .bucket(directory)
                    .object(filename)
                    .build();
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new FileException("Error during deleting file from MinIO");
        }
    }

    @Override
    public String getAccessLink(String directory, String filename) {
        try {
            GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                    .bucket(directory)
                    .object(filename)
                    .build();
            return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new FileException("Error during retrieving access link from MinIO");
        }
    }
}
