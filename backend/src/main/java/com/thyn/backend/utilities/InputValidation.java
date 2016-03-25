package com.thyn.backend.utilities;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains input validation helper methods.
 *
 * @author Angelo Agatino Nicolosi
 */
public class InputValidation {

    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    private static final String DATE_PATTERN = "\\d{2}/\\d{2}/\\d{4}";


    private static Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
    private static Pattern datePattern = Pattern.compile(DATE_PATTERN);
    private static Matcher matcher;

    public static boolean validateDateInput(String input)
    {
        return validateTextBoxInput(input) && isValidDate(input);
    }

    private static boolean isValidDate(String date) {
        matcher = datePattern.matcher(date);
        return matcher.matches();
    }

    public static boolean validateTextBoxInput(String input)
    {
        return input != null && input.trim().length() > 0;
    }

    public static boolean validateEmailAddressInput(String input)
    {
        return validateTextBoxInput(input) && isValidEmailAddress(input);
    }

    public static boolean validatePassword(String input)
    {
        return validateTextBoxInput(input) && input.length() >= 8;
    }

    private static boolean isValidEmailAddress(String email) {
        matcher = emailPattern.matcher(email);
        return matcher.matches();
    }
}
