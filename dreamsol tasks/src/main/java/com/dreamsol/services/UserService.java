package com.dreamsol.services;

import java.util.List;

import com.dreamsol.dto.UserExcelResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.dreamsol.dto.UserRequestDto;
import com.dreamsol.dto.UserResponseDto;
import com.dreamsol.response.ApiResponse;
import com.dreamsol.response.UserAllDataResponse;

public interface UserService 
{
	ResponseEntity<ApiResponse> createUser(UserRequestDto userRequestDto,String path,MultipartFile file);
	ResponseEntity<Object> getUser(Long userId);
	ResponseEntity<ApiResponse> deleteUser(String path,Long userId);
	ResponseEntity<ApiResponse> updateUser(UserRequestDto userRequestDto, String path, MultipartFile file,Long userId);
	ResponseEntity<Object> getAllUsers(Integer pageNumber, Integer pageSize, String sortBy,String sortDirection);
	ResponseEntity<Object> uploadFile(MultipartFile file);

}
