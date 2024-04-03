package com.dreamsol.services;

import com.dreamsol.dto.UserTypeResponseDto;
import com.dreamsol.entities.UserType;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import com.dreamsol.dto.UserTypeRequestDto;
import com.dreamsol.response.ApiResponse;
import com.dreamsol.response.UserTypeAllDataResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserTypeService 
{
	ResponseEntity<ApiResponse> createUserType(UserTypeRequestDto userTypeRequestDto);
	ResponseEntity<ApiResponse> updateUserType(UserTypeRequestDto userTypeRequestDto,Long userTypeId);
	ResponseEntity<ApiResponse> deleteUserType(Long userTypeId);
	ResponseEntity<UserTypeResponseDto> getSingleUserType(Long userTypeId);
	ResponseEntity<UserTypeAllDataResponse> searchUserTypes(Integer pageNumber,Integer pageSize,String sortBy,String sortDirection,String keywords);
	ResponseEntity<?> getCorrectAndIncorrectUserTypeList(MultipartFile excelFile);
	ResponseEntity<?> saveCorrectList(List<UserTypeRequestDto> correctList);
	ResponseEntity<?> downloadDataFromDB();
	ResponseEntity<Resource> getUserTypeExcelSample();

	/* -------------------------- UserType Helper Methods -----------------------------*/

	UserType getUserType(long userTypeId);
	UserType getUserType(UserTypeRequestDto userTypeRequestDto);
	UserTypeResponseDto entityToDto(UserType userType);
	boolean isUserTypeExist(UserTypeRequestDto userTypeRequestDto);

    UserType getUserType(String userTypeName, String userTypeCode);
}
/*
ResponseEntity<UserTypeAllDataResponse> getAllUserTypes(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection);
 */