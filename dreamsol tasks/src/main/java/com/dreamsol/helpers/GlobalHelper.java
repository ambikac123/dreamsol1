package com.dreamsol.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GlobalHelper
{
    public static final String REGEX_NAME = "[A-Za-z]{3,}(\\s[A-Za-z]+)*$";
    public static final String NAME_ERROR_MESSAGE = "name should contain [a-z, A-Z and space], It should be starts with atleast 3 letters";
   public static final String REGEX_EMAIL = "^[a-zA-Z]{3}[a-zA-Z0-9]+@[a-zA-Z]+(\\.[a-zA-Z]{2,})+$";
   public static final String EMAIL_ERROR_MESSAGE = "email can contain [a-z, A-Z, 0-9,@,.,] characters and the format will be example@xyz.abc";
    public static final String REGEX_MOBILE = "^[6-9]\\d{9}$";
    public static final String REGEX_CODE = "^[a-zA-Z]{2,7}\\d{0,3}$";
    public static final String REGEX_VALID_IMAGE = "";
    public static boolean isValidName(String name)
    {
        Pattern pattern = Pattern.compile(REGEX_NAME);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    public static boolean isValidMobile(Long mobile)
    {
        String mobileNo = Long.toString(mobile);
        Pattern pattern = Pattern.compile(REGEX_MOBILE);
        Matcher matcher = pattern.matcher(mobileNo);
        return matcher.matches();
    }

    public static boolean isValidEmail(String email)
    {
        Pattern pattern = Pattern.compile(REGEX_EMAIL);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public static boolean isValidCode(String code)
    {
        Pattern pattern = Pattern.compile(REGEX_CODE);
        Matcher matcher = pattern.matcher(code);
        return matcher.matches();
    }
}
