package com.dreamsol.services.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dreamsol.entities.User;
import com.dreamsol.repositories.UserRepository;
import com.dreamsol.services.ImageUploadService;

@Service
public class ImageUploadServiceImpl implements ImageUploadService
{

	@Autowired
	private UserRepository userRepository; 
	
	@Override
	public String uploadImage(String destinationPath, MultipartFile sourceFile)
	{
		// file name
		String sourceFileName = sourceFile.getOriginalFilename();
		String sourceFileExtension = Objects.requireNonNull(sourceFileName).substring(sourceFileName.lastIndexOf('.'));
		
			// file path
			String randomID = UUID.randomUUID().toString();
			String newFileName = randomID+sourceFileExtension;
			String newFilePath = destinationPath +newFileName;
			
			// create folder not created
			File file = new File(destinationPath);
			if(!file.exists())
			{
				file.mkdir();
			}
			
			// file copy
			try 
			{
					Files.copy(sourceFile.getInputStream(), Paths.get(newFilePath));
			} 
			catch (IOException e) {
				
				e.printStackTrace();
			}
			return newFileName;
	}

	@Override
	public String getResource(String path, String fileName) throws FileNotFoundException,IOException{
		
		String fullPath = path+File.separator+fileName;
		
		InputStream resource =  new FileInputStream(fullPath);
		byte[] imageBytes = resource.readAllBytes();
		resource.close();
		return  Base64.getEncoder().encodeToString(imageBytes);
	}

	@Override
	public boolean deleteImage(String path, String fileName) {
		 Path imagePath = Paths.get(path, fileName);
		 try {
	            // Delete the file
	            Files.delete(imagePath);
	            return true;
	        } catch (IOException e) {
	            e.printStackTrace();
	            return false;
	        }
	}
	
}
