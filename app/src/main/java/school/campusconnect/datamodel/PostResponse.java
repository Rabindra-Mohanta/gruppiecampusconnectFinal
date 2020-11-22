package school.campusconnect.datamodel;

import java.util.List;


public class PostResponse extends BaseResponse {

    private List<PostItem> data;
    public int totalNumberOfPages;

    public List<PostItem> getResults() {
        return data;
    }

    public void setResults(List<PostItem> results) {
        this.data = results;
    }
}
