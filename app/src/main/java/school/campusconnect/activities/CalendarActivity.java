package school.campusconnect.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.calendar.AddEventReq;
import school.campusconnect.datamodel.calendar.DayEventTBL;
import school.campusconnect.datamodel.calendar.EventInDayRes;
import school.campusconnect.datamodel.calendar.EventListRes;
import school.campusconnect.datamodel.calendar.MonthEventTBL;
import school.campusconnect.fragments.DashboardNewUi.BaseTeamFragmentv3;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.DateTimeHelper;
import school.campusconnect.utils.MixOperations;
import school.campusconnect.views.SMBDialogUtils;

public class CalendarActivity extends BaseActivity {
    private static final String TAG = "CalendarActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    @Bind(R.id.tvData)
    public TextView tvData;
    @Bind(R.id.tvMonthYear)
    public TextView tvMonthYear;
    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    @Bind(R.id.compactcalendar_view)
    public CompactCalendarView calendarView;
    @Bind(R.id.rvEvents)
    public RecyclerView rvEvents;


    ArrayList<EventInDayRes.EventInDayData> dataDay = new ArrayList<>();
    ArrayList<EventListRes.EventData> dataMonth = new ArrayList<>();


    Calendar today = Calendar.getInstance();
    Calendar selected = Calendar.getInstance();
    LeafManager leafManager;
    EventAdapter eventAdapter;
    private MenuItem menuAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        init();

        initCalendar();

        checkEvent();

        getLocalyMonthEvent();

