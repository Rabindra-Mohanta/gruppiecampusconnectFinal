package school.campusconnect.datamodel;

public class PersonalSettingRes extends BaseResponse{
    public PersonalSettingData data;

    public class PersonalSettingData {
        public boolean allowReplyPost;
        public boolean allowComment;
    }
}
