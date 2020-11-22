package school.campusconnect.datamodel.authorizeduser;

import java.util.List;

import school.campusconnect.datamodel.BaseResponse;

/**
 * Created by frenzin04 on 1/12/2017.
 */
public class AuthorizedUserResponse extends BaseResponse {

    public List<AuthorizedUserData> data;
    public int totalNumberOfPages;


    public List<AuthorizedUserData> getResults() {
        return data;
    }


}
