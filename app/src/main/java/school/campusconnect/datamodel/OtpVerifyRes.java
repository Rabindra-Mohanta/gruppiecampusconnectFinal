package school.campusconnect.datamodel;

public class OtpVerifyRes extends BaseResponse {
    public OtpData data;

    public class OtpData {
      public boolean otpVerified;
    }
}
