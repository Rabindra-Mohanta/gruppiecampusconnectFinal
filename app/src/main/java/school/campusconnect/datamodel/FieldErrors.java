package school.campusconnect.datamodel;

import com.google.gson.annotations.SerializedName;


public class FieldErrors<T> {
    @SerializedName("field_errors")
    public T fieldErrors;

}
