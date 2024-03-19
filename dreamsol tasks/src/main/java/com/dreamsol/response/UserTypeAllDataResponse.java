package com.dreamsol.response;

import java.util.List;

import com.dreamsol.dto.UserTypeRequestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserTypeAllDataResponse 
{
	private List<UserTypeRequestDto> contents;
	private int pageNumber;
	private int pageSize;
	private long totalElements;
	private int totalPages;
	private boolean firstPage;
	private boolean lastPage;
}
