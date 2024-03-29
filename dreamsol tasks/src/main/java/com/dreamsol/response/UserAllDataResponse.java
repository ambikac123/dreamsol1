package com.dreamsol.response;

import java.util.List;

import com.dreamsol.dto.DepartmentRequestDto;
import com.dreamsol.dto.UserResponseDto;

import com.dreamsol.dto.UserSingleDataResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserAllDataResponse
{
	private List<UserSingleDataResponseDto> contents;
	private int pageNumber;
	private int pageSize;
	private long totalElements;
	private int totalPages;
	private boolean firstPage;
	private boolean lastPage;
}
