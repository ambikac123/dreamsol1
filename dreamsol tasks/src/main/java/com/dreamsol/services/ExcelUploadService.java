package com.dreamsol.services;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.dreamsol.entities.User;

public interface ExcelUploadService 
{
	public List<User> saveExcelFile(MultipartFile file);
	public List<User> getAllExcelData();
}
