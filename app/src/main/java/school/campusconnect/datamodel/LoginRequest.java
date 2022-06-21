package school.campusconnect.datamodel;


import java.io.Serializable;

public class LoginRequest implements Serializable {

    public UserName userName;
    public String password;
    public String deviceToken="";
    public String deviceType;
    public String appVersion;
    public String osVersion;
    public String deviceModel;
    public String udid;

    public static class UserName{
        public String countryCode;
        public String phone;
    }

    public LoginRequest()
    {
        userName = new UserName();
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "userName=" + userName +
                ", password='" + password + '\'' +
                ", udid='" + udid + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                '}';
    }
}
