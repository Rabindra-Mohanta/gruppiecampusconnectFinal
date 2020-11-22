package school.campusconnect.datamodel;


import java.util.ArrayList;

public class ErrorResponseModel<T> extends BaseErrorResponse {
    //public T errors;
    public ArrayList<T> errors;
}
