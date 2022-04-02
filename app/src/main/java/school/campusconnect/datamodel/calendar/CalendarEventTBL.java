package school.campusconnect.datamodel.calendar;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "CalendarEventTBL")
public class CalendarEventTBL extends Model {

    @Column(name = "type")
    public String type;

    @Column(name = "text")
    public String text;

    @Column(name = "eventId")
    public String eventId;

    @Column(name = "canEdit")
    public boolean canEdit;
}
