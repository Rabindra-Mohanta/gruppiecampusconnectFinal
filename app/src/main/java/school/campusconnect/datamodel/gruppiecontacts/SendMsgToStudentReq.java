package school.campusconnect.datamodel.gruppiecontacts;

public class SendMsgToStudentReq {
    private String title;

    public SendMsgToStudentReq() {
    }

    public SendMsgToStudentReq(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
