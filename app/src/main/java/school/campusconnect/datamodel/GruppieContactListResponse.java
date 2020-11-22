package school.campusconnect.datamodel;

import java.util.List;


public class GruppieContactListResponse extends BaseResponse {


    public List<GruppieContactListItem> items;
    public int pages;

    public List<GruppieContactListItem> getResults() {
      /*  Collections.sort(data, new Comparator<UserListItem>() {
            public int compare(UserListItem v1, UserListItem v2) {
                return v1.getName().compareTo(v2.getName());
            }
        });*/
        return items;
    }

    public void setResults(List<GruppieContactListItem> results) {
        this.items = results;
    }
}
