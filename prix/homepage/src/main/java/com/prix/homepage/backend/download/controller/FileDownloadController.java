package com.prix.homepage.backend.download.controller;

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

import static com.prix.homepage.backend.basic.utils.PathUtil.PATH_SW_RELEASE;


@Controller
@RequestMapping("/download/software")
public class FileDownloadController {

    /**
     * URL 접속을 통해 소프트웨어를 직접 다운로드
     * @param fileName 다운로드 받을 파일 명.
     * @return 파일이 존재할 경우 파일 다운로드.
     */
    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> downloadFileFromExternal(@PathVariable String fileName) {
        try {
            // 외부 경로에서 파일을 찾음
            Path filePath = Paths.get(PATH_SW_RELEASE).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                // 파일이 존재할 경우 다운로드 처리
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                // 파일이 없을 경우 404 에러
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
