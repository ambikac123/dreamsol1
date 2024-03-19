package com.dreamsol.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.dreamsol.dto.*;
import com.dreamsol.entities.Department;
import com.dreamsol.entities.UserType;
import com.dreamsol.helpers.GlobalHelper;
import com.dreamsol.helpers.UserExcelHelper;
import com.dreamsol.helpers.UserHelper;
import com.dreamsol.repositories.DepartmentRepository;
import com.dreamsol.repositories.UserTypeRepository;
import org.apache.poi.UnsupportedFileFormatException;
import org.apache.tomcat.util.http.fileupload.InvalidFileNameException;
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
import org.springframework.web.multipart.MultipartFile;

import com.dreamsol.entities.User;
import com.dreamsol.exceptions.ResourceAlreadyExistException;
import com.dreamsol.exceptions.ResourceNotFoundException;
import com.dreamsol.repositories.UserRepository;
import com.dreamsol.response.ApiResponse;
import com.dreamsol.response.UserAllDataResponse;
import com.dreamsol.services.ExcelUploadService;
import com.dreamsol.services.ImageUploadService;
import com.dreamsol.services.UserService;

@Service
public class UserServiceImpl implements UserService
{
	@Autowired private UserRepository userRepository;
	@Autowired private DepartmentRepository departmentRepository;
	@Autowired private UserTypeRepository userTypeRepository;
	@Autowired private ModelMapper modelMapper;
	@Autowired private ImageUploadService imageUploadService;
	@Autowired private ExcelUploadService excelUploadService;
	
