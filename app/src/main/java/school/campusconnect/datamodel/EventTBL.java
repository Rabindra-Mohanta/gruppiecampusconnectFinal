package school.campusconnect.datamodel;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "EventTBL")
public class EventTBL extends Model {
    @Column(name = "insertedId")
    public String insertedId;

    @Column(name = "groupId")
    public String groupId;

    @Column(name = "eventType")
    public String eventType;

    @Column(name = "eventName")
    public String eventName;

    @Column(name = "eventAt")
    public String eventAt;

    @Column(name = "now")
    public long now;

    public EventTBL(){
        super();
    }

    public static List<EventTBL> getAll(int eventType,String groupId) {
        return new Select().from(EventTBL.class).where("eventType = ?", eventType).where("groupId = ?", groupId).execute();
    }
    public static EventTBL get(String insertedId) {
        List<EventTBL> list = new Select().from(EventTBL.class).where("insertedId = ?", insertedId).execute();
        if(list!=null && list.size()>0){
           return list.get(list.size()-1);
        }
        return null;
    } public static EventTBL getByEventType(int eventType,String groupId) {
        List<EventTBL> list = new Select().from(EventTBL.class).where("eventType = ?", eventType).where("groupId = ?", groupId).orderBy("eventType DESC").limit(1).execute();
        if(list!=null && list.size()>0){
           return list.get(0);
        }
        return null;
    }
    public static void deleteAll() {
        new Delete().from(EventTBL.class).execute();
    }
}
