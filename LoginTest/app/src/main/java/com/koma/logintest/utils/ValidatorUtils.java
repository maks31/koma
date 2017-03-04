package com.koma.logintest.utils;

        import java.util.regex.Matcher;
        import java.util.regex.Pattern;

public class ValidatorUtils {

    private static Pattern pattern;
    private static Matcher matcher;

    private static final String USERNAME_PATTERN = "^[A-Za-z0-9_ ]{2,15}$";
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    //	private static final String EMAIL_PATTERN = "^[a-zA-Z0-9]+[a-zA-Z0-9._-]+[a-zA-Z0-9]+@+[a-zA-Z0-9]+\\.+[a-z]+$";
    private static final String PHONE_NUMBER_PATTERN = "\\d{10}";
    private static final String PWD_PATTERN = "\\A[a-zA-Z0-9_)($#%/*!@]{6,20}\\z";

    public ValidatorUtils() {

    }

    public static boolean userNameValidate(String username) {
        pattern = Pattern.compile(USERNAME_PATTERN);
        matcher = pattern.matcher(username);

        return matcher.matches();
    }

    public static boolean emailValidate(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean phoneValidate(String phoneNumber) {
        pattern = Pattern.compile(PHONE_NUMBER_PATTERN);
        matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public static boolean passwordValidate(String pwd) {
        pattern = Pattern.compile(PWD_PATTERN);
        matcher = pattern.matcher(pwd);
        return matcher.matches();
    }

    public static String youtubeIdValidate(String youtubeUrl) 	{
        if (youtubeUrl != null && (!youtubeUrl.equalsIgnoreCase(""))) {
            String video_id = null;
            if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http")) {
                String expression = "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*";
                CharSequence input = youtubeUrl;
                Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(input);
                if (matcher.matches()) {
                    String groupIndex1 = matcher.group(7);
                    if (groupIndex1 != null && groupIndex1.length() == 11)
                        video_id = groupIndex1;
                }
            }
            return video_id;
        } else {
            return null;
        }
    }
}

