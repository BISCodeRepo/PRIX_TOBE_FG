package com.prix.homepage.backend.download;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/download/software")
public class FileDownloadController {

    private final String FILE_BASE_PATH = "src/main/resources/static/software";

//    @GetMapping("/{fileName:.+}")
//    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
//        try {
//            // 파일 경로 설정
//            Path filePath = Paths.get(FILE_BASE_PATH).resolve(fileName).normalize();
//            Resource resource = new UrlResource(filePath.toUri());
//
//            if (resource.exists()) {
//                // 파일이 존재할 경우 다운로드 처리
//                return ResponseEntity.ok()
//                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                        .body(resource);
//            } else {
//                // 파일이 존재하지 않을 경우 처리
//                return ResponseEntity.notFound().build();
//            }
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().build();
//        }
//    }

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> downloadFileFromInternal(@PathVariable String fileName) {
        // Spring Boot 내부의 static 폴더에서 파일을 가져옴
        Resource resource = new ClassPathResource("static/software/" + fileName);

        if (resource.exists()) {
            // 파일이 존재할 경우 다운로드 처리
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            // 파일이 없을 경우 404 에러
            return ResponseEntity.notFound().build();
        }
    }
}
