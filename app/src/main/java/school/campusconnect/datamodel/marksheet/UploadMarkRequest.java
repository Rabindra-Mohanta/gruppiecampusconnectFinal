package school.campusconnect.datamodel.marksheet;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class UploadMarkRequest {
    public ArrayList<Map<String,Object>> subjectMarks;

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
