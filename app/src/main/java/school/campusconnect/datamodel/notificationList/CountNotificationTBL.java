package school.campusconnect.datamodel.notificationList;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import school.campusconnect.datamodel.baseTeam.BaseTeamTableV2;

@Table(name = "CountNotificationTBL")
public class CountNotificationTBL extends Model {

    @Column(name = "team_id")
    public String teamID;

    @Column(name = "count")
    public int count;

    public static List<CountNotificationTBL> getCountNotification(String teamID) {
        return new Select().from(CountNotificationTBL.class).where("team_id = ?", teamID).execute();
    }

    public static void deleteCountNotification(String teamID) {
        new Delete().from(CountNotificationTBL.class).where("team_id = ?", teamID).execute();
    }

    public static void updateCountNotification(String teamID,int count) {
        new Update(CountNotificationTBL.class).set("count = ?", count).where("team_id = ?", teamID).execute();
    }

    public static void deleteAll() {
        new Delete().from(CountNotificationTBL.class).execute();
    }
}
