package school.campusconnect.datamodel.ticket;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class TicketEventUpdateResponse extends BaseResponse implements Serializable {

    public ArrayList<eventData> data;

    public static class eventData implements Serializable
    {
        public int eventType;
        public String eventName;
        public String eventAt;
    }
}
