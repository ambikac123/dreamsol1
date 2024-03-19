package com.dreamsol.services.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.dreamsol.dto.UserSingleDataResponseDto;
import com.dreamsol.dto.UserTypeResponseDto;
import com.dreamsol.dto.UserTypeSingleDataResponseDto;
import com.dreamsol.entities.User;
import com.dreamsol.exceptions.InvalidInputFormat;
import com.dreamsol.helpers.GlobalHelper;
import com.dreamsol.helpers.UserHelper;
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

import com.dreamsol.dto.UserTypeRequestDto;
import com.dreamsol.entities.UserType;
import com.dreamsol.exceptions.ResourceAlreadyExistException;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.UserTypeRepository;
import com.dreamsol.response.ApiResponse;
import com.dreamsol.response.UserTypeAllDataResponse;
import com.dreamsol.services.UserTypeService;

@Service
public class UserTypeServiceImpl implements UserTypeService
{
	@Autowired private UserTypeRepository userTypeRepository;
	
	@Autowired private ModelMapper modelMapper;
	
	public ResponseEntity<ApiResponse> createUserType(UserTypeRequestDto userTypeRequestDto)
	{
		try
		{
			if(Objects.isNull(userTypeRequestDto))
				throw new NullPointerException("User type request data is missing. Please provide valid input data.");
			String userTypeName = userTypeRequestDto.getUserTypeName();
			String userTypeCode = userTypeRequestDto.getUserTypeCode();
			if(GlobalHelper.isValidName(userTypeName) && GlobalHelper.isValidCode(userTypeCode))
			{
				UserType userType = userTypeRepository.findByUserTypeName(userTypeName);
				if(Objects.isNull(userType))
				{
					userType = userTypeRepository.findByUserTypeCode(userTypeCode);
					if(Objects.isNull(userType))
					{
						userType = new UserType();
						userType.setUserTypeName(userTypeName);
						userType.setUserTypeCode(userTypeCode);
						userTypeRepository.save(userType);
						return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("New usertype added successfully!",true));
					}
					throw new ResourceAlreadyExistException("usertype code");
				}
				throw new ResourceAlreadyExistException("usertype name");
			}
			throw new InvalidInputFormat("usertype name/code");
		}catch(RuntimeException e)
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(),false));
		}
	}

	public ResponseEntity<ApiResponse> updateUserType(UserTypeRequestDto userTypeRequestDto,Long userTypeId)
	{
		try{
			if (Objects.isNull(userTypeRequestDto)) {
				throw new NullPointerException("Error: Null value encountered. Please ensure that all required data is properly initialized and provided.");
			}
			UserType userType = userTypeRepository.findById(userTypeId).orElseThrow(() -> new ResourceNotFoundException("UserType", "userTypeId", userTypeId));
			BeanUtils.copyProperties(userTypeRequestDto, userType);
			userTypeRepository.save(userType);
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("usertype with id "+userTypeId+" updated successfully!",true));
		}catch(RuntimeException e)
		{
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Error occured : usertype with id "+userTypeId+" not updated! ["+e.getMessage()+"]",false));
		}
	}
	
	public ResponseEntity<ApiResponse> deleteUserType(Long userTypeId)
	{
		try{
			UserType userType = userTypeRepository.findById(userTypeId)
					.orElseThrow(()->new ResourceNotFoundException("UserType","userTypeId",userTypeId));
			List<User> userList = userType.getUsers();
			for(User user : userList)
			{
				//user.getUserType().setUserTypeId(0);
				user.setUserType(null);
			}
			userType.setUsers(userList);
			userTypeRepository.delete(userType);
			return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("usertype with id "+userTypeId+" deleted successfully!", true));
		}catch(RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Error occured : usertype with id "+userTypeId+" not deleted! ["+e.getMessage()+"]",false));
		}
	}
	
	public ResponseEntity<UserTypeResponseDto> getUserTypeById(Long userTypeId)
	{
		UserType userType = userTypeRepository.findById(userTypeId).orElseThrow(()->new ResourceNotFoundException("UserType","userTypeId",userTypeId));
		UserTypeResponseDto userTypeResponseDto = new UserTypeResponseDto();
		BeanUtils.copyProperties(userType,userTypeResponseDto);

		List<User> users = userType.getUsers();
		List<UserSingleDataResponseDto> userSingleDataResponseDtoList = users.stream()
				.map((user)-> UserHelper.userToUserSingleDataResponseDto(user))
				.collect(Collectors.toList());
		userTypeResponseDto.setUsers(userSingleDataResponseDtoList);
		return ResponseEntity.status(HttpStatus.OK).body(userTypeResponseDto);
	}

	public ResponseEntity<UserTypeAllDataResponse> getAllUserTypes(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) 
	{
		Sort sort = null;
		if(sortDirection.equalsIgnoreCase("asc"))
			sort = Sort.by(sortBy).ascending();
		else
			sort = Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(pageNumber,pageSize, sort);
		Page<UserType> page = userTypeRepository.findAll(pageable);
		
		List<UserType> userTypeList = page.getContent();
		List<UserTypeRequestDto> userTypeDtoList = userTypeList.stream().map((userType)->entityToDto(userType)).collect(Collectors.toList());
		UserTypeAllDataResponse userTypeAllDataResponse = new UserTypeAllDataResponse();
		userTypeAllDataResponse.setContents(userTypeDtoList);
		userTypeAllDataResponse.setPageNumber(page.getNumber());
		userTypeAllDataResponse.setPageSize(page.getSize());
		userTypeAllDataResponse.setTotalElements(page.getTotalElements());
		userTypeAllDataResponse.setTotalPages(page.getTotalPages());
		userTypeAllDataResponse.setFirstPage(page.isFirst());
		userTypeAllDataResponse.setLastPage(page.isLast());
		return ResponseEntity.status(HttpStatus.OK).body(userTypeAllDataResponse);
	}

	public ResponseEntity<List<UserTypeRequestDto>> searchUserTypes(String keywords) {
		List<UserType> userTypeList = userTypeRepository.findByName("%"+keywords+"%");
		if(userTypeList.isEmpty())
		{
			userTypeList = userTypeRepository.findByCode("%"+keywords+"%");
		}
		List<UserTypeRequestDto> userTypeDtoList = userTypeList.stream().map((userType)->entityToDto(userType)).collect(Collectors.toList());
		return ResponseEntity.ok(userTypeDtoList);
	}
	public UserTypeRequestDto entityToDto(UserType userType) 
	{
        UserTypeRequestDto userTypeRequestDto = modelMapper.map(userType, UserTypeRequestDto.class);
        return userTypeRequestDto;
    }

    public UserType dtoToEntity(UserTypeRequestDto userTypeRequestDto) 
    {
        UserType userType = modelMapper.map(userTypeRequestDto, UserType.class);
        return userType;
    }


}
