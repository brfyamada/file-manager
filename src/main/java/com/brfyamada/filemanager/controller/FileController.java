package com.brfyamada.filemanager.controller;

import com.brfyamada.filemanager.service.CSV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
@CrossOrigin("*")
@Slf4j
public class FileController {

    @Autowired
    private CSV csv;

    @GetMapping
    public String getAllfromFile() {
        return "Olá Mundo";
    }

    @Value("${app.file.path}")
    private String path;

    @PostMapping("/upload")
    public ResponseEntity<String> saveFIle(@RequestParam("file") MultipartFile file){
        try {
            String result = csv.saveFile(file);
            return new ResponseEntity<>("{\"fileName\": \""+ result +"\"}", HttpStatus.OK);
        }catch(IOException ex) {
            return new ResponseEntity<>("{\"error\": \"Erro ao carregar o arquivo!\"}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
