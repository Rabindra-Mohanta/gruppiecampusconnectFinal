package school.campusconnect.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codekidlabs.storagechooser.StorageChooser;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.BuildConfig;
import school.campusconnect.LeafApplication;
import school.campusconnect.R;
import school.campusconnect.activities.AttendanceDetailActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.adapters.FixedGridLayoutManager;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.attendance_report.AttendanceReportRes;
import school.campusconnect.datamodel.attendance_report.AttendanceReportResv2;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.student.StudentRes;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.MixOperations;

import static school.campusconnect.network.LeafManager.API_CLASSES;

public class AttendanceReportFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static String TAG = "AttendanceReportFragment";
    @Bind(R.id.spClass)
    public Spinner spClass;

    @Bind(R.id.imgLeft)
    public ImageView imgLeft;
    @Bind(R.id.tvMonth)
    public TextView tvMonth;
    @Bind(R.id.imgRight)
    public ImageView imgRight;

    @Bind(R.id.rvStudents)
    public RecyclerView rvStudents;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;
    @Bind(R.id.llClass)
    public LinearLayout llClass;

    /*@Bind(R.id.rvAttendTitle)
    public RecyclerView rvAttendTitle;*/

    private int StartDate;
    private int CurrentDate;
    private int EndDate;

    private int CurrentMonth;
    private int Month;
    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    Calendar calendar;
    private ArrayList<ClassResponse.ClassData> listClass;
    private ArrayList<AttendanceReportRes.AttendanceReportData> attendanceReportList;
    private ArrayList<AttendanceReportResv2.AttendanceReportData> attendanceReportListv2;
    private String selectedTeamId="";
    private String className="";
    private String classNameExcel="";

    private ArrayList<String> attendanceTitle = new ArrayList<>();
    private ArrayList<String> attendanceData = new ArrayList<>();

    private int ColumnCount = 0;

    @Bind(R.id.imgLeftDate)
    public ImageView imgLeftDate;

    @Bind(R.id.imgRightDate)
    public ImageView imgRightDate;

    String[] monthName= {"January","February","March", "April", "May", "June", "July",
            "August", "September", "October", "November",
            "December"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance_report,container,false);
        ButterKnife.bind(this,view);

        init();


        if(getArguments()!=null){
            selectedTeamId=getArguments().getString("team_id","");
            className=getArguments().getString("className","");
            classNameExcel = className;
        }

        if(TextUtils.isEmpty(selectedTeamId)){
            LeafManager leafManager = new LeafManager();
            leafManager.getClasses(this,GroupDashboardActivityNew.groupId);
        }else {
            llClass.setVisibility(View.GONE);
            getAttendanceReport();
        }


        return view;
    }

    private void init() {


        rvStudents.setLayoutManager(new LinearLayoutManager(getActivity()));

        /*attendanceTitle.add("Roll No");
        attendanceTitle.add("Name");*/

        calendar = Calendar.getInstance();
        /*calendar.set(Calendar.DAY_OF_MONTH ,1);


        ColumnCount = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        Log.e(TAG,"ColumnCount"+ColumnCount);
        Log.e(TAG,"YEAR"+calendar.get(Calendar.YEAR));
        Log.e(TAG,"MONTH"+calendar.get(Calendar.MONTH)+1);

        for (int i=0;i<ColumnCount;i++)
        {
           // attendanceTitle.add(i+"/"+calendar.get(Calendar.MONTH)+1+"/"+calendar.get(Calendar.YEAR));
            attendanceTitle.add(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
            calendar.add(Calendar.DAY_OF_MONTH,1);
        }

        rvAttendTitle.setAdapter(new AttendHeadAdpater(attendanceTitle));


        FixedGridLayoutManager fixedGridLayoutManager = new FixedGridLayoutManager();
        fixedGridLayoutManager.setTotalColumnCount(5);
        rvStudents.setLayoutManager(fixedGridLayoutManager);*/

        CurrentDate = calendar.get(Calendar.DATE);

        if (CurrentDate == 1)
        {
            EndDate = CurrentDate;
            StartDate = CurrentDate;
            imgLeftDate.setVisibility(View.INVISIBLE);
        }
        else
        {
            EndDate = CurrentDate;
            StartDate = EndDate-2;
        }

        String month = monthName[calendar.get(Calendar.MONTH)];
        CurrentMonth = getMonthNumber(month);
        Month = CurrentMonth;

        Log.e(TAG,"currentMonth"+CurrentMonth);

        spClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(listClass!=null){
                    selectedTeamId = listClass.get(i).getId();
                    classNameExcel = listClass.get(i).getName();
                    getAttendanceReport();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private int getMonthNumber(String month) {
        switch (month){
            case "January":return 1;
            case "February":return 2;
            case "March":return 3;
            case "April":return 4;
            case "May":return 5;
            case "June":return 6;
            case "July":return 7;
            case "August":return 8;
            case "September":return 9;
            case "October":return 10;
            case "November":return 11;
            case "December":return 12;

        }
        return 0;
    }

    private String getMonth(int month) {
        switch (month){
            case 1 : return "Jan";
            case 2 : return "Feb";
            case 3 : return "Mar";
            case 4 : return "Apr";
            case 5 : return "May";
            case 6 : return "Jun";
            case 7 : return "Jul";
            case 8 : return "Aug";
            case 9 : return "Sep";
            case 10 : return "Oct";
            case 11 : return "Nov";
            case 12 : return "Dec";

        }
        return "";
    }


    private int getLastDateOfMonth(int month) {

        switch (month){


            case 1 : return 31;
            case 2 :    if (calendar.get(Calendar.YEAR) % 4 == 0)
                        {
                            return 29;
                        }
                        else
                        {
                            return 28;
                        }


            case 3 : return 31;
            case 4 : return 30;
            case 5 : return 31;
            case 6 : return 30;
            case 7 : return 31;
            case 8 : return 31;
            case 9 : return 30;
            case 10 : return 31;
            case 11 : return 30;
            case 12 : return 31;

        }
        return 0;
    }



    private void getAttendanceReport() {


        Log.e(TAG,"month"+Month);
        Log.e(TAG,"start Date"+StartDate);
        Log.e(TAG,"End Date"+EndDate);

        tvMonth.setText(getMonth(Month).toUpperCase());

        showLoadingBar(progressBar,true);
        LeafManager leafManager = new LeafManager();
        leafManager.getAttendanceReportOffline(this,GroupDashboardActivityNew.groupId,selectedTeamId,Month,calendar.get(Calendar.YEAR),StartDate,EndDate);
       /* progressBar.setVisibility(View.VISIBLE);
        tvMonth.setText(MixOperations.getMonth(calendar.getTime()).toUpperCase());
        LeafManager leafManager = new LeafManager();
        leafManager.getAttendanceReportOffline(this,GroupDashboardActivityNew.groupId,selectedTeamId,calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR),StartDate,EndDate);*/
       // leafManager.getAttendanceReport(this,GroupDashboardActivityNew.groupId,selectedTeamId,calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.YEAR));
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @OnClick({R.id.imgLeft,R.id.imgRight,R.id.imgRightDate,R.id.imgLeftDate})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.imgLeft:
                //calendar.add(Calendar.MONTH,-1);
                Month = Month-1;

                if (Month == 1)
                {
                    imgLeft.setVisibility(View.INVISIBLE);
                }

                if (imgLeftDate.getVisibility() == View.INVISIBLE)
                {
                    imgLeftDate.setVisibility(View.VISIBLE);
                }

                if (imgRight.getVisibility() == View.INVISIBLE)
                {
                    imgRight.setVisibility(View.VISIBLE);
                }


                EndDate = getLastDateOfMonth(Month);
                StartDate = EndDate-2;

                getAttendanceReport();
                break;
            case R.id.imgRight:
               // calendar.add(Calendar.MONTH,1);
                Month = Month+1;

                if (Month == CurrentMonth)
                {
                    if (CurrentDate == 1)
                    {
                        EndDate = CurrentDate;
                        StartDate = CurrentDate;
                        imgLeftDate.setVisibility(View.INVISIBLE);
                        imgRightDate.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        EndDate = CurrentDate;
                        StartDate = EndDate-2;
                    }
                    imgRight.setVisibility(View.INVISIBLE);
                }
                else
                {
                    EndDate = getLastDateOfMonth(Month);
                    StartDate = EndDate-2;
                }

                if (imgLeft.getVisibility() == View.INVISIBLE)
                {
                    imgLeft.setVisibility(View.VISIBLE);
                }


                getAttendanceReport();
                break;

            case R.id.imgRightDate:

                EndDate = EndDate+1;
                StartDate = EndDate -2;

                if (EndDate == CurrentDate)
                {
                    imgRightDate.setVisibility(View.INVISIBLE);
                }

                if (EndDate == getLastDateOfMonth(Month))
                {
                    imgRightDate.setVisibility(View.INVISIBLE);
                }
                if (imgLeftDate.getVisibility() == View.INVISIBLE)
                {
                    imgLeftDate.setVisibility(View.VISIBLE);
                }
                getAttendanceReport();
                break;

            case R.id.imgLeftDate:
                EndDate = EndDate-1;
                StartDate = EndDate -2;
                if (StartDate == 1)
                {
                    imgLeftDate.setVisibility(View.INVISIBLE);
                }

                if (imgRightDate.getVisibility() == View.INVISIBLE)
                {
                    imgRightDate.setVisibility(View.VISIBLE);
                }
                getAttendanceReport();

                break;

        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        hideLoadingBar();
        if(getActivity()==null)
            return;

        if(apiId==API_CLASSES){
            ClassResponse res = (ClassResponse) response;
            listClass = res.getData();
            String[] stud = new String[listClass.size()];
            for (int i=0;i<listClass.size();i++){
                stud[i] = listClass.get(i).getName();
            }
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,stud);
            spClass.setAdapter(adapter);
        }

        if (apiId == LeafManager.API_ATTENDANCE_REPORT_OFFLINE)
        {
            AttendanceReportResv2 res = (AttendanceReportResv2) response;
            attendanceReportListv2 = res.result;

            rvStudents.setAdapter(new ReportStudentAdapterV2(res.result));

        }

        else {
            /*AttendanceReportResv2 res = (AttendanceReportResv2) response;
            attendanceReportListv2 = res.result;*/

         /*   AttendanceReportRes res = (AttendanceReportRes) response;
            rvStudents.setAdapter(new ReportStudentAdapter(res.result));
*/


           // rvAttendTitle.setAdapter(new AttendHeadAdpater(attendanceTitle));
            //rvStudents.setAdapter(new ReportStudentAdapterV2(res.result));
        }

    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
    }

    public class ReportStudentAdapter extends RecyclerView.Adapter<ReportStudentAdapter.ViewHolder>
    {
        List<AttendanceReportRes.AttendanceReportData> list;
        private Context mContext;

        public ReportStudentAdapter(List<AttendanceReportRes.AttendanceReportData> list) {
            this.list = list;
        }

        @Override
        public ReportStudentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_student,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ReportStudentAdapter.ViewHolder holder, final int position) {
            final AttendanceReportRes.AttendanceReportData item = list.get(position);

            holder.tvRollNo.setText(item.getRollNumber());
            holder.tvName.setText(item.getStudentName());
            holder.tvMorning.setText(item.getMorningPresentCount()+"("+item.getTotalMorningAttendance()+")");
            holder.tvEvering.setText(item.getAfternoonPresentCount()+"("+item.getTotalAfternoonAttendance()+")");
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText(getResources().getString(R.string.msg_no_data_found));
                }else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText(getResources().getString(R.string.msg_no_data_found));
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.tvRollNo) TextView tvRollNo;

            @Bind(R.id.tvName) TextView tvName;

            @Bind(R.id.tvMorning) TextView tvMorning;

            @Bind(R.id.tvEvering) TextView tvEvering;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editStudent(list.get(getAdapterPosition()));
                    }
                });
            }
        }
    }

    public class ReportStudentAdapterV2 extends RecyclerView.Adapter<ReportStudentAdapterV2.ViewHolder>
    {
        List<AttendanceReportResv2.AttendanceReportData> list;
        private Context mContext;

        public ReportStudentAdapterV2(List<AttendanceReportResv2.AttendanceReportData> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_atttendance_head,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            if (position == 0)
            {

                holder.llLinear.removeAllViews();

                for (int i = 0; i < list.get(0).getAttendanceReport().size()+2; i++) {

                    TextView valueTV = new TextView(mContext);

                    if (i>=2)
                    {
                        valueTV.setText(list.get(0).getAttendanceReport().get(i-2).getDate()+"\n( "+list.get(0).getAttendanceReport().get(i-2).getSubjectName()+" )");
                        valueTV.setLayoutParams(new LinearLayout.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.padding_120dp), mContext.getResources().getDimensionPixelSize(R.dimen.padding_50dp)));

                    }
                    else
                    {
                        if (i==0)
                        {
                            valueTV.setText(mContext.getResources().getString(R.string.txt_roll_no_student));
                            valueTV.setLayoutParams(new LinearLayout.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.padding_70dp), mContext.getResources().getDimensionPixelSize(R.dimen.padding_50dp)));
                        }
                         if(i==1)
                         {
                             valueTV.setText(mContext.getResources().getString(R.string.txt_name_student));
                             valueTV.setLayoutParams(new LinearLayout.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.padding_80dp), mContext.getResources().getDimensionPixelSize(R.dimen.padding_50dp)));
                         }
                    }
                    valueTV.setId((list.get(0).getAttendanceReport().size()+2)*position+i);

                    valueTV.setPadding(10,10,10,10);
                    valueTV.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    valueTV.setBackground(mContext.getResources().getDrawable(R.drawable.primary_border_rec_btn_bg));
                    valueTV.setGravity(Gravity.CENTER);
                    holder.llLinear.addView(valueTV);
                }
            }
            else
            {
                holder.llLinear.removeAllViews();


                for (int i = 0; i < list.get(position-1).getAttendanceReport().size()+2; i++) {

                    TextView valueTV = new TextView(mContext);

                    if (i>=2)
                    {
                        if (list.get(position-1).getAttendanceReport().get(i-2).getAttendance().equalsIgnoreCase("present"))
                        {
                            valueTV.setText("P");
                        }
                        else if (list.get(position-1).getAttendanceReport().get(i-2).getAttendance().equalsIgnoreCase("absent"))
                        {
                            valueTV.setText("A");
                        }
                        else
                        {
                            valueTV.setText("L");
                        }
                     /*   valueTV.setText(list.get(position-1).getAttendanceReport().get(i-2).getAttendance().equalsIgnoreCase("presemy")?"P":"A");*/
                        valueTV.setMinLines(2);
                        valueTV.setLayoutParams(new LinearLayout.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.padding_120dp), LinearLayout.LayoutParams.WRAP_CONTENT));
                    }
                    else
                    {
                        if (i==0)
                        {
                            valueTV.setText(list.get(position-1).getRollNumber());
                            valueTV.setMinLines(2);
                            valueTV.setLayoutParams(new LinearLayout.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.padding_70dp), LinearLayout.LayoutParams.WRAP_CONTENT));
                        }
                        if(i==1)
                        {
                            valueTV.setText(list.get(position-1).getStudentName());
                            valueTV.setLayoutParams(new LinearLayout.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.padding_80dp), LinearLayout.LayoutParams.WRAP_CONTENT));
                            valueTV.setSingleLine(true);
                            valueTV.setMinLines(2);
                        }
                    }

                    valueTV.setPadding(10,10,10,10);
                    valueTV.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                    valueTV.setBackground(mContext.getResources().getDrawable(R.drawable.primary_border_rec_btn_bg));
                    valueTV.setGravity(Gravity.CENTER);
                    holder.llLinear.addView(valueTV);
                }
            }



        }

       /* @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText("No Data found.");
                    return ColumnCount * 5;
                }else {
                    txtEmpty.setText("");
                }
                return ColumnCount * 5;
              //  return list.size();
            }
            else
            {
                txtEmpty.setText("No Data found.");
                return ColumnCount * 5;
            }

        }*/


        @Override
        public int getItemCount() {

            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText(getResources().getString(R.string.msg_no_report));
                }else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText(getResources().getString(R.string.msg_no_report));
                return 0;
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

        /*    @Bind(R.id.tvTitle)
            TextView tvTitle;*/

            @Bind(R.id.llLinear)
            LinearLayout llLinear;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);


            }
        }
    }

    private void editStudent(AttendanceReportRes.AttendanceReportData studentData) {
        Intent intent = new Intent(getActivity(), AttendanceDetailActivity.class);
        intent.putExtra("group_id",GroupDashboardActivityNew.groupId);
        intent.putExtra("team_id",selectedTeamId);
        intent.putExtra("title",studentData.getStudentName()+"- ("+className+")");
        intent.putExtra("calendar",calendar);
        intent.putExtra("userId",studentData.getUserId());
        intent.putExtra("rollNo",studentData.getRollNumber());
        startActivity(intent);
    }


    private boolean checkPermissionForWriteExternal() {
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            AppLog.e("External" + "permission", "checkpermission , granted");
            return true;
        } else {
            AppLog.e("External" + "permission", "checkpermission , denied");
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(getActivity(), getResources().getString(R.string.toast_storage_permission_needed), Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 21);
            }
            return false;
        }
    }
    public void exportDataToCSV() {

        if (!checkPermissionForWriteExternal()) {
            return;
        }

        StorageChooser chooser = new StorageChooser.Builder()
                .withActivity(getActivity())
                .withFragmentManager(getActivity().getFragmentManager())
                .withMemoryBar(false)
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .disableMultiSelect()
                .setDialogTitle("Select Folder")
                .showFoldersInGrid(true)
                .allowCustomPath(true)
                .build();

        chooser.show();

        chooser.setOnSelectListener(
                new StorageChooser.OnSelectListener() {
                                        @Override
                                        public void onSelect(String path)
                                        {

                                            File mainFolder = new File(path, LeafApplication.getInstance().getResources().getString(R.string.app_name));

                                            if (!mainFolder.exists()) {
                                                mainFolder.mkdir();
                                            }
                                            File csvFolder = new File(mainFolder,"Excel");
                                            if (!csvFolder.exists()) {
                                                csvFolder.mkdir();
                                            }

                                            File file = new File(csvFolder, classNameExcel+"_"+tvMonth.getText().toString() + ".xls");


                                            try {
                                                showLoadingBar(progressBar,true);
                                                if (!file.exists()) {
                                                    file.createNewFile();
                                                }
                                                HSSFWorkbook workbook = new HSSFWorkbook();
                                                HSSFSheet firstSheet = workbook.createSheet(classNameExcel+"_"+tvMonth.getText().toString());


                                                HSSFRow rowA = firstSheet.createRow(0);
                                                rowA.createCell(0).setCellValue("Roll No");
                                                rowA.createCell(1).setCellValue("Name");




                                                for (int i = 0; i < attendanceReportListv2.get(0).getAttendanceReport().size()+2; i++) {

                                                    if (i>=2)
                                                    {
                                                        rowA.createCell(i).setCellValue(attendanceReportListv2.get(0).getAttendanceReport().get(i-2).getDate()+"\n( "+attendanceReportListv2.get(0).getAttendanceReport().get(i-2).getSubjectName()+" )");
                                                    }
                                                }


                                                for (int j = 0; j < attendanceReportListv2.size(); j++) {

                                                    Log.e(TAG,"attendanceReportListv2 pos "+j);

                                                    HSSFRow rowData = firstSheet.createRow(j + 1);
                                                    rowData.createCell(0).setCellValue(attendanceReportListv2.get(j).getRollNumber());
                                                    rowData.createCell(1).setCellValue(attendanceReportListv2.get(j).getStudentName());

                                                    for (int i = 0; i < attendanceReportListv2.get(j).getAttendanceReport().size(); i++) {

                                                        Log.e(TAG,"attendanceReportListv2 value "+ attendanceReportListv2.get(j).getAttendanceReport().get(i).getAttendance());

                                                        if (attendanceReportListv2.get(j).getAttendanceReport().get(i).getAttendance().equalsIgnoreCase("present"))
                                                        {

                                                            rowData.createCell(i+2).setCellValue("P");
                                                        }
                                                        else if (attendanceReportListv2.get(j).getAttendanceReport().get(i).getAttendance().equalsIgnoreCase("absent"))
                                                        {

                                                            rowData.createCell(i+2).setCellValue("A");
                                                        }
                                                        else
                                                        {
                                                            rowData.createCell(i+2).setCellValue("L");
                                                        }

                                                    }
                                                }


                                                FileOutputStream fos = null;
                                                try {
                                                    fos = new FileOutputStream(file);
                                                    workbook.write(fos);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                } finally {
                                                    if (fos != null) {
                                                        try {
                                                            fos.flush();
                                                            fos.close();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                                hideLoadingBar();
                                                Toast.makeText(getContext(),getResources().getString(R.string.toast_file_download)+file.getAbsolutePath(),Toast.LENGTH_SHORT).show();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });

       /*
        File mainFolder = new File(getActivity().getFilesDir(), LeafApplication.getInstance().getResources().getString(R.string.app_name));
        if (!mainFolder.exists()) {
            mainFolder.mkdir();
        }
        File csvFolder = new File(mainFolder,"Excel");
        if (!csvFolder.exists()) {
            csvFolder.mkdir();
        }*/


    }

    private void shareFile(File file) {
        Uri uriFile;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            uriFile = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileprovider", file);
        } else {
            uriFile = Uri.fromFile(file);
        }
        Intent sharingIntent = new Intent();
        sharingIntent.setAction(Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uriFile) ;
        sharingIntent.setType("text/csv");
        startActivity(Intent.createChooser(sharingIntent, "share file with"));
    }


    public class AttendHeadAdpater extends RecyclerView.Adapter<AttendHeadAdpater.ViewHolder>
    {
        ArrayList<String> list;
        Context context;
        public AttendHeadAdpater(ArrayList<String> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            context = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_atttendance_head,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {


            /*    final AttendanceReportResv2.AttendanceReportData item = list.get(position);*/

            //if(position ==0)
            for (int i = 0; i < list.size()*7; i++) {

                TextView valueTV = new TextView(context);
                valueTV.setText(list.get(i%list.size()));
                valueTV.setId(position*33+i);
                valueTV.setLayoutParams(new LinearLayout.LayoutParams(80, LinearLayout.LayoutParams.WRAP_CONTENT));
                holder.llLinear.addView(valueTV);
            }
          /*  else
            {

            }*/
          //  holder.tvTitle.setText(list.get(position));

        //    holder.rvData.setAdapter(new ReportStudentAdapterV2());

        }

        @Override
        public int getItemCount() {
            return 50;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

        /*    @Bind(R.id.tvTitle)
            TextView tvTitle;*/

            @Bind(R.id.llLinear)
            LinearLayout llLinear;

           /* @Bind(R.id.rvData)
            RecyclerView rvData;*/


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

               /* itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editStudent(list.get(getAdapterPosition()));
                    }
                });*/
            }
        }
    }


    /*public class ReportStudentAdapterV2 extends RecyclerView.Adapter<ReportStudentAdapterV2.ViewHolder>
    {


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_student_date,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            Log.e(TAG,"onBindViewHolder"+getItemCount());
            *//*    final AttendanceReportResv2.AttendanceReportData item = list.get(position);*//*



        }

        @Override
        public int getItemCount() {
            return 50;

        }



        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.tvData)
            TextView tvData;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

               *//* itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editStudent(list.get(getAdapterPosition()));
                    }
                });*//*
            }
        }
    }*/
}
