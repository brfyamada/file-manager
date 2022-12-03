package com.brfyamada.filemanager.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class CSV {

    @Value("${app.file.path}")
    private String path;

    // Read file from resources
    public void readFile() {
        String line = "";
        String splitBy = ",";
        InputStream input = getClass().getClassLoader().getResourceAsStream("file.csv");
        BufferedReader file = new BufferedReader(new InputStreamReader(input));
        try {
            while((line = file.readLine()) != null) {
                String[] employee = line.split(splitBy);
                //use comma as separator
                System.out.println("[Id=" + employee[0] + ", Name=" + employee[1] + ", Year of birth=" + employee[2] + "]");
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    //Saving file to server folder
    public String saveFile(MultipartFile file) throws IOException {
        log.info("Recebendo o arquivo", file.getOriginalFilename());
        var fileName = UUID.randomUUID() + "." + extrairExtensao(file.getOriginalFilename());
        var fileCompletedPath = path + fileName;
        log.info("Nome do arquivo: " + fileCompletedPath);
        try {
            Files.copy(file.getInputStream(), Path.of(fileCompletedPath), StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex){
            log.error("Error saving file: ", ex);
            throw ex;
        }

    }

    private String extrairExtensao(String originalFilename) {
        int i = originalFilename.lastIndexOf(".");
        return originalFilename.substring(i + 1);
    }
}
