package com.dreamsol.entities;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "department")
public class Department 
{
	@Id
	@GeneratedValue(generator = "department_seq")
	@SequenceGenerator(name = "department_seq", initialValue = 1001, allocationSize = 1)
	@Column(name = "department_id",nullable = false)
	@Schema(hidden = true)
	private long departmentId;
	
	@Column(name = "department_name", length = 100,nullable = false,unique = true)
	private String departmentName;
	
	@Column(name = "department_code", length = 7,nullable = false,unique = true)
	private String departmentCode;

	@OneToMany(mappedBy = "department")
	private List<User> users;

}
