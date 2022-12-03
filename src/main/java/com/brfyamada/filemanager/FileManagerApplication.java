package com.brfyamada.filemanager;

import com.brfyamada.filemanager.service.CSV;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FileManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileManagerApplication.class, args);
		CSV csv = new CSV();
		csv.readFile();
	}

}
