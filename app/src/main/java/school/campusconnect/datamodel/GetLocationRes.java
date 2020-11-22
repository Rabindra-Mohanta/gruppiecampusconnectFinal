package school.campusconnect.datamodel;

public class GetLocationRes extends BaseResponse {
    public GetLocationData data;

    public class GetLocationData {
        public String tripStartedAt;
        public String latitude;
        public String longitude;

        public double getLatitude() {
            try {
                return Double.parseDouble(latitude);
            }catch (Exception e)
            {
                return 0;
            }

        }
        public double getLongitude() {
            try {
                return Double.parseDouble(longitude);
            }catch (Exception e)
            {
                return 0;
            }
        }
    }
}
