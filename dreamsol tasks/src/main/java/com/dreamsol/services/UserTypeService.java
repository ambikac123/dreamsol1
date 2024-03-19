package com.dreamsol.services;

import java.util.List;

import com.dreamsol.dto.UserTypeResponseDto;
import com.dreamsol.dto.UserTypeSingleDataResponseDto;
import org.springframework.http.ResponseEntity;

import com.dreamsol.dto.UserTypeRequestDto;
import com.dreamsol.entities.UserType;
import com.dreamsol.response.ApiResponse;
import com.dreamsol.response.UserTypeAllDataResponse;

public interface UserTypeService 
{
	ResponseEntity<ApiResponse> createUserType(UserTypeRequestDto userTypeRequestDto);
	ResponseEntity<ApiResponse> updateUserType(UserTypeRequestDto userTypeRequestDto,Long userTypeId);
	ResponseEntity<ApiResponse> deleteUserType(Long userTypeId);
	ResponseEntity<UserTypeResponseDto> getUserTypeById(Long userTypeId);
	ResponseEntity<UserTypeAllDataResponse> getAllUserTypes(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection);
	ResponseEntity<List<UserTypeRequestDto>> searchUserTypes(String keywords);
	UserTypeRequestDto entityToDto(UserType userType);
	UserType dtoToEntity(UserTypeRequestDto userTypeRequestDto);
}
