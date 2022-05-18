package school.campusconnect.firebase;

import com.google.gson.Gson;

public class SendNotificationModel {
    public String to; // token or Topic
    public SendNotiData data;

    public SendNotificationModel() {
        data = new SendNotiData();
    }

    public static class SendNotiData {
        public String title; // title of notification
        public String body; // message of notification
        public String Notification_type;
        public boolean iSNotificationSilent;

        public String groupId;
        public String createdById;
        public String postId; // if related to post
        public String postType; // if related to post
        public String teamId; // if related to team/class
        public String createdByName; // if related to team/class

        /*video call paramater*/
        public boolean isVideoCall;
        public String meetingID;
        public String zoomName;
        public String className;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
