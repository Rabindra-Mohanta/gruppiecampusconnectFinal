package school.campusconnect.datamodel;

import java.util.List;

public class LeadResponse extends BaseResponse {


    private List<LeadItem> data;
    public int totalNumberOfPages;


    public List<LeadItem> getResults() {
       /* Collections.sort(data, new Comparator<LeadItem>() {
            public int compare(LeadItem v1, LeadItem v2) {
                return v1.getName().compareTo(v2.getName());
            }
        });*/
        return data;
    }

    public void setResults(List<LeadItem> results) {
        this.data = results;
    }

}
