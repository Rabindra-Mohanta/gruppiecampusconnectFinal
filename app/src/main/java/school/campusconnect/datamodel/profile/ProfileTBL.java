package school.campusconnect.datamodel.profile;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import school.campusconnect.datamodel.AddressItem;
import school.campusconnect.datamodel.banner.BannerTBL;

@Table(name = "ProfileTBL")
public class ProfileTBL extends Model {

    @Column(name = "profileData")
    public String profileData;

    @Column(name = "_now")
    public String _now;

    public static List<ProfileTBL> getProfile() {
        return new Select().from(ProfileTBL.class).execute();
    }

    public static void deleteAll() {
        new Delete().from(ProfileTBL.class).execute();
    }

}
