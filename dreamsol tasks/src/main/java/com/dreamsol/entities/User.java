package com.dreamsol.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user")
public class User 
{
	@Id
	@GeneratedValue(generator = "user_seq")
	@SequenceGenerator(name = "user_seq", initialValue = 1, allocationSize = 1)
	@Column(name = "user_id",nullable = false)
	@Schema(hidden = true)
	private long userId;
	
	@Column(name = "user_name", length = 100,nullable = false)
	private String userName;
	
	@Column(name = "user_email", length = 100,unique = true,nullable = false)
	private String userEmail;
	
	@Column(name = "user_mobile", length = 10, unique = true,nullable = false)
	private long userMobile;
	
	@Column(name = "user_image",unique = true)
	private String imageURI;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usertype_id",nullable = true)
	private UserType userType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department_id",nullable = true)
	private Department department;

}


