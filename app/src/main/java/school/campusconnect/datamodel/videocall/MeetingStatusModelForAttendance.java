package school.campusconnect.datamodel.videocall;

import java.util.ArrayList;
import java.util.HashMap;

public class MeetingStatusModelForAttendance {
    public String create_at;
    public String t_id;
    public String tech_id;
    public String tech_name;
    public HashMap<String,AttendanceLiveClass> attendance;
    public boolean auto_join;

    public MeetingStatusModelForAttendance() {
        attendance = new HashMap<>();
    }

    public static class AttendanceLiveClass
    {
        public String sname;
        public ArrayList<String> joinAt;

        public AttendanceLiveClass() {
            joinAt = new ArrayList<>();
            sname = new String();
        }
    }

    @Override
    public String toString()
    {
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
