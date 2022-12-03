package com.brfyamada.filemanager.service;

import com.brfyamada.filemanager.exceptions.FileNotFoundException;
import com.brfyamada.filemanager.exceptions.FileStorageException;
import com.brfyamada.filemanager.property.FileStorageProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.file.path}")
    private String path;

    private final Path fileStorageLocation;
    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();

        try{
            Files.createDirectories(this.fileStorageLocation);
        } catch( Exception ex){
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

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

    public String storeFile(MultipartFile file) {
        log.info("Recebendo o arquivo", file.getOriginalFilename());
        var fileName = UUID.randomUUID() + "." + extrairExtensao(file.getOriginalFilename());
        var fileCompletedPath = path + fileName;
        log.info("Nome do arquivo: " + fileCompletedPath);
        try {
            Files.copy(file.getInputStream(), Path.of(fileCompletedPath), StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex){
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName, ex);
        }
    }

    private String extrairExtensao(String originalFilename) {
        int i = originalFilename.lastIndexOf(".");
        return originalFilename.substring(i + 1);
    }


    //Not used
    public String storeFileOriginalName(MultipartFile file) {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }



    }
}
