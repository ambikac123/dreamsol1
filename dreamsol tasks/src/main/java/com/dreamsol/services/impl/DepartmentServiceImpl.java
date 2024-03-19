package com.dreamsol.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.dreamsol.dto.*;
import com.dreamsol.entities.User;
import com.dreamsol.exceptions.InvalidInputFormat;
import com.dreamsol.helpers.GlobalHelper;
import com.dreamsol.helpers.UserHelper;
import com.dreamsol.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.dreamsol.entities.Department;
import com.dreamsol.entities.UserType;
import com.dreamsol.exceptions.ResourceAlreadyExistException;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.DepartmentRepository;
import com.dreamsol.repositories.UserTypeRepository;
import com.dreamsol.response.ApiResponse;
import com.dreamsol.response.DepartmentAllDataResponse;
import com.dreamsol.services.DepartmentService;

import javax.swing.text.html.parser.Entity;
import javax.xml.ws.Response;

@Service
public class DepartmentServiceImpl implements DepartmentService
{
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired private UserTypeRepository userTypeRepository;
    @Autowired private UserRepository userRepository;
	@Autowired private ModelMapper modelMapper;
	
	public ResponseEntity<ApiResponse> createDepartment(DepartmentRequestDto departmentRequestDto)
	{
		try
		{
			if(Objects.isNull(departmentRequestDto))
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Missing input data!",false));

			String departmentName = departmentRequestDto.getDepartmentName();
			String departmentCode = departmentRequestDto.getDepartmentCode();
			if(GlobalHelper.isValidName(departmentName) && GlobalHelper.isValidCode(departmentCode))
			{
				Department department = departmentRepository.findByDepartmentName(departmentName);
				if(Objects.isNull(department))
				{
					department = departmentRepository.findByDepartmentCode(departmentCode);
					if(Objects.isNull(department))
					{
						department = new Department();
						BeanUtils.copyProperties(departmentRequestDto,department);
						departmentRepository.save(department);
						return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("New department added successfully!",true));
					}
					throw new ResourceAlreadyExistException("department code");
					//return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Department code already exists!",false));
				}
				throw new ResourceAlreadyExistException("department name");
				//return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("Department name already exists!",false));
			}
			throw new InvalidInputFormat("department name/code");
			//return ResponseEntity.ok(new ApiResponse("Invalid department name/code!",false));
		}catch(RuntimeException e)
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(),false));
		}
	}

	public ResponseEntity<ApiResponse> updateDepartment(DepartmentRequestDto departmentRequestDto, Long departmentId)
	{
		try {
			if(Objects.isNull(departmentRequestDto)) {
				throw new NullPointerException("Error: Null value encountered. Please ensure that all required data is properly initialized and provided.");
            }
            Department department = departmentRepository.findById(departmentId).orElseThrow(()->new ResourceNotFoundException("Department","departmentId",departmentId));
            BeanUtils.copyProperties(departmentRequestDto,department);
            departmentRepository.save(department);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Department with id "+departmentId+" updated successfully!",true));
		}catch(RuntimeException e)
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Error occured : department with id "+departmentId+" not updated! ["+e.getMessage()+"]",false));
		}
	}
	
	public ResponseEntity<ApiResponse> deleteDepartment(Long departmentId)
    {
        try {
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "departmentId", departmentId));
            List<User> userList = department.getUsers();
			for(User user : userList)
			{

				user.setDepartment(null);
			}
			department.setUsers(userList);
			departmentRepository.delete(department);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("department with id "+departmentId+" deleted successfully!", true));
        }catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Error occured : department with id "+departmentId+" not deleted! ["+e.getMessage()+"]",false));
        }
	}

	public ResponseEntity<DepartmentResponseDto> getDepartmentById(Long departmentId) {
		Department department = departmentRepository.findById(departmentId)
				.orElseThrow(()->new ResourceNotFoundException("Department","Id",departmentId));

		DepartmentResponseDto departmentResponseDto = new DepartmentResponseDto();
		BeanUtils.copyProperties(department, departmentResponseDto);
		List<User> users = department.getUsers();
		List<UserSingleDataResponseDto> userSingleDataResponseDtoList = users.stream()
				.map((user)->UserHelper.userToUserSingleDataResponseDto(user))
				.collect(Collectors.toList());
		departmentResponseDto.setUsers(userSingleDataResponseDtoList);

		return ResponseEntity.status(HttpStatus.OK).body(departmentResponseDto);
	}

	public ResponseEntity<DepartmentAllDataResponse> getAllDepartments(Integer pageNumber, Integer pageSize,String sortBy,String sortDirection) 
	{
		Sort sort = null;
		if(sortDirection.equalsIgnoreCase("asc"))
			sort = Sort.by(sortBy).ascending();
		else if(sortDirection.equalsIgnoreCase("desc"))
			sort = Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber,pageSize,sort);
		Page<Department> page = departmentRepository.findAll(pageable);
		
		List<Department> departmentList = page.getContent();
		List<DepartmentRequestDto> departmentDtoList = departmentList.stream().map((department)->this.entityToDto(department)).collect(Collectors.toList());
		DepartmentAllDataResponse departmentAllDataResponse = new DepartmentAllDataResponse();
		departmentAllDataResponse.setContents(departmentDtoList);
		departmentAllDataResponse.setPageNumber(page.getNumber());
		departmentAllDataResponse.setPageSize(page.getSize());
		departmentAllDataResponse.setTotalElements(page.getTotalElements());
		departmentAllDataResponse.setTotalPages(page.getTotalPages());
		departmentAllDataResponse.setFirstPage(page.isFirst());
		departmentAllDataResponse.setLastPage(page.isLast());
		return ResponseEntity.status(HttpStatus.OK).body(departmentAllDataResponse);
	}
	
	public ResponseEntity<List<DepartmentRequestDto>> searchDepartments(String keyword) {
		
		List<Department> departmentList = departmentRepository.findByName("%"+keyword+"%");
		 
		if(departmentList.isEmpty())
		{
			departmentList = departmentRepository.findByCode("%"+keyword+"%");
		}
		List<DepartmentRequestDto> departmentDtoList = departmentList.stream().map((department)-> entityToDto(department)).collect(Collectors.toList());
		return ResponseEntity.ok(departmentDtoList);
	}
	
	public Department dtoToEntity(DepartmentRequestDto departmentDto)
	{
		Department department = modelMapper.map(departmentDto, Department.class);	
		return department;
	}
	
	public DepartmentRequestDto entityToDto(Department department)
	{
		DepartmentRequestDto departmentDto = modelMapper.map(department, DepartmentRequestDto.class);
		return departmentDto;
	}
}
