package school.campusconnect.datamodel.teamdiscussion;

import java.util.List;

import school.campusconnect.datamodel.BaseResponse;


public class TeamPostGetResponse extends BaseResponse {

    List<TeamPostGetData> data;
    public int totalItems;
    public int totalNumberOfPages;


    public List<TeamPostGetData> getResults() {
        return data;
    }

}