	// To create new user
	public ResponseEntity<ApiResponse> createUser(UserRequestDto userRequestDto,String path,MultipartFile file) 
	{
		try
        {
            String fileName= file.getOriginalFilename();
            if(Objects.isNull(fileName) || Objects.isNull(userRequestDto))
            {
                throw new NullPointerException("missing input data!");
            }
            User user = userRepository.findByUserMobile(userRequestDto.getUserMobile());
            if(Objects.isNull(user))
            {
                user = userRepository.findByUserEmail(userRequestDto.getUserEmail());
                if(Objects.isNull(user))
                {
                    //user = modelMapper.map(userRequestDto,User.class);
                    user = new User();
                    BeanUtils.copyProperties(userRequestDto,user);

                    String userTypeName = userRequestDto.getUserType().getUserTypeName();
                    String userTypeCode = userRequestDto.getUserType().getUserTypeCode();
                    String departmentName = userRequestDto.getDepartment().getDepartmentName();
                    String departmentCode = userRequestDto.getDepartment().getDepartmentCode();
                    UserType userType = userTypeRepository.findByUserTypeName(userTypeName);
                    if(Objects.isNull(userType))
                    {
                        userType = userTypeRepository.findByUserTypeCode(userTypeCode);
                        if(Objects.isNull(userType))
                            throw new ResourceNotFoundException("usertype with name["+userTypeName+"] and code["+userTypeCode+"] doesn't exist");
                        throw new ResourceNotFoundException("usertype with name["+userTypeName+"] doesn't exist!");
                    }

                    Department department = departmentRepository.findByDepartmentName(departmentName);
                    if(Objects.isNull(department))
                    {
                        department = departmentRepository.findByDepartmentCode(departmentCode);
                        if(Objects.isNull(department))
                            throw new ResourceNotFoundException("department with name["+departmentName+"] and code["+departmentCode+"] doesn't exist");
                        throw new ResourceNotFoundException("department with name["+departmentName+"] doesn't exist!");
                    }

                    user.setUserType(userType);
                    user.setDepartment(department);

                    String fileExtension = fileName.substring(fileName.lastIndexOf('.'));
                    if(fileExtension.equals(".jpg")||fileExtension.equals(".jpeg")||fileExtension.equals(".png"))
                    {
                        String newImageName = imageUploadService.uploadImage(path, file);
                        user.setImageURI(newImageName);
                        try {
                            userRepository.save(user);
                            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("User created successfully!!", true));
                        }catch(RuntimeException e){
                            imageUploadService.deleteImage(path,newImageName);
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("New user not created!, "+e.getMessage(),false));
                        }
                    }
                    throw new InvalidFileNameException("image extension","Invalid file format, only .jpg, .jpeg and .png acceptable");
                }
                throw new ResourceAlreadyExistException("Email");
            }
            throw new ResourceAlreadyExistException("Mobile No.");
		}
        catch(RuntimeException exception)
        {
            return ResponseEntity.ok(new ApiResponse(exception.getMessage(),false));
        }
	}
	
	// To fetch single user detail
	public ResponseEntity<Object> getUser(Long userId)
	{
		User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User","userId",userId));
		UserSingleDataResponseDto userSingleDataResponseDto = new UserSingleDataResponseDto();
		BeanUtils.copyProperties(user, userSingleDataResponseDto);

        UserTypeSingleDataResponseDto userTypeSingleDataResponseDto = new UserTypeSingleDataResponseDto();
        if(!Objects.isNull(user.getUserType())) {
            BeanUtils.copyProperties(user.getUserType(), userTypeSingleDataResponseDto);
        }
        userSingleDataResponseDto.setUserType(userTypeSingleDataResponseDto);

        DepartmentSingleDataResponseDto departmentSingleDataResponseDto = new DepartmentSingleDataResponseDto();
        if(!Objects.isNull(user.getDepartment())) {
            BeanUtils.copyProperties(user.getDepartment(), departmentSingleDataResponseDto);
        }
        userSingleDataResponseDto.setDepartment(departmentSingleDataResponseDto);
		return ResponseEntity.ok(userSingleDataResponseDto);
	}

	// To delete user details
	@Override
	public ResponseEntity<ApiResponse> deleteUser(String path,Long userId) {
		User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User","userId",userId));
		String fileName = user.getImageURI();
		if(fileName.isEmpty())
		{
			userRepository.delete(user);
			return ResponseEntity.ok(new ApiResponse("User deleted successfully!!",true));
		}
		else if(imageUploadService.deleteImage(path, fileName))
		{
			userRepository.delete(user);
			return ResponseEntity.ok(new ApiResponse("User deleted successfully!!",true));
		}
		return ResponseEntity.ok(new ApiResponse("User not deleted !!",false));
	}

	// To update user details
	@Override
	public ResponseEntity<ApiResponse> updateUser(UserRequestDto userRequestDto,String path, MultipartFile file,Long userId) 
	{
		User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User","userId",userId));
		BeanUtils.copyProperties(userRequestDto,user);

        String userTypeName = userRequestDto.getUserType().getUserTypeName();
        UserType userType = userTypeRepository.findByUserTypeName(userTypeName);
        if(!Objects.isNull(userType)) {
            String userTypeCode = userRequestDto.getUserType().getUserTypeCode();
            userType = userTypeRepository.findByUserTypeCode(userTypeCode);
            if(!Objects.isNull(userType)) {
                user.setUserType(userType);
            }
            else {
                throw new ResourceNotFoundException("usertype code[" + userTypeCode + "] doesn't exist!");
            }
        }
        else{
            throw new ResourceNotFoundException("usertype name["+userTypeName+"] doesn't exist!");
        }
        String departmentName = userRequestDto.getDepartment().getDepartmentName();
        Department department = departmentRepository.findByDepartmentName(departmentName);
        if(!Objects.isNull(department)) {
            String departmentCode = userRequestDto.getDepartment().getDepartmentCode();
            department = departmentRepository.findByDepartmentCode(departmentCode);
            if(!Objects.isNull(department)){
                user.setDepartment(department);
            }else{
                throw new ResourceNotFoundException("department code["+departmentCode+"] doesn't exist");
            }
        }
        else{
            throw new ResourceNotFoundException("department name["+departmentName+"] doesn't exist");
        }

		String fileName = file.getOriginalFilename();
		if(!fileName.isEmpty())
		{
			String oldFileName = user.getImageURI();
			if(imageUploadService.deleteImage(path, oldFileName))
			{
				String newImageName = imageUploadService.uploadImage(path, file);
                user.setImageURI(newImageName);
                userRepository.save(user);
				return ResponseEntity.ok(new ApiResponse("User updated successfully!!",true));
			}
		}
        userRepository.save(user);
		return ResponseEntity.ok(new ApiResponse("User updated successfully!!",true));
	}


	@Override
	public ResponseEntity<Object> getAllUsers(Integer pageNumber, Integer pageSize, String sortBy,String sortDirection) {
		//Sort sort = null;
        try{

            Sort sort = null;
            if(sortDirection.equalsIgnoreCase("asc"))
            {
                sort = Sort.by(sortBy).ascending();
            }
            else if(sortDirection.equalsIgnoreCase("desc"))
            {
                sort = Sort.by(sortBy).descending();
            }

            Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
            Page<User> page = userRepository.findAll(pageable);
            List<User> userList = page.getContent();
            if(!userList.isEmpty())
            {
                UserAllDataResponse  userAllDataResponse = new UserAllDataResponse();
                List<UserSingleDataResponseDto> userSingleDataResponseDtoList = new ArrayList<>();
                    userSingleDataResponseDtoList = userList.stream().map(this::userToUserSingleDataResponseDto).collect(Collectors.toList());
                    userAllDataResponse.setContents(userSingleDataResponseDtoList);
                    userAllDataResponse.setPageNumber(page.getNumber());
                    userAllDataResponse.setPageSize(page.getSize());
                    userAllDataResponse.setTotalElements(page.getTotalElements());
                    userAllDataResponse.setTotalPages(page.getTotalPages());
                    userAllDataResponse.setFirstPage(page.isFirst());
                    userAllDataResponse.setLastPage(page.isLast());
                return ResponseEntity.status(HttpStatus.OK).body(userAllDataResponse);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("No users list found! ",false));

        }catch(RuntimeException e)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),false));
        }
	}

	@Override
	public ResponseEntity<Object> uploadFile(MultipartFile file)
	{
        if(!UserExcelHelper.checkExcelFormat(file))
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse("Invalid file format", false));
        }
		List<User> usersList = excelUploadService.saveExcelFile(file);
		UserExcelResponseDto userExcelResponseDto = new UserExcelResponseDto();
		List<SingleUserResponseDto> correctUsersList = new ArrayList<>();
		List<SingleUserResponseDto> incorrectUsersList = new ArrayList<>();
		for(User user : usersList)
		{
            SingleUserResponseDto singleUserResponseDto = new SingleUserResponseDto();
            ApiResponse apiResponse = UserHelper.getUserStatus(user);
            BeanUtils.copyProperties(user,singleUserResponseDto);

            UserTypeSingleDataResponseDto userTypeSingleDataResponseDto = new UserTypeSingleDataResponseDto();
            BeanUtils.copyProperties(user.getUserType(),userTypeSingleDataResponseDto);
            singleUserResponseDto.setUserType(userTypeSingleDataResponseDto);

            DepartmentSingleDataResponseDto departmentSingleDataResponseDto = new DepartmentSingleDataResponseDto();
            BeanUtils.copyProperties(user.getDepartment(),departmentSingleDataResponseDto);
            singleUserResponseDto.setDepartment(departmentSingleDataResponseDto);

            singleUserResponseDto.setResponseMessage(apiResponse);
			if(apiResponse.isSuccess())
			{
				correctUsersList.add(singleUserResponseDto);
			}
			else
			{
				incorrectUsersList.add(singleUserResponseDto);
			}
		}
        userExcelResponseDto.setCorrectUsersList(correctUsersList);
        userExcelResponseDto.setIncorrectUsersList(incorrectUsersList);
		return ResponseEntity.ok(userExcelResponseDto);
    }
	public UserSingleDataResponseDto userToUserSingleDataResponseDto(User user) throws RuntimeException
	{
		UserSingleDataResponseDto  userSingleDataResponseDto = new UserSingleDataResponseDto();
		BeanUtils.copyProperties(user,userSingleDataResponseDto);

		UserTypeSingleDataResponseDto userTypeSingleDataResponseDto = new UserTypeSingleDataResponseDto();
		BeanUtils.copyProperties(user.getUserType(),userTypeSingleDataResponseDto,"users");
		userSingleDataResponseDto.setUserType(userTypeSingleDataResponseDto);

		DepartmentSingleDataResponseDto departmentSingleDataResponseDto = new DepartmentSingleDataResponseDto();
		BeanUtils.copyProperties(user.getDepartment(),departmentSingleDataResponseDto,"users");
		userSingleDataResponseDto.setDepartment(departmentSingleDataResponseDto);

		return userSingleDataResponseDto;
	}
}
