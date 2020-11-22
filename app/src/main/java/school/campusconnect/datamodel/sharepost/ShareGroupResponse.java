package school.campusconnect.datamodel.sharepost;

import java.util.List;

import school.campusconnect.datamodel.BaseResponse;

/**
 * Created by frenzin04 on 2/6/2017.
 */

public class ShareGroupResponse extends BaseResponse {

    public List<ShareGroupItem> data;

    public int totalItems;
    public int totalPages;

    public List<ShareGroupItem> getResults() {
        return data;
    }

}
