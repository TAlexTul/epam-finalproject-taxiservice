package com.epam.alextuleninov.taxiservice.validation;

import com.epam.alextuleninov.taxiservice.service.message.PageMessageBuilder;
import jakarta.servlet.http.HttpServletRequest;

import java.util.regex.Pattern;

import static com.epam.alextuleninov.taxiservice.Constants.*;

/**
 * The DataValidator class contains the methods for
 * validating an input data (data from forms).
 */
public final class DataValidator {

    private final static String REGEX_CHECK_FOR_NAME = "^[a-zA-Zа-яА-Я\\s]{2,20}$";
    private final static String REGEX_EMAIL = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
    private final static String REGEX_PASSWORD = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,20}$";
    private final static String REGEX_NUMBER = "[0-9]+";
    private final static String REGEX_LOCAL_DATE_TIME = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}";

    /**
     * Check registration data.
     *
     * @param req request from HttpServletRequest
     * @return true if validation is success
     */
    public static boolean initRegisterValidation(HttpServletRequest req) {
        String locale = (String) req.getSession().getAttribute(SCOPE_LOCALE);

        if (!validateName(req.getParameter(SCOPE_FIRST_NAME))) {
            changeLocale(locale, req, SCOPE_FIRST_NAME, FIRST_NAME_NOT_VALID_UK, FIRST_NAME_NOT_VALID);
            return false;
        }
        if (!validateName(req.getParameter(SCOPE_LAST_NAME))) {
            changeLocale(locale, req, SCOPE_LAST_NAME, LAST_NAME_NOT_VALID_UK, LAST_NAME_NOT_VALID);
            return false;
        }
        if (!validateLogin(req.getParameter(SCOPE_LOGIN))) {
            changeLocale(locale, req, SCOPE_LOGIN_VALIDATE, LOGIN_NOT_VALID_UK, LOGIN_NOT_VALID);
            return false;
        }
        if (!validatePassword(req.getParameter(SCOPE_PASSWORD))) {
            changeLocale(locale, req, SCOPE_PASSWORD_VALIDATE, PASSWORD_NOT_VALID_UK, PASSWORD_NOT_VALID);
            return false;
        }
        return true;
    }

    /**
     * Check login data.
     *
     * @param req request from HttpServletRequest
     * @return true if validation is success
     */
    public static boolean initLoginValidation(HttpServletRequest req) {
        String locale = (String) req.getSession().getAttribute(SCOPE_LOCALE);

        if (!validateLogin(req.getParameter(SCOPE_LOGIN))) {
            changeLocale(locale, req, SCOPE_LOGIN_VALIDATE, LOGIN_NOT_VALID_UK, LOGIN_NOT_VALID);
            return false;
        }
        if (!validatePassword(req.getParameter(SCOPE_PASSWORD))) {
            changeLocale(locale, req, SCOPE_PASSWORD_VALIDATE, PASSWORD_NOT_VALID_UK, PASSWORD_NOT_VALID);
            return false;
        }
        return true;
    }

    /**
     * Check order data.
     *
     * @param req request from HttpServletRequest
     * @return true if validation is success
     */
    public static boolean initOrderValidation(HttpServletRequest req) {
        return validateNumber(req.getParameter("numberOfPassengers"))
                && validateLocalDateTime(req.getParameter("dateOfTravel"));
    }

    /**
     * Validate name.
     *
     * @param name name of user
     * @return true if validation is success
     */
    private static boolean validateName(String name) {
        return Pattern.matches(REGEX_CHECK_FOR_NAME, name);
    }

    /**
     * Validate login.
     *
     * @param login login of user
     * @return true if validation is success
     */
    private static boolean validateLogin(String login) {
        return Pattern.matches(REGEX_EMAIL, login);
    }

    /**
     * Validate password.
     *
     * @param password password of user
     * @return true if validation is success
     */
    private static boolean validatePassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }

    /**
     * Validate number of passengers in order.
     *
     * @param number number passengers in order
     * @return true if validation is success
     */
    private static boolean validateNumber(String number) {
        return !number.equals("0")
                && Pattern.matches(REGEX_NUMBER, number);
    }

    /**
     * Validate date and time in order.
     *
     * @param localDateTime number passengers in order
     * @return true if validation is success
     */
    private static boolean validateLocalDateTime(String localDateTime) {
        return Pattern.matches(REGEX_LOCAL_DATE_TIME, localDateTime);
    }

    /**
     * Change locale from user request.
     *
     * @param locale     current locale
     * @param req        request from user
     * @param scope      scope for jsp page
     * @param notValidUk message on Ukrainian language
     * @param notValid   message on English language
     */
    private static void changeLocale(String locale, HttpServletRequest req, String scope,
                                     String notValidUk, String notValid) {
        if (locale.equals("uk_UA")) {
            req.setAttribute(scope, notValidUk);
        } else {
            req.setAttribute(scope, notValid);
        }
    }
}
