package school.campusconnect.datamodel.booths;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class MyBoothEventRes extends BaseResponse {

    public ArrayList<EventData> data;

    public static class EventData implements Serializable
    {
        public String lastUpdatedMyBoothTeamTime;
        public ArrayList<myBoothTeamsLastPostEventData> myBoothTeamsLastPostEventAt;
    }
    public static class myBoothTeamsLastPostEventData implements Serializable
    {

        public String teamId;
        public int members;
        public String lastTeamPostAt;

    }

}
