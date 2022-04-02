package school.campusconnect.datamodel.banner;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import school.campusconnect.datamodel.booths.BoothPresidentTBL;

@Table(name = "BannerTBL")
public class BannerTBL extends Model {

    @Column(name = "updatedAt")
    public String updatedAt;

    @Column(name = "fileType")
    public String fileType;

    @Column(name = "fileName")
    public String fileName;

    @Column(name = "_now")
    public long _now;

    @Column(name = "groupId")
    public String groupId;

    public static List<BannerTBL> getAll() {
        return new Select().from(BannerTBL.class).execute();
    }

    public static List<BannerTBL> getBanner(String group_id) {
        return new Select().from(BannerTBL.class).where("groupId = ?", group_id).execute();
    }

    public static void deleteBanner(String group_id) {
        new Delete().from(BannerTBL.class).where("groupId = ?", group_id).execute();
    }

    public static void deleteAll() {
        new Delete().from(BannerTBL.class).execute();
    }
}
