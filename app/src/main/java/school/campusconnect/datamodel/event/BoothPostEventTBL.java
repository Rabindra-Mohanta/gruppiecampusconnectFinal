package school.campusconnect.datamodel.event;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "BoothPostEventTBL")
public class BoothPostEventTBL extends Model {
    @Column(name = "boothId")
    public String boothId;

    @Column(name = "members")
    public int members;

    @Column(name = "lastBoothPostAt")
    public String lastBoothPostAt;


    public String getLastCommitteeForBoothUpdatedEventAt() {
        return lastCommitteeForBoothUpdatedEventAt;
    }

    public void setLastCommitteeForBoothUpdatedEventAt(String lastCommitteeForBoothUpdatedEventAt) {
        this.lastCommitteeForBoothUpdatedEventAt = lastCommitteeForBoothUpdatedEventAt;
    }

    @Column(name = "lastCommitteeForBoothUpdatedEventAt")
    public String lastCommitteeForBoothUpdatedEventAt;

    public static List<BoothPostEventTBL> getAll() {
        return new Select().from(BoothPostEventTBL.class).execute();
    }

    public static void deleteAll() {
        new Delete().from(BoothPostEventTBL.class).execute();
    }
}
