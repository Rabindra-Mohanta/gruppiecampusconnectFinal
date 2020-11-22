package school.campusconnect.datamodel.marksheet;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class MarkCardListResponse extends BaseResponse {
    private ArrayList<MarkCardData> data;

    public ArrayList<MarkCardData> getData() {
        return data;
    }

    public void setData(ArrayList<MarkCardData> data) {
        this.data = data;
    }

    public static class MarkCardData {
        @SerializedName("title")
        @Expose
        public String title;
        @SerializedName("marksCardId")
        @Expose
        public String marksCardId;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMarksCardId() {
            return marksCardId;
        }

        public void setMarksCardId(String marksCardId) {
            this.marksCardId = marksCardId;
        }
    }
}
