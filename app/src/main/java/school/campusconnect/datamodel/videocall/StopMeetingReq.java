package school.campusconnect.datamodel.videocall;

public class StopMeetingReq {
    public String meetingOnLiveId;
    public String subjectId;

    public StopMeetingReq(String meetingOnLiveId, String subjectId) {
        this.meetingOnLiveId = meetingOnLiveId;
        this.subjectId = subjectId;
    }

    @Override
    public String toString() {
        return "StopMeetingReq{" +
                "meetingOnLiveId='" + meetingOnLiveId + '\'' +
                ", subjectId='" + subjectId + '\'' +
                '}';
    }
}
