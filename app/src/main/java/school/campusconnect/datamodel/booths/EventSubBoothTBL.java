package school.campusconnect.datamodel.booths;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "EventSubBoothTBL")
public class EventSubBoothTBL extends Model {

    @Column(name = "teamId")
    public String teamId;

    @Column(name = "members")
    public String members;

    @Column(name = "lastTeamPostAt")
    public String lastTeamPostAt;

    public static List<EventSubBoothTBL> getAll()
    {
        return new Select().from(EventSubBoothTBL.class).execute();
    }
    public static List<EventSubBoothTBL> deleteAll()
    {
        return new Delete().from(EventSubBoothTBL.class).execute();
    }

}
