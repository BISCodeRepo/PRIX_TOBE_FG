package com.prix.homepage.backend.download;

import com.prix.homepage.frontend.controller.BaseController;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;

@Controller
@RequestMapping("/download/releasenote")
public class ReleaseNoteController extends BaseController {

    @GetMapping()
    public ResponseEntity<Resource> getReleaseNote(
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestHeader(value = "Referer", required = false) String referer
            ) throws IOException {

        // 파일 이름이 없을 경우 예외처리
        if (fileName == null || fileName.isEmpty()) {
            if (referer != null && !referer.isEmpty()) {
                return ResponseEntity.status(302).header("Location", referer).build();
            }
            return ResponseEntity.status(302).header("Location", "/").build();
        }

        // 파일 경로 설정 (동적으로 전달받은 파일 이름 사용)
        String filePath = "static/releasenote/" + fileName + ".txt";
        Resource resource = new ClassPathResource(filePath);

        // 파일이 존재하지 않으면 예외 처리
        if (!resource.exists()) {
            if (referer != null && !referer.isEmpty()) {
                return ResponseEntity.status(302).header("Location", referer).build();
            }
            return ResponseEntity.status(302).header("Location", "/").build();
        }

        // 파일의 MIME 타입 설정
        String contentType = Files.probeContentType(resource.getFile().toPath());

        // 파일을 HTTP 응답으로 반환
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : MediaType.TEXT_PLAIN_VALUE)
                .body(resource);
    }



}
