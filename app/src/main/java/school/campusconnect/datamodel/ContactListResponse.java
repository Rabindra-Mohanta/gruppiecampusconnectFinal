package school.campusconnect.datamodel;

import java.util.List;


public class ContactListResponse extends BaseResponse {


    public List<ContactListItem> data;

    public List<ContactListItem> getResults() {

        return data;
    }

    public void setResults(List<ContactListItem> results) {
        this.data = results;
    }

}
