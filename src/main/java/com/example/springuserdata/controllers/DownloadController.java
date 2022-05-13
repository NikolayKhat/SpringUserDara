package com.example.springuserdata.controllers;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Slf4j
@Data
@Controller
@RequestMapping("/download")
public class DownloadController {

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping
    public ResponseEntity<InputStreamResource> fileView(@RequestParam("fileName") String filePath)
            throws FileNotFoundException {
        File file = new File(uploadPath + "/" + filePath);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(uploadPath + "/" + filePath));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName(filePath) + "\"")
                .contentType(MediaType.parseMediaType("application/pdf"))
                .contentLength(file.length())
                .body(resource);
    }

    /* Возвращает исходное название файла */
    private String fileName(String str) {
        StringBuilder name = new StringBuilder(str);
        name = name.delete(0, name.indexOf(".") + 1);
        return name.toString();
    }
}
