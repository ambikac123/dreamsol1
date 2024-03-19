package com.dreamsol.controllers;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


import com.dreamsol.dto.UserExcelResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dreamsol.dto.UserRequestDto;
import com.dreamsol.dto.UserResponseDto;
import com.dreamsol.response.ApiResponse;
import com.dreamsol.response.UserAllDataResponse;
import com.dreamsol.services.ImageUploadService;
import com.dreamsol.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "This is user API")

public class UserController 
{
	@Autowired private UserService userService;
	@Autowired private ImageUploadService imageUploadService;
	@Value("${project.image}")
	private String path;

/*
	@Value("${project.file}")
	private String file;
*/

    @Operation(
		summary = "Create User",
		description = "It is used to save user data with image into database"
	)
	@PostMapping(path = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse> createUser(
			@Valid @RequestPart("userRequestDto") UserRequestDto userRequestDto,
			@RequestParam("file") MultipartFile file
	)
	{
		return userService.createUser(userRequestDto,path,file);
	}
	
	@Operation(
			summary = "Update User ",
			description = "It is used to modify user data into database"
		)
	@PutMapping(path = "/update/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse> updateUser(
			@Valid 
			@RequestPart("userRequestDto") UserRequestDto userRequestDto,
			@RequestParam("file") MultipartFile file,
			@PathVariable("userId") Long userId
	)
	{
		return userService.updateUser(userRequestDto,path, file, userId);
	}
	
	@Operation(
			summary = "Delete User",
			description = "It is used to delete user data from database"
		)
	@DeleteMapping("/delete/{userId}")
	public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId)
	{
		return userService.deleteUser(path,userId);
	}
	
	@Operation(
			summary = "Get User",
			description = "It is used to get details of an existing user"
		)
	@GetMapping("/get/{userId}")
	public ResponseEntity<Object> getUser(@PathVariable Long userId)
	{
		return userService.getUser(userId);
	}
	
	@Operation(
			summary = "Get All Users",
			description = "It is used to get all users detail from database"
		)
	@GetMapping("/get-all")
	public ResponseEntity<Object> getAllUsers(
			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
			@RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
			@RequestParam(value = "sortBy", defaultValue = "userId", required = false) String sortBy,
			@RequestParam(value = "sortDirection",defaultValue = "asc", required = false) String sortDirection
	)
	{
		return userService.getAllUsers(pageNumber,pageSize,sortBy,sortDirection);
	}
	
	@Operation(
			summary = "Download Image File",
			description = "It is used to download image in string of Base64 format"
		)
	@GetMapping(value = "/download-image/{imageName}" ,produces = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<String> downloadFile(
			@PathVariable("imageName") String imageName, HttpServletResponse response
	) throws IOException, FileNotFoundException
	{
		return ResponseEntity.ok(imageUploadService.getResource(path, imageName));
	}
	
	@Operation(
			summary = "Upload Excel File ",
			description = "It is used to upload an excel file containing users data and inserting the bulk-data into database."
		)
	@PostMapping(value = "/upload-file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Object> uploadFile(@RequestParam("file") MultipartFile file)
	{
		return userService.uploadFile(file);
	}
	
	/*
	 * @GetMapping(value = "/get-excel-data") public ResponseEntity<List<User>>
	 * getAllUsersExcelData() { return
	 * ResponseEntity.ok(excelUploadService.getAllExcelData()); }
	 */
}
