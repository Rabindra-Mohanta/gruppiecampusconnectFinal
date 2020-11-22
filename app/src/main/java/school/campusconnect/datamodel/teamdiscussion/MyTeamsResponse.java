package school.campusconnect.datamodel.teamdiscussion;

import java.util.List;

import school.campusconnect.datamodel.BaseResponse;

/**
 * Created by frenzin04 on 1/12/2017.
 */
public class MyTeamsResponse extends BaseResponse {

    public List<MyTeamData> data;

    public List<MyTeamData> getResults() {
        return data;
    }


}
