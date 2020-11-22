package school.campusconnect.datamodel.comments;

import java.util.List;

import school.campusconnect.datamodel.BaseResponse;

/**
 * Created by frenzin04 on 1/11/2017.
 */
public class GroupCommentResponse extends BaseResponse {

    public List<GroupCommentData> data;
    public int totalNumberOfPages;

    public List<GroupCommentData> getResults() {
        return data;
    }

    public void setResults(List<GroupCommentData> results) {
        this.data = results;
    }

}
