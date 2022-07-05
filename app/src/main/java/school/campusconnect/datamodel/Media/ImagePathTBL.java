package school.campusconnect.datamodel.Media;

import android.net.Uri;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

public class ImagePathTBL extends Model {

    @Column(name  = "fileName")
    public String fileName;

    @Column(name  = "url")
    public String url;


    public static List<ImagePathTBL> getLastInserted(String fileName) {
        return new Select().from(ImagePathTBL.class).where("fileName = ?", fileName).execute();
    }


    public static void deleteAll() {
        new Delete().from(ImagePathTBL.class).execute();
    }

}
