package com.saralvigyan.saralvigyan.app;

/**
 * Created by Mangesh kokare on 13-11-2017.
 */

public class AppConfig {

    // server URL configuration
    //public static final String URL_REQUEST_SMS = "http://192.168.0.101/android_sms/msg91/request_sms.php";
    public static final String URL_VERIFY_OTP = "http://192.168.0.5/SaralVigyan/verify_otp.php";

    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use  MSGIND, TESTER and ALERTS as sender ID
    // If you want custom sender Id, approve MSG91 to get one
    public static final String SMS_ORIGIN = "ANHIVE";

    // special character to prefix the otp. Make sure this character appears only once in the sms
    public static final String OTP_DELIMITER = ":";
    // Server user login url
    public static String URL_LOGIN = "http://192.168.0.5/SaralVigyan/login.php";

    // Server user register url
    public static String URL_REGISTER = "http://192.168.0.5/SaralVigyan/register.php";

    //Change password url
    public static String URL_UPDATE_PASSWORD = "http://192.168.0.5/SaralVigyan/change_password.php";
}
