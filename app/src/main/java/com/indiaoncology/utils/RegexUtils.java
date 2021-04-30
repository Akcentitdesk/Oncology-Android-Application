package com.indiaoncology.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {
    private static String EMAIL_PATTERN = "[a-zA-Z0-9-]+@[a-z]{2,10}+\\.+[a-z]{2,3}+";
    private static String EMAIL_PATTERN_With_LIMIT = "[a-zA-Z0-9-]+@[a-z]{2,10}+\\.+[a-z]{2,3}+\\.+[a-z]{2,3}+";

    public static boolean validName(String name) {
        boolean isValid = false;
        if (!TextUtils.isEmpty(name) && name.length() >= 2) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isValidEmail(CharSequence target) {
        return target != null && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidPassword(String password) {
//        String PASSWORD_PATTERN ="((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20})";
//        pattern = Pattern.compile(PASSWORD_PATTERN);
//        matcher = pattern.matcher(pass);
//        return matcher.matches();

        boolean isValid = false;
        if (!TextUtils.isEmpty(password) && password.length() >= 8 && password.length() <= 16) {
            isValid = true;
        }
        return isValid;
    }


    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "[a-zA-Z0-9-]+@[a-z]{2,10}+\\.+[a-z]{2,3}+";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    // validating password with confirm password
    public static boolean isPasswordMatch(String pass, String confirmPass) {
        if (pass.equals(confirmPass)) {
            return true;
        }
        return false;
    }

    public static boolean isValidMobileNumber(String number) {
        return number.matches("^[6-9][0-9]{9}$");
    }

    public static boolean isValidPhoneNumber(String password) {
//        String PASSWORD_PATTERN ="((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{8,20})";
//        pattern = Pattern.compile(PASSWORD_PATTERN);
//        matcher = pattern.matcher(pass);
//        return matcher.matches();

        boolean isValid = false;
        if (!TextUtils.isEmpty(password) && password.length() > 9) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean validEmailAddress(final String email) {
        return email.matches(EMAIL_PATTERN) || email.matches(EMAIL_PATTERN_With_LIMIT) ? true : false;
    }

    public static boolean isValidPincode(final String pincode) {
        boolean isValid = false;
        // Regex to check valid pin code of India.
        String regex = "^[1-9]{1}[0-9]{2}\\s{0,1}[0-9]{3}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(pincode);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

}
