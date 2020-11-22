package school.campusconnect.datamodel;


public class LoginRequest {

    public UserName userName;
    public String password;
    public String deviceToken="";
    public String deviceType;

    public static class UserName{
        public String countryCode;
        public String phone;
    }

    public LoginRequest()
    {
        userName = new UserName();
    }

}
