package school.campusconnect.datamodel.authorizeduser;

import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamAddress;

/**
 * Created by frenzin04 on 1/12/2017.
 */
public class AuthorizedUserData extends BaseResponse {

    public String id;
    public String name;
    public String phone;
    public String image;
    public String profileCompletion;
    public String email;
    public String gender;
    public String dob;
    public String qualification;
    public String occupation;
    public MyTeamAddress address;
    public String[] groups;

}
