package school.campusconnect.datamodel.question;

import java.util.List;

import school.campusconnect.datamodel.BaseResponse;

/**
 * Created by frenzin04 on 3/9/2017.
 */

public class QuestionResponse extends BaseResponse {


    private int id;
    private List<QuestionData> data;
    public int totalItems;
    public int totalPages;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<QuestionData> getResults() {
        return data;
    }

    public void setResults(List<QuestionData> results) {
        this.data = results;
    }

}
