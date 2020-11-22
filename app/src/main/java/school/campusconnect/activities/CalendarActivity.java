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
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.calendar.AddEventReq;
import school.campusconnect.datamodel.calendar.EventInDayRes;
import school.campusconnect.datamodel.calendar.EventListRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
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


    Calendar today=Calendar.getInstance();
    Calendar selected=Calendar.getInstance();
    LeafManager leafManager;
    EventAdapter eventAdapter;
    private MenuItem menuAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        init();

        initCalendar();

        getEventInMonth();

        getEventInDay();

    }

    private void initCalendar() {
        calendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> events = calendarView.getEvents(dateClicked);
                selected.setTime(dateClicked);
                AppLog.e(TAG, "Day was clicked: " + dateClicked + " with events " + events);
                getEventInDay();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                selected.setTime(firstDayOfNewMonth);
                AppLog.e(TAG, "Month was scrolled to: " + firstDayOfNewMonth);
                getEventInMonth();
                getEventInDay();
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

    private void getEventInMonth(){
        tvMonthYear.setText(MixOperations.convertDate(selected.getTime(),"MMMM yyyy"));
        calendarView.removeAllEvents();
        progressBar.setVisibility(View.VISIBLE);
        leafManager.getEventList(this,GroupDashboardActivityNew.groupId,selected.get(Calendar.MONTH)+1,selected.get(Calendar.YEAR));
    }
    private void getEventInDay(){
        eventAdapter.clear();
        tvData.setText("");
        progressBar.setVisibility(View.VISIBLE);
        leafManager.getEventInDay(this,GroupDashboardActivityNew.groupId,selected.get(Calendar.DAY_OF_MONTH),selected.get(Calendar.MONTH)+1,selected.get(Calendar.YEAR));
       showIfEventAddVisible();
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
                getEventInMonth();
                getEventInDay();
                break;
            case LeafManager.API_GET_EVENTS:
                EventListRes eventListRes= (EventListRes) response;
                AppLog.e(TAG,"eventListRes : "+eventListRes);
                setEvents(eventListRes.getData());
                break;
            case LeafManager.API_GET_EVENTS_IN_DAY:
                EventInDayRes eventInDayRes= (EventInDayRes) response;
                AppLog.e(TAG,"eventInDayRes : "+eventInDayRes);
                showEventInDay(eventInDayRes.getData());
                break;
            case LeafManager.API_DELETE_EVENT:
                 Toast.makeText(this, "Delete Success", Toast.LENGTH_LONG).show();
                getEventInMonth();
                getEventInDay();
                break;

        }
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
        SMBDialogUtils.showSMBDialogOKCancel(this, "Are you sure you want to delete?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                  progressBar.setVisibility(View.VISIBLE);
                  leafManager.deleteEvent(CalendarActivity.this,GroupDashboardActivityNew.groupId,eventInDayData.getEventId());
            }
        });
    }
}
