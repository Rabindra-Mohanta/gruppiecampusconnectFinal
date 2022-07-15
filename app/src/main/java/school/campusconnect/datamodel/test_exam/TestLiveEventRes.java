package school.campusconnect.datamodel.test_exam;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class TestLiveEventRes extends BaseResponse {
    private ArrayList<TestLiveEvent> data;

    public ArrayList<TestLiveEvent> getData() {
        return data;
    }

    public void setData(ArrayList<TestLiveEvent> data) {
        this.data = data;
    }

    public static class TestLiveEvent implements Serializable {
        @SerializedName("testExamId")
        @Expose
        public String testExamId;

        @SerializedName("teamId")
        @Expose
        public String teamId;
        @SerializedName("subjectId")
        @Expose
        public String subjectId;
        @SerializedName("groupId")
        @Expose
        public String groupId;

        @SerializedName("eventName")
        @Expose
        public String eventName;

        @SerializedName("createdByName")
        @Expose
        public String createdByName;
        @SerializedName("createdById")
        @Expose
        public String createdById;

        @SerializedName("eventType")
        @Expose
        public int eventType;


    }
}

