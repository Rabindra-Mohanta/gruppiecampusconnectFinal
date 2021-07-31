package school.campusconnect.datamodel.ebook;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;

public class AddEbookReq2 {
    public String description;
    public String fileType="pdf";
    public ArrayList<String> fileName;
    public ArrayList<String> thumbnailImage;
    public String title;
    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
