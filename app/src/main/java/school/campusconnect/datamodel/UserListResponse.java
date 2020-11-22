package school.campusconnect.datamodel;

import java.util.List;


public class UserListResponse extends BaseResponse {


    public List<UserListItem> data;
    public int totalNumberOfPages;

    public List<UserListItem> getResults() {
      /*  Collections.sort(data, new Comparator<UserListItem>() {
            public int compare(UserListItem v1, UserListItem v2) {
                return v1.getName().compareTo(v2.getName());
            }
        });*/
        return data;
    }

    public void setResults(List<UserListItem> results) {
        this.data = results;
    }
}
