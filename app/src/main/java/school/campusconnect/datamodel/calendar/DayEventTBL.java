package school.campusconnect.datamodel.calendar;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import school.campusconnect.datamodel.gallery.GalleryTable;

@Table(name = "DayEventTBL")
public class DayEventTBL extends Model {

    @Column(name = "type")
    public String type;

    @Column(name = "text")
    public String text;

    @Column(name = "eventId")
    public String eventId;

    @Column(name = "canEdit")
    public boolean canEdit;

    @Column(name = "group_id")
    public String group_id;

    @Column(name = "day")
    public int day;

    @Column(name = "month")
    public int month;

    @Column(name = "year")
    public int year;

    @Column(name = "_now")
    public String _now;


    public static List<DayEventTBL> getEvent(String group_id,int day,int month,int year)
    {
        return new Select().from(DayEventTBL.class).where("group_id = ?",group_id).where("day = ?",day).where("month = ?",month).where("year = ?",year).execute();
    }

    public static List<MonthEventTBL> getLastEvent()
    {
        return new Select().from(MonthEventTBL.class).orderBy("(group_id) DESC").limit(1).execute();
    }
    public static List<DayEventTBL> deleteEvent(String group_id,int day,int month,int year)
    {
        return new Select().from(DayEventTBL.class).where("group_id = ?",group_id).where("day = ?",day).where("month = ?",month).where("year = ?",year).execute();
    }

    public static List<DayEventTBL> deleteAllEvent()
    {
        return new Select().from(DayEventTBL.class).execute();
    }
}
