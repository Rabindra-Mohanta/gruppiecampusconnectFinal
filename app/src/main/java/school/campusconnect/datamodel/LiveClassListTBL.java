package school.campusconnect.datamodel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import school.campusconnect.activities.GroupDashboardActivityNew;

@Table(name = "LiveClassListTBL")
public class LiveClassListTBL extends Model {
    @Column(name = "zoomPassword")
    public String zoomPassword;

    @Column(name = "zoomName")
    public String zoomName;

    @Column(name = "zoomMail")
    public String zoomMail;

    @Column(name = "zoomSecret")
    public String zoomSecret;

    @Column(name = "zoomMeetingPassword")
    public String zoomMeetingPassword;

    @Column(name = "zoomKey")
    public String zoomKey;

    @Column(name = "teamId")
    public String teamId;

    @Column(name = "name")
    public String name;

    @Column(name = "jitsiToken")
    public String jitsiToken;

    @Column(name = "groupId")
    public String groupId;

    @Column(name = "canPost")
    public boolean canPost;

    public LiveClassListTBL() {
        super();
    }

    public static List<LiveClassListTBL> getAll(String groupId) {
        return new Select().from(LiveClassListTBL.class).where("groupId = ?", groupId).execute();
    }
    public static void deleteAll(String groupId) {
        new Delete().from(LiveClassListTBL.class).where("groupId = ?", groupId).execute();
    }
    public static void deleteAll() {
        new Delete().from(LiveClassListTBL.class).execute();
    }
}
