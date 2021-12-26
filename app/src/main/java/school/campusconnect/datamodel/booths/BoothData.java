package school.campusconnect.datamodel.booths;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BoothData {
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName("boothPresidentName")
        @Expose
        public String boothPresidentName;
        @SerializedName("boothName")
        @Expose
        public String boothName;
        @SerializedName("boothImage")
        @Expose
        public String boothImage;
        @SerializedName("boothId")
        @Expose
        public String boothId;
        @SerializedName("countryCode")
        @Expose
        public String countryCode;
        @SerializedName("totalBoothMembersCount")
        @Expose
        public String totalBoothMembersCount;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }