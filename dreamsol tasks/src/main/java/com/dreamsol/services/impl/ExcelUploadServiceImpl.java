package com.dreamsol.services.impl;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dreamsol.entities.User;
import com.dreamsol.helpers.UserExcelHelper;
import com.dreamsol.repositories.UserRepository;
import com.dreamsol.services.ExcelUploadService;

@Service
public class ExcelUploadServiceImpl implements ExcelUploadService
{
	@Autowired private UserRepository userRepository;
	public List<User> saveExcelFile(MultipartFile file)
	{
		List<User> users=null;
		try
		{
			users = UserExcelHelper.convertExcelToListOfUsers(file.getInputStream());

			//userRepository.saveAll(users);
		}catch(IOException e)
		{
			System.out.println(e.getMessage());
		}
		return users;
	}
	
	public List<User> getAllExcelData()
	{
		return userRepository.findAll();
	}
}
