package school.campusconnect.datamodel.videocall;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import school.campusconnect.LeafApplication;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.network.LeafManager;

public class VideoClassResponse extends BaseResponse {
    private ArrayList<ClassData> data;

    public ArrayList<ClassData> getData() {
        return data;
    }

    public void setData(ArrayList<ClassData> data) {
        this.data = data;
    }

    public static class ClassData {
        @SerializedName("teacherName")
        @Expose
        public String teacherName;
        @SerializedName("countryCode")
        @Expose
        public String countryCode;
        @SerializedName("phone")
        @Expose
        public String phone;
        @SerializedName(value = "className", alternate = "name")
        @Expose
        public String className;
        @SerializedName(value = "classImage", alternate = "image")
        @Expose
        public String classImage;

        @SerializedName("teamId")
        @Expose
        public String id;

        @SerializedName(value = "members", alternate = "studentCount")
        @Expose
        public String members;

        @SerializedName(value = "category")
        @Expose
        public String category;

        @SerializedName(value = "subjectId")
        @Expose
        public String subjectId;

        @SerializedName(value = "jitsiToken")
        @Expose
        public String jitsiToken;

        @SerializedName(value = "zoomPassword")
        @Expose
        public String zoomPassword;

        @SerializedName(value = "meetingIdOnLive")
        @Expose
        public String meetingIdOnLive;

        @SerializedName(value = "groupId")
        @Expose
        public String groupId;
        @SerializedName(value = "createdByName")
        @Expose
        public String createdByName;
        @SerializedName(value = "createdById")
        @Expose
        public String createdById;

        public boolean isJoining;


        public String getZoomMeetingPassword() {
            return zoomMeetingPassword;
        }

        public void setZoomMeetingPassword(String zoomMeetingPassword) {
            this.zoomMeetingPassword = zoomMeetingPassword;
        }

        @SerializedName(value = "zoomMeetingPassword")
        @Expose
        public String zoomMeetingPassword;

        @SerializedName(value = "zoomSecret")
        @Expose
        public String zoomSecret;

        public String getZoomSecret() {
            return zoomSecret;
        }

        public void setZoomSecret(String zoomSecret) {
            this.zoomSecret = zoomSecret;
        }

        public String getZoomKey() {
            return zoomKey;
        }

        public void setZoomKey(String zoomKey) {
            this.zoomKey = zoomKey;
        }

        @SerializedName(value = "zoomKey")
        @Expose
        public String zoomKey;


        public String getZoomPassword() {
            return zoomPassword;
        }

        public void setZoomPassword(String zoomPassword) {
            this.zoomPassword = zoomPassword;
        }

        public String getZoomMail() {
            return zoomMail;
        }

        public void setZoomMail(String zoomMail) {
            this.zoomMail = zoomMail;
        }

        public ArrayList<String> getZoomName() {
            return zoomName;
        }

        public void setZoomName(ArrayList<String> zoomName) {
            this.zoomName = zoomName;
        }

        @SerializedName(value = "zoomMail")
        @Expose
        public String zoomMail;

        @SerializedName(value = "zoomName")
        @Expose
        public ArrayList<String> zoomName;

        @SerializedName(value = "canPost")
        @Expose
        public boolean canPost;

        @SerializedName(value = "alreadyOnJitsiLive")
        @Expose
        public boolean alreadyOnJitsiLive;

        @SerializedName(value = "isLive")
        @Expose
        public boolean isLive;

        public MeetingStatusModel firebaseLive;


        public String getJitsiToken() {
            return jitsiToken;
        }

        public void setJitsiToken(String jitsiToken) {
            this.jitsiToken = jitsiToken;
        }

        public String getMembers() {
            return members;
        }

        public void setMembers(String members) {
            this.members = members;
        }

        public String getTeacherName() {
            return teacherName;
        }

        public void setTeacherName(String teacherName) {
            this.teacherName = teacherName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getName() {
            return className;
        }

        public void setName(String name) {
            this.classImage = name;
        }

        public String getImage() {
            return classImage;
        }

        public void setImage(String image) {
            this.classImage = image;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @NonNull
        @Override
        public String toString() {
            return new Gson().toJson(this);
        }

        public boolean isCreatedByMe() {
            if ((createdById + "").equalsIgnoreCase(LeafPreference.getInstance(LeafApplication.getInstance()).getUserId())) {
                return true;
            }
            return false;
        }
    }
}
