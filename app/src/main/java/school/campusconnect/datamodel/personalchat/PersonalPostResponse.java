package school.campusconnect.datamodel.personalchat;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

/**
 * Created by frenzin04 on 2/6/2017.
 */

public class PersonalPostResponse extends BaseResponse {

    public ArrayList<PersonalPostItem> data;

    public ArrayList<PersonalPostItem> getResults() {
        return data;
    }

}
