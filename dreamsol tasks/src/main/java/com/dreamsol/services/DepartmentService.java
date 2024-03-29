package com.dreamsol.services;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.dreamsol.dto.DepartmentRequestDto;
import com.dreamsol.dto.DepartmentResponseDto;
import com.dreamsol.response.ApiResponse;
import com.dreamsol.response.DepartmentAllDataResponse;

public interface DepartmentService
{
	ResponseEntity<ApiResponse> createDepartment(DepartmentRequestDto departmentDto);
	
	ResponseEntity<ApiResponse> updateDepartment(DepartmentRequestDto departmentDto,Long departmentId);
	
	ResponseEntity<DepartmentResponseDto> getDepartmentById(Long departmentId);
	
	ResponseEntity<DepartmentAllDataResponse> getAllDepartments(Integer pageNumber,Integer pageSize, String sortBy,String sortDirection);
	
	ResponseEntity<ApiResponse> deleteDepartment(Long departmentId);

	ResponseEntity<List<DepartmentRequestDto>> searchDepartments(String keyword);
}
