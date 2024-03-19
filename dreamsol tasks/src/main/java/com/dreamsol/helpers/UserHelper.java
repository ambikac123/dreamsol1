package com.dreamsol.helpers;

import com.dreamsol.dto.*;
import com.dreamsol.entities.User;
import com.dreamsol.response.ApiResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.beans.PropertyDescriptor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserHelper
{
    public static void copyPropertiesNonNull(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(target.getClass());
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (propertyDescriptor.getReadMethod() != null &&
                    propertyDescriptor.getWriteMethod() != null &&
                    ReflectionUtils.invokeMethod(propertyDescriptor.getReadMethod(), source) == null) {
                ReflectionUtils.invokeMethod(propertyDescriptor.getWriteMethod(), target, (Object) null);
            }
        }
    }

    public static ApiResponse getUserStatus(User user)
    {
        String userName = user.getUserName();
        long userMobile = user.getUserMobile();
        String userEmail = user.getUserEmail();
        String userTypeName = user.getUserType().getUserTypeName();
        String userTypeCode = user.getUserType().getUserTypeCode();
        String departmentName = user.getDepartment().getDepartmentName();
        String departmentCode = user.getDepartment().getDepartmentCode();
        StringBuilder message = new StringBuilder();
        boolean success = true;
        if(!GlobalHelper.isValidName(userName))
        {
            success = false;
            message.append("Invalid User Name/");
        }
        if(!GlobalHelper.isValidMobile(userMobile))
        {
            success = false;
            message.append("Invalid Mobile No./");
        }
        if(!GlobalHelper.isValidEmail(userEmail))
        {
            success = false;
            message.append("Invalid Email Id, ");
        }
        if(!GlobalHelper.isValidName(userTypeName))
        {
            success = false;
            message.append("Invalid UserType Code, ");
        }
        if(!GlobalHelper.isValidCode(userTypeCode))
        {
            success = false;
            message.append("Invalid UserType Code, ");
        }
        if(!GlobalHelper.isValidName(departmentName))
        {
            success = false;
            message.append("Invalid Department Name, ");
        }
        if(!GlobalHelper.isValidCode(departmentCode))
        {
            success = false;
            message.append("Invalid Department Code");
        }
        if(success)
        {
            message.append("Correct");
        }
        return new ApiResponse(message.toString(),success);
    }
    public static UserResponseDto userToUserResponseDto(User user)
    {
        UserResponseDto userResponseDto = new UserResponseDto();
        BeanUtils.copyProperties(user,userResponseDto);
        return userResponseDto;
    }
    public static UserSingleDataResponseDto userToUserSingleDataResponseDto(User user) throws RuntimeException
    {
        UserSingleDataResponseDto userSingleDataResponseDto = new UserSingleDataResponseDto();
        BeanUtils.copyProperties(user,userSingleDataResponseDto);

        UserTypeSingleDataResponseDto userTypeSingleDataResponseDto = new UserTypeSingleDataResponseDto();
        BeanUtils.copyProperties(user.getUserType(),userTypeSingleDataResponseDto);
        userSingleDataResponseDto.setUserType(userTypeSingleDataResponseDto);

        DepartmentSingleDataResponseDto departmentSingleDataResponseDto = new DepartmentSingleDataResponseDto();
        BeanUtils.copyProperties(user.getDepartment(),departmentSingleDataResponseDto);
        userSingleDataResponseDto.setDepartment(departmentSingleDataResponseDto);

        return userSingleDataResponseDto;
    }
}
