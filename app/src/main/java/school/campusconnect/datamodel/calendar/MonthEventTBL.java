package school.campusconnect.datamodel.calendar;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

@Table(name = "MonthEventTBL")
public class MonthEventTBL extends Model {

    @Column(name = "type")
    public String type;

    @Column(name = "group_id")
    public String group_id;

    @Column(name = "month")
    public int month;

    @Column(name = "year")
    public int year;

    @Column(name = "dayRes")
    public int dayRes;

    @Column(name = "monthRes")
    public int monthRes;

    @Column(name = "yearRes")
    public int yearRes;

    @Column(name = "_now")
    public String _now;


    public static List<MonthEventTBL> getEvent(String group_id,int month, int year)
    {
        return new Select().from(MonthEventTBL.class).where("group_id = ?",group_id).where("month = ?",month).where("year = ?",year).execute();
    }

    public static List<MonthEventTBL> getLastEvent()
    {
        return new Select().from(MonthEventTBL.class).orderBy("(group_id) DESC").limit(1).execute();
    }

    public static List<MonthEventTBL> deleteEvent(String group_id,int month,int year)
    {
        return new Delete().from(MonthEventTBL.class).where("group_id = ?",group_id).where("month = ?",month).where("year = ?",year).execute();
    }

    public static List<MonthEventTBL> deleteAllEvent()
    {
        return new Delete().from(MonthEventTBL.class).execute();
    }

}
