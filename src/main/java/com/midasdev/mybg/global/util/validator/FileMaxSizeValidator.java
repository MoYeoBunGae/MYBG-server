package com.midasdev.mybg.global.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileMaxSizeValidator implements ConstraintValidator<FileMaxSize, MultipartFile> {

    private long maxSize;

    @Override
    public void initialize(FileMaxSize constraintAnnotation) {
        this.maxSize = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // null or 비어있으면 검사 대상 아님
        }
        return file.getSize() <= maxSize;
    }
}
