package school.campusconnect.datamodel;

import java.util.List;


public class ContactResponse extends BaseResponse{


    private List<ContactsItem> data;


    public List<ContactsItem> getResults() {
        return data;
    }

    public void setResults(List<ContactsItem> results) {
        this.data = results;
    }

}
