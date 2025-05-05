package com.midasdev.mybg.global.s3;

import com.midasdev.mybg.global.exception.ApplicationException;
import com.midasdev.mybg.global.exception.ApplicationExceptionType;
import com.midasdev.mybg.global.url.ResourceUrlGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3ImageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif");
    private static final long MAX_FILE_SIZE = 3 * 1024 * 1024; // 3MB

    private final S3Client s3Client;
    private final ResourceUrlGenerator resourceUrlGenerator;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String upload(MultipartFile multipartFile, S3Directory directory) {
        validateImageFile(multipartFile);

        String fileName = createFileName(multipartFile.getOriginalFilename());
        String filePath = directory.getDirectory() + "/" + fileName;

        try (InputStream inputStream = multipartFile.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                                                                .bucket(bucketName)
                                                                .key(filePath)
                                                                .contentType(multipartFile.getContentType())
                                                                .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, multipartFile.getSize()));
        } catch (IOException e) {
            throw new ApplicationException(ApplicationExceptionType.S3_FILE_UPLOAD_EXCEPTION, e);
        }

        return resourceUrlGenerator.generateS3Url(directory.getDirectory(), fileName);
    }

    private void validateImageFile(MultipartFile multipartFile) {
        String contentType = multipartFile.getContentType();
        String originalFilename = multipartFile.getOriginalFilename();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ApplicationException(ApplicationExceptionType.S3_FILE_FORMAT_EXCEPTION, "지원하지 않는 파일 형식입니다.");
        }

        if (originalFilename == null || !hasAllowedExtension(originalFilename)) {
            throw new ApplicationException(ApplicationExceptionType.S3_FILE_FORMAT_EXCEPTION, String.format("파일이름이 없거나 허용되지 않는 확장자입니다.(%s)", originalFilename));
        }

        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            throw new ApplicationException(ApplicationExceptionType.S3_FILE_MAX_SIZE_EXCEPTION);
        }
    }

    private boolean hasAllowedExtension(String fileName) {
        String extension = getFileExtension(fileName).toLowerCase();
        return ALLOWED_EXTENSIONS.contains(extension);
    }

    private String createFileName(String originalFileName) {
        String extension = getFileExtension(originalFileName);
        return UUID.randomUUID() + extension;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            throw new ApplicationException(ApplicationExceptionType.S3_FILE_FORMAT_EXCEPTION, "파일 이름이 없습니다.");
        }
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1) {
            throw new ApplicationException(ApplicationExceptionType.S3_FILE_FORMAT_EXCEPTION, "파일 확장자가 없습니다.");
        }
        return fileName.substring(dotIndex);
    }
}
