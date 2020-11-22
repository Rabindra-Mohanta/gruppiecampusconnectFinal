package school.campusconnect.datamodel.ebook;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;

public class AddEbookReq {
    public String className;
    public ArrayList<EBooksResponse.SubjectBook> subjectBooks;

    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
