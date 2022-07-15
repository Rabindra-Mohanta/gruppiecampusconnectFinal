package school.campusconnect.datamodel.syllabus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChangeStatusPlanModel {

    public static class ChangeStatusModelReq implements Serializable
    {
        @SerializedName("toDate")
        @Expose
        private String toDate;

        @SerializedName("fromDate")
        @Expose
        private String fromDate;

        @SerializedName("actualStartDate")
        @Expose
        private String actualStartDate;

        @SerializedName("actualEndDate")
        @Expose
        private String actualEndDate;

        public String getToDate() {
            return toDate;
        }

        public void setToDate(String toDate) {
            this.toDate = toDate;
        }

        public String getFromDate() {
            return fromDate;
        }

        public void setFromDate(String fromDate) {
            this.fromDate = fromDate;
        }

        public String getActualStartDate() {
            return actualStartDate;
        }

        public void setActualStartDate(String actualStartDate) {
            this.actualStartDate = actualStartDate;
        }

        public String getActualEndDate() {
            return actualEndDate;
        }

        public void setActualEndDate(String actualEndDate) {
            this.actualEndDate = actualEndDate;
        }
    }
}
