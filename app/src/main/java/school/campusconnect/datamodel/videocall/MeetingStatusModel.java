package school.campusconnect.datamodel.videocall;

import java.util.ArrayList;

public class MeetingStatusModel {
    public String create_at;
    public String t_id;
    public String tech_id;
    public String tech_name;
    public ArrayList<AttendanceLiveClass> attendance;
    public boolean auto_join;

    public MeetingStatusModel() {
        attendance = new ArrayList<>();
    }

    public static class AttendanceLiveClass {
        public String uid;
        public String sname;
        public ArrayList<String> joinAt;

        public AttendanceLiveClass() {
            joinAt = new ArrayList<>();
        }
    }

    @Override
    public String toString() {
        return "MeetingStatusModel{" +
                "meetingCreatedAtTime='" + create_at + '\'' +
                ", teamId='" + t_id + '\'' +
                ", meetingCreatedById='" + tech_id + '\'' +
                ", meetingCreatedByName='" + tech_name + '\'' +
                ", attendance=" + attendance +
                ", autoJoinForStudent=" + auto_join +
                '}';
    }
}