        getLocalyDayEvent();

    }

    private void checkEvent() {

        if (MonthEventTBL.getLastEvent().size() > 0)
        {
            if (MixOperations.isNewEventUpdate(LeafPreference.getInstance(getApplicationContext()).getString("CALENDAR_POST"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", MonthEventTBL.getLastEvent().get(0)._now)) {
                MonthEventTBL.deleteAllEvent();
                DayEventTBL.deleteAllEvent();
            }
        }

        if (DayEventTBL.getLastEvent().size() > 0)
        {
            if (MixOperations.isNewEventUpdate(LeafPreference.getInstance(getApplicationContext()).getString("CALENDAR_POST"), "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", DayEventTBL.getLastEvent().get(0)._now)) {
                MonthEventTBL.deleteAllEvent();
                DayEventTBL.deleteAllEvent();
            }
        }
    }

    private void initCalendar() {
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = calendarView.getEvents(dateClicked);
                selected.setTime(dateClicked);
                AppLog.e(TAG, "Day was clicked: " + dateClicked + " with events " + events);
                getLocalyDayEvent();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                selected.setTime(firstDayOfNewMonth);
                AppLog.e(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                getLocalyMonthEvent();
                getLocalyDayEvent();
            }
        });
        selected.set(Calendar.DAY_OF_MONTH,1);
        calendarView.setCurrentDate(selected.getTime());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.calendar_menu,menu);
        menuAdd=menu.findItem(R.id.menu_add_event);
        if(GroupDashboardActivityNew.isAdmin || GroupDashboardActivityNew.isPost){
            menuAdd.setVisible(true);
        }
        else {
            menuAdd.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            default:
                return super.onOptionsItemSelected(item);
            case R.id.menu_add_event:
                showAddEventPopup();
                return true;
        }

    }


    private void showIfEventAddVisible() {
        if(GroupDashboardActivityNew.isAdmin || GroupDashboardActivityNew.isPost){
            Calendar now = Calendar.getInstance();
            now.add(Calendar.DAY_OF_MONTH,-1);
            now.set(Calendar.HOUR_OF_DAY,0);
            now.set(Calendar.MINUTE,0);
            now.set(Calendar.SECOND,0);
            if(selected.after(now)){
                if(menuAdd!=null)
                    menuAdd.setVisible(true);
            }else {
                if(menuAdd!=null)
                    menuAdd.setVisible(false);
            }
        }
    }


    private void showAddEventPopup() {
        final Dialog dialog=new Dialog(this,R.style.AppTheme_AlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_event);
        final EditText etTitle=dialog.findViewById(R.id.etTitle);
        final RadioButton rbtEvent=dialog.findViewById(R.id.rbtEvent);
        dialog.findViewById(R.id.btnAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValueValid(etTitle)){
                    AddEventReq addEventReq=new AddEventReq();
                    addEventReq.setText(etTitle.getText().toString());
                    addEventReq.setType(rbtEvent.isChecked()?"event":"holiday");
                    progressBar.setVisibility(View.VISIBLE);
                    leafManager.addEvent(CalendarActivity.this,GroupDashboardActivityNew.groupId,addEventReq,selected.get(Calendar.DAY_OF_MONTH),selected.get(Calendar.MONTH)+1,selected.get(Calendar.YEAR));
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void init() {
        ButterKnife.bind(this);
        leafManager = new LeafManager();
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.calendar));

        eventAdapter = new EventAdapter();
        rvEvents.setAdapter(eventAdapter);

        if(getIntent().hasExtra("date")){
            selected.setTime(MixOperations.getDateFromStringDate(getIntent().getStringExtra("date"), Constants.DATE_FORMAT));
        }

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        switch (apiId) {
            case LeafManager.API_ADD_EVENT:
                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
                break;

            case LeafManager.API_GET_EVENTS:
                EventListRes eventListRes= (EventListRes) response;
                AppLog.e(TAG,"eventListRes : "+eventListRes);

                saveToLocallyMonthData(eventListRes.data);

                break;
            case LeafManager.API_GET_EVENTS_IN_DAY:
                EventInDayRes eventInDayRes= (EventInDayRes) response;
                AppLog.e(TAG,"eventInDayRes : "+eventInDayRes);

                saveToLocally(eventInDayRes.getData());

                break;
            case LeafManager.API_DELETE_EVENT:
                 Toast.makeText(this, "Delete Success", Toast.LENGTH_LONG).show();
                break;

        }
    }

    private void saveToLocallyMonthData(ArrayList<EventListRes.EventData> data) {

        MonthEventTBL.deleteEvent(GroupDashboardActivityNew.groupId,selected.get(Calendar.MONTH)+1,selected.get(Calendar.YEAR));

        if (data.size() > 0)
        {
            for (int i = 0;i<data.size();i++)
            {
                MonthEventTBL dayEventTBL = new MonthEventTBL();

                dayEventTBL.yearRes = data.get(i).getYear();
                dayEventTBL.monthRes = data.get(i).getMonth();

                dayEventTBL.year = selected.get(Calendar.YEAR);
                dayEventTBL.month = selected.get(Calendar.MONTH)+1;

                dayEventTBL.type = data.get(i).getType();
                dayEventTBL.dayRes = data.get(i).getDay();
                dayEventTBL.group_id = GroupDashboardActivityNew.groupId;

                if (!LeafPreference.getInstance(getApplicationContext()).getString("CALENDAR_POST").isEmpty())
                {
                    dayEventTBL._now = LeafPreference.getInstance(getApplicationContext()).getString("CALENDAR_POST");
                }
                else
                {
                    dayEventTBL._now = DateTimeHelper.getCurrentTime();
                }
                dayEventTBL.save();
            }

            setEvents(data);

        }

    }

    private void saveToLocally(ArrayList<EventInDayRes.EventInDayData> data) {

        DayEventTBL.deleteEvent(GroupDashboardActivityNew.groupId,selected.get(Calendar.DAY_OF_MONTH),selected.get(Calendar.MONTH)+1,selected.get(Calendar.YEAR));

        if (data.size() > 0)
        {
            for (int i = 0;i<data.size();i++)
            {
                DayEventTBL dayEventTBL = new DayEventTBL();
                dayEventTBL.eventId = data.get(i).getEventId();
                dayEventTBL.year = selected.get(Calendar.YEAR);
                dayEventTBL.month = selected.get(Calendar.MONTH)+1;
                dayEventTBL.type = data.get(i).getType();
                dayEventTBL.day = selected.get(Calendar.DAY_OF_MONTH);
                dayEventTBL.text = data.get(i).getText();
                dayEventTBL.canEdit = data.get(i).isCanEdit();
                dayEventTBL.group_id = GroupDashboardActivityNew.groupId;

                if (!LeafPreference.getInstance(getApplicationContext()).getString("CALENDAR_POST").isEmpty())
                {
                    dayEventTBL._now = LeafPreference.getInstance(getApplicationContext()).getString("CALENDAR_POST");
                }
                else
                {
                    dayEventTBL._now = DateTimeHelper.getCurrentTime();
                }
                dayEventTBL.save();
            }

            showEventInDay(data);

        }

    }


    private void getLocalyDayEvent() {


        List<DayEventTBL> eventTBLList = DayEventTBL.getEvent(GroupDashboardActivityNew.groupId,selected.get(Calendar.DAY_OF_MONTH),selected.get(Calendar.MONTH)+1,selected.get(Calendar.YEAR));

        dataDay.clear();

        if (eventTBLList.size() > 0)
        {
            for (int i = 0;i<eventTBLList.size();i++)
            {
                EventInDayRes.EventInDayData eventInDayData = new EventInDayRes.EventInDayData();

                eventInDayData.setType(eventTBLList.get(i).type);
                eventInDayData.setCanEdit(eventTBLList.get(i).canEdit);
                eventInDayData.setEventId(eventTBLList.get(i).eventId);
                eventInDayData.setText(eventTBLList.get(i).text);
                dataDay.add(eventInDayData);
            }


            showEventInDay(dataDay);
            showIfEventAddVisible();
        }
        else
        {
            getEventInDay();
        }

    }

    private void getLocalyMonthEvent() {

        tvMonthYear.setText(MixOperations.convertDate(selected.getTime(),"MMMM yyyy"));

        List<MonthEventTBL> eventTBLList = MonthEventTBL.getEvent(GroupDashboardActivityNew.groupId,selected.get(Calendar.MONTH)+1,selected.get(Calendar.YEAR));

        dataMonth.clear();

        if (eventTBLList.size() > 0)
        {
            for (int i = 0;i<eventTBLList.size();i++)
            {
                EventListRes.EventData eventInDayData = new EventListRes.EventData();

                eventInDayData.setType(eventTBLList.get(i).type);
                eventInDayData.setDay(eventTBLList.get(i).dayRes);
                eventInDayData.setYear(eventTBLList.get(i).yearRes);
                eventInDayData.setMonth(eventTBLList.get(i).monthRes);
                dataMonth.add(eventInDayData);
            }

            setEvents(dataMonth);
        }
        else
        {
            getEventInMonth();
        }

    }

    private void getEventInMonth(){

        progressBar.setVisibility(View.VISIBLE);
        leafManager.getEventList(this,GroupDashboardActivityNew.groupId,selected.get(Calendar.MONTH)+1,selected.get(Calendar.YEAR));
    }
    private void getEventInDay(){


        progressBar.setVisibility(View.VISIBLE);
        leafManager.getEventInDay(this,GroupDashboardActivityNew.groupId,selected.get(Calendar.DAY_OF_MONTH),selected.get(Calendar.MONTH)+1,selected.get(Calendar.YEAR));

    }


    private void showEventInDay(ArrayList<EventInDayRes.EventInDayData> data) {

        if(data!=null && data.size()>0){
            eventAdapter.addEvetns(data);
            tvData.setVisibility(View.GONE);
        }
        else {
            tvData.setText("No Data Found");
            tvData.setVisibility(View.VISIBLE);
        }
    }

    private void setEvents(ArrayList<EventListRes.EventData> data){
        calendarView.removeAllEvents();
        if(data!=null && data.size()>0){
            for (int i=0;i<data.size();i++){
                Calendar eventTime=Calendar.getInstance();
                eventTime.set(Calendar.DAY_OF_MONTH,data.get(i).getDay());
                eventTime.set(Calendar.MONTH,data.get(i).getMonth()-1);
                eventTime.set(Calendar.YEAR,data.get(i).getYear());
                AppLog.e(TAG,"event at : "+eventTime);
                if(data.get(i).getType().equalsIgnoreCase("event")){
                    Event ev1 = new Event(Color.GREEN, eventTime.getTimeInMillis(),"event");
                    calendarView.addEvent(ev1);
                }
                else {
                    Event ev1 = new Event(Color.RED, eventTime.getTimeInMillis(),"holiday");
                    calendarView.addEvent(ev1);
                }

            }
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Try Again", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onException(int apiId, String msg) {
        super.onException(apiId,msg);
        progressBar.setVisibility(View.GONE);
    }

    private class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder>{
        private ArrayList<EventInDayRes.EventInDayData> listEvent;

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            EventInDayRes.EventInDayData item = listEvent.get(position);
            holder.tvData.setText(item.getText());
            holder.tvDate.setText(MixOperations.convertDate(selected.getTime(),"MMMM dd"));
            if("event".equalsIgnoreCase(item.getType())){
                holder.imgEvent.setImageResource(R.drawable.event_bg);
            }
            else {
                holder.imgEvent.setImageResource(R.drawable.holiday_bg);
            }
        }

        @Override
        public int getItemCount() {
            return listEvent!=null?listEvent.size():0;
        }

        public void addEvetns(ArrayList<EventInDayRes.EventInDayData> data) {
            listEvent=data;
            notifyDataSetChanged();
        }

        public void clear() {
            if(listEvent!=null)
            {
                listEvent.clear();
                notifyDataSetChanged();
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvData;
            TextView tvDate;
            ImageView imgEvent;
            public ViewHolder(View itemView) {
                super(itemView);
                tvData = itemView.findViewById(R.id.tvData);
                tvDate = itemView.findViewById(R.id.tvDate);
                imgEvent = itemView.findViewById(R.id.imgEvent);

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if(listEvent.get(getAdapterPosition()).isCanEdit()){
                            showConfirmDeleteDialog(listEvent.get(getAdapterPosition()));
                            return true;
                        }
                        return false;
                    }
                });
            }
        }
    }

    private void showConfirmDeleteDialog(final EventInDayRes.EventInDayData eventInDayData) {
        SMBDialogUtils.showSMBDialogOKCancel(this,  getResources().getString(R.string.dialog_are_you_want_to_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                  progressBar.setVisibility(View.VISIBLE);
                  leafManager.deleteEvent(CalendarActivity.this,GroupDashboardActivityNew.groupId,eventInDayData.getEventId());
            }
        });
    }
}
