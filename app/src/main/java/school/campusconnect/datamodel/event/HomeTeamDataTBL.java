package school.campusconnect.datamodel.event;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import school.campusconnect.datamodel.feed.AdminFeedTable;

@Table(name = "HomeTeamDataTBL")
public class HomeTeamDataTBL extends Model {

    @Column(name = "teamId")
    public String teamId;

    @Column(name = "members")
    public int members;

    @Column(name = "lastTeamPostAt")
    public String lastTeamPostAt;
/*
    @Column(name = "lastCommitteeForBoothUpdatedEventAt")
    public String lastCommitteeForBoothUpdatedEventAt;*/

    @Column(name = "canPost")
    public boolean canPost;

    @Column(name = "canComment")
    public boolean canComment;

    public static List<HomeTeamDataTBL> getAll() {
        return new Select().from(HomeTeamDataTBL.class).execute();
    }

    public static List<HomeTeamDataTBL> getTeamPost(String teamId) {
        return new Select().from(HomeTeamDataTBL.class).where("teamId = ?",teamId).execute();
    }

    public static void deleteTeamPost(String teamId) {
        new Delete().from(HomeTeamDataTBL.class).where("teamId = ?",teamId).execute();
    }
    public static void deleteAll() {
        new Delete().from(HomeTeamDataTBL.class).execute();
    }

}

