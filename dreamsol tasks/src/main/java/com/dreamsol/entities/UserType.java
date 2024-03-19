package com.dreamsol.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "usertype")
public class UserType 
{
	@Id
	@GeneratedValue(generator = "usertype_seq")
	@SequenceGenerator(name = "usertype_seq", initialValue = 101, allocationSize = 1)
	@Column(name = "usertype_id",nullable = false)
	@Schema(hidden = true)
	private long userTypeId;

	@Column(name = "usertype_name", length = 100,nullable = false,unique = true)
	private String userTypeName;

	@Column(name = "usertype_code", length = 100,nullable = false,unique = true)
	private String userTypeCode;

	@OneToMany(mappedBy = "userType")
	private List<User> users;

}
