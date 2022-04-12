package school.campusconnect.datamodel.booths;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "MyBoothEventTBL")
public class MyBoothEventTBL extends Model {

    @Column(name = "teamId")
    public String teamId;

    @Column(name = "members")
    public int members;

    @Column(name = "lastTeamPostAt")
    public String lastTeamPostAt;

    public static List<MyBoothEventTBL> getAll()
    {
        return new Select().from(MyBoothEventTBL.class).execute();
    }
    public static List<MyBoothEventTBL> deleteAll()
    {
        return new Delete().from(MyBoothEventTBL.class).execute();
    }

}
