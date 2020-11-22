package school.campusconnect.datamodel;

public class SettingRes extends BaseResponse{
    public SettingData data;

    public class SettingData {
        public boolean allowPostShare;
        public boolean allowPostAll;
        public boolean allowAdminChange;
    }
}
