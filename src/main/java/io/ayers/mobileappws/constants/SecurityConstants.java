package io.ayers.mobileappws.constants;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 864000000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";
    public static final String LOG_IN_URL = "/users/login";
    public static final String TOKEN_SECRET = "abcd1234";
    public static final String VERIFICATION_EMAIL_URL = "/users/email-verification";
    public static final String PASSWORD_RESET_URL = "/users/password-reset";
    public static final String DO_PASSWORD_RESET_URL = "/users/do-password-reset";
}
