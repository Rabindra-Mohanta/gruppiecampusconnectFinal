package school.campusconnect.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.adapters.TeamSubjectAdapter;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.GroupValidationError;
import school.campusconnect.datamodel.marksheet.AddMarkCardReq;
import school.campusconnect.datamodel.marksheet.SubjectTeamResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;

public class AddMarkCardActivity extends BaseActivity implements LeafManager.OnAddUpdateListener<GroupValidationError> {

    private static final String TAG = "CreateTeamActivity";
    @Bind(R.id.etName)
    EditText etName;

    @Bind(R.id.etFrom)
    EditText etFrom;

    @Bind(R.id.etTo)
    EditText etTo;

    @Bind(R.id.etMarks)
    EditText etMarks;

    @Bind(R.id.etMinMarks)
    EditText etMinMarks;

    @Bind(R.id.tvSubject)
    EditText tvSubject;

    @Bind(R.id.imgAdd)
    ImageView imgAdd;


    @Bind(R.id.btnCreateClass)
    Button btnCreateClass;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    @Bind(R.id.rvSubjects)
    RecyclerView rvSubjects;

    LeafManager leafManager;

    private String groupId;
    private String teamId;

    TeamSubjectAdapter adapter = new TeamSubjectAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mark_card);

        init();

        showLoadingBar(progressBar);
   //     progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getTeamSubjects(this, groupId, teamId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

      /*  if (isEdit) {
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        }
*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      /*  if (item.getItemId() == R.id.menuDelete) {
            if (!isConnectionAvailable()) {
                showNoNetworkMsg();
                return true;
            }

            SMBDialogUtils.showSMBDialogOKCancel(this, "Are you sure you want to permanently delete this Subjects.?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressBar.setVisibility(View.VISIBLE);
                    leafManager.deleteSubjects(AddMarkCardActivity.this, GroupDashboardActivityNew.groupId, subjectData.getSubjectId());
                }
            });
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        ButterKnife.bind(this);

        rvSubjects.setAdapter(adapter);

        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_create_mark_card));
        leafManager = new LeafManager();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            groupId = bundle.getString("group_id", "");
            teamId = bundle.getString("team_id", "");
            AppLog.e(TAG, ",groupId:" + groupId + ",teamId:" + teamId);
        }

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(tvSubject.getText().toString().trim())) {
                    Toast.makeText(AddMarkCardActivity.this, getResources().getString(R.string.toast_please_add_subject), Toast.LENGTH_SHORT).show();
                    tvSubject.requestFocus();
                } else if (TextUtils.isEmpty(etMarks.getText().toString().trim())) {
                    etMarks.requestFocus();
                    Toast.makeText(AddMarkCardActivity.this, getResources().getString(R.string.toast_please_add_max_marks), Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(etMinMarks.getText().toString().trim())) {
                    etMinMarks.requestFocus();
                    Toast.makeText(AddMarkCardActivity.this, getResources().getString(R.string.toast_please_add_min_marks), Toast.LENGTH_SHORT).show();
                } else {
                    adapter.add(new TeamSubjectAdapter.AddSubjectData(tvSubject.getText().toString(), etMarks.getText().toString(), etMinMarks.getText().toString()));
                    hide_keyboard(view);
                    tvSubject.setText("");
                    etMarks.setText("");
                    etMinMarks.setText("");
                    tvSubject.requestFocus();
                }
            }
        });


    }

    public void hide_keyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @OnClick({R.id.btnCreateClass, R.id.etFrom, R.id.etTo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCreateClass:
                if (isValid()) {

                    if (!TextUtils.isEmpty(tvSubject.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etMarks.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etMinMarks.getText().toString().trim())) {

                    } else if (TextUtils.isEmpty(tvSubject.getText().toString().trim()) &&
                            TextUtils.isEmpty(etMarks.getText().toString().trim()) &&
                            TextUtils.isEmpty(etMinMarks.getText().toString().trim())) {

                    } else {
                        if (TextUtils.isEmpty(tvSubject.getText().toString().trim())) {
                            Toast.makeText(AddMarkCardActivity.this, getResources().getString(R.string.toast_please_add_subject), Toast.LENGTH_SHORT).show();
                            tvSubject.requestFocus();
                            return;
                        } else if (TextUtils.isEmpty(etMarks.getText().toString().trim())) {
                            etMarks.requestFocus();
                            Toast.makeText(AddMarkCardActivity.this, getResources().getString(R.string.toast_please_add_max_marks), Toast.LENGTH_SHORT).show();
                            return;
                        } else if (TextUtils.isEmpty(etMinMarks.getText().toString().trim())) {
                            etMinMarks.requestFocus();
                            Toast.makeText(AddMarkCardActivity.this, getResources().getString(R.string.toast_please_add_min_marks), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }


                    if (!isConnectionAvailable()) {
                        showNoNetworkMsg();
                        return;
                    }

                    AddMarkCardReq request = new AddMarkCardReq();
                    request.title = etName.getText().toString();
                    ArrayList<TeamSubjectAdapter.AddSubjectData> list = adapter.getList();
                    HashMap<String, HashMap<String, Object>> hashMap = new HashMap<>();
                    for (int i = 0; i < list.size(); i++) {
                        if (TextUtils.isEmpty(list.get(i).maxMarks)) {
                            Toast.makeText(this, getResources().getString(R.string.toast_please_add_max_marks), Toast.LENGTH_SHORT).show();
                            return;
                        }else if (TextUtils.isEmpty(list.get(i).minMarks)) {
                            Toast.makeText(this, getResources().getString(R.string.toast_please_add_min_marks), Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            HashMap<String, Object> values = new HashMap<>();
                            values.put("max", TextUtils.isDigitsOnly(list.get(i).maxMarks)?Integer.parseInt(list.get(i).maxMarks):list.get(i).maxMarks);
                            values.put("min", TextUtils.isDigitsOnly(list.get(i).minMarks)?Integer.parseInt(list.get(i).minMarks):list.get(i).minMarks);
                            hashMap.put(list.get(i).name, values);
                        }
                    }
                    if (!TextUtils.isEmpty(tvSubject.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etMarks.getText().toString().trim()) &&
                            !TextUtils.isEmpty(etMinMarks.getText().toString().trim())) {
                        HashMap<String, Object> values = new HashMap<>();
                        values.put("max", TextUtils.isDigitsOnly(etMarks.getText().toString())?Integer.parseInt(etMarks.getText().toString()):etMarks.getText().toString());
                        values.put("min", TextUtils.isDigitsOnly(etMinMarks.getText().toString())?Integer.parseInt(etMinMarks.getText().toString()):etMinMarks.getText().toString());
                        hashMap.put(tvSubject.getText().toString(), values);
                    }
                    request.subjects = new ArrayList<>();
                    request.subjects.add(hashMap);
                    request.duration = etFrom.getText().toString() + " to " + etTo.getText().toString();

                    showLoadingBar(progressBar);
                   // progressBar.setVisibility(View.VISIBLE);
                    AppLog.e(TAG, "request :" + request);
                    leafManager.createMarkCard(this, GroupDashboardActivityNew.groupId, teamId, request);
                }
                break;
            case R.id.etFrom: {
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog fragment = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        etFrom.setText(format.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                //fragment.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                fragment.show();
                break;
            }
            case R.id.etTo:
                final Calendar calendar = Calendar.getInstance();
                DatePickerDialog fragment = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        etTo.setText(format.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                //fragment.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
                fragment.show();
                break;
        }
    }



    public boolean isValid() {
        boolean valid = true;
        if (!isValueValidOnly(etName)) {
            Toast.makeText(this, getResources().getString(R.string.toast_please_enter_title), Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (adapter.getList().size() == 0) {
            Toast.makeText(this, getResources().getString(R.string.toast_add_one_subject), Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (!isValueValidOnly(etFrom)) {
            Toast.makeText(this, getResources().getString(R.string.toast_please_select_from_date), Toast.LENGTH_SHORT).show();
            valid = false;
        } else if (!isValueValidOnly(etTo)) {
            Toast.makeText(this, getResources().getString(R.string.toast_please_select_to_date), Toast.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            hideLoadingBar();
           // progressBar.setVisibility(View.GONE);

        switch (apiId) {
            case LeafManager.API_CREATE_MARK_CARD:
                Intent intent = new Intent(this, UpdateMarksheetActivity.class);
                intent.putExtras(getIntent().getExtras());
                startActivity(intent);
                finish();
                break;
            case LeafManager.API_TEAM_SUBJECT_LIST:
                SubjectTeamResponse res = (SubjectTeamResponse) response;
                List<SubjectTeamResponse.SubjectDataTeam> result = res.getData();
                AppLog.e(TAG, "SubjectTeamResponse " + result);
                if (result != null && result.size() > 0) {
                    adapter.addList(result.get(0).subjects);
                }
                break;

        }
    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel<GroupValidationError> error) {
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);

        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            if (apiId == LeafManager.API_SUBJECTS_DELETE) {
                GroupValidationError groupValidationError = (GroupValidationError) error;
                Toast.makeText(this, groupValidationError.message, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, error.title, Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public void onException(int apiId, String msg) {
        super.onException(apiId, msg);
        if (progressBar != null)
            hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
        Toast.makeText(this, getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }
}
