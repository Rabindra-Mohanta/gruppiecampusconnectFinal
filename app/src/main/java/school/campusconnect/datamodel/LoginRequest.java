package school.campusconnect.datamodel;


public class LoginRequest {

    public UserName userName;
    public String password;
    public String deviceToken="";
    public String deviceType;
    public String appVersion;
    public String osVersion;
    public String deviceModel;

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
                ", deviceToken='" + deviceToken + '\'' +
                ", deviceType='" + deviceType + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                '}';
    }
}
