package com.prix.homepage.backend.admin;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UploadForm {
    private String name;
    private MultipartFile file;
}