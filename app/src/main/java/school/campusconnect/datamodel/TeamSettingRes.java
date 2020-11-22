package school.campusconnect.datamodel;

public class TeamSettingRes extends BaseResponse{
    public TeamSettingData data;

    public class TeamSettingData {
        public boolean allowTeamPostCommentAll;
        public boolean allowTeamPostAll;
        public boolean allowTeamUsersToAddMembersToGroup;
        public boolean enableGps;
        public boolean enableAttendance;
    }
}
