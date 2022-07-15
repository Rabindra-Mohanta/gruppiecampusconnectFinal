package school.campusconnect.datamodel.booths;

import java.io.Serializable;
import java.util.ArrayList;

import school.campusconnect.datamodel.BaseResponse;

public class SubBoothWorkerEventRes extends BaseResponse {

    public ArrayList<EventData> data;

    public static class EventData implements Serializable
    {
        public String lastUpdatedMySubBoothTeamTime;
        public ArrayList<mySubBoothTeamsLastPostEventData> mySubBoothTeamsLastPostEventAt;
    }
    public static class mySubBoothTeamsLastPostEventData implements Serializable
    {

        public String teamId;
        public int members;
        public String lastTeamPostAt;

    }
}
