package school.campusconnect.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Map;

public class MarkSheetListResponse extends BaseResponse {
    public int totalNumberOfPages;

    public ArrayList<MarkSheetData> data;

    public class MarkSheetData {
        @SerializedName("title")
        @Expose
        public String title;

        @SerializedName("markscardId")
        @Expose
        public String marksCardId;

        @SerializedName("insertedAt")
        @Expose
        public String insertedAt;

        public ArrayList<Map<String,String>> subjectMarks;

        public ArrayList<Map<String,String>>  maxMarks;
        public ArrayList<Map<String,String>>  minMarks;

    }
}
