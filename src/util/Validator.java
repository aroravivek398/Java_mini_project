package util;

import java.util.regex.Pattern;

public class Validator {


    private static final String EMAIL_REGEX =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";


    public static boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }


    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }


    public static boolean isValidPassword(String password) {
        return password.length() >= 5;
    }
}