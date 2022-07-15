package school.campusconnect.datamodel.videocall;

import java.util.ArrayList;

public class MeetingStatusModelApi {
    public String meetingCreatedAtTime;
    public String meetingEndedAtTime;
    public String teamId;
    public String meetingCreatedById;
    public String meetingCreatedByName;
    public String subjectName;
    public ArrayList<AttendanceLiveClass> attendance;
    public String month;

    public MeetingStatusModelApi() {
        attendance = new ArrayList<>();
    }

    public static class AttendanceLiveClass {
        public String userId;
        public String studentName;
        public ArrayList<String> meetingJoinedAtTime;

        public AttendanceLiveClass() {
            meetingJoinedAtTime = new ArrayList<>();
        }
    }

    @Override
    public String toString() {
        return "MeetingStatusModel{" +
                "meetingCreatedAtTime='" + meetingCreatedAtTime + '\'' +
                ", meetingEndedAtTime='" + meetingEndedAtTime + '\'' +
                ", teamId='" + teamId + '\'' +
                ", meetingCreatedById='" + meetingCreatedById + '\'' +
                ", meetingCreatedByName='" + meetingCreatedByName + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", attendance=" + attendance +
                ", month='" + month + '\'' +
                '}';
    }
}
