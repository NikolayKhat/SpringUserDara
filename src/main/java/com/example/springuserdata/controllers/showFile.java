package com.example.springuserdata.controllers;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@Slf4j
@Data
@Controller
@RequestMapping("/showfile")
public class showFile {

    @Value("${upload.path}")
    private String uploadPath;

    @GetMapping
    public ResponseEntity<InputStreamResource> fileView(@RequestParam("uploadPath") String filePath) {
        File file = new File(filePath);
        InputStreamResource resource = null;
        try {
            resource = new InputStreamResource(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, file.getName())
                .contentType(MediaType.parseMediaType("application/pdf"))
                .contentLength(file.length())
                .body(resource);

    }
}
