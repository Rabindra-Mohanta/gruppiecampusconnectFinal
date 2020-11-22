package school.campusconnect.datamodel.notifications;

import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

/**
 * Created by frenzin04 on 2/27/2017.
 */

public class NotificationResponse extends BaseResponse {

    public ArrayList<NotificationData> data;

    public int totalItems;
    public int totalPages;

}