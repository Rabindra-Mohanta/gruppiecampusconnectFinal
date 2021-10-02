package school.campusconnect.datamodel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "EBookItem")
public class EBookItem extends Model {
    @Column(name = "ebookId")
    public String ebookId;

    @Column(name = "subjectName")
    public String subjectName;

    @Column(name = "description")
    public String description;

    @Column(name = "fileName")
    public String fileName;

    @Column(name = "thumbnailImage")
    public String thumbnailImage;

    @Column(name = "teamId")
    public String teamId;

    @Column(name = "groupId")
    public String groupId;

    public EBookItem(){
        super();
    }

    public static List<EBookItem> getAll(String teamId,String groupId) {
        return new Select().from(EBookItem.class).where("teamId = ?", teamId).where("groupId = ?", groupId).execute();
    }
    public static void deleteAll(String teamId,String groupId) {
        new Delete().from(EBookItem.class).where("teamId = ?", teamId).where("groupId = ?", groupId).execute();
    }
    public static void deleteAll() {
        new Delete().from(EBookItem.class).execute();
    }
}
