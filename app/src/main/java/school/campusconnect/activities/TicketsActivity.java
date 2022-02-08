package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.adapters.TicketsAdapter;
import school.campusconnect.databinding.ActivityTicketsBinding;
import school.campusconnect.utils.AppLog;

public class TicketsActivity extends BaseActivity implements TicketsAdapter.OnClickListener {

    public static String TAG = "TicketsActivity";
    ActivityTicketsBinding binding;
    String Approval = "Not Approved";
    String[] approvalList;

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_tickets);

        inits();
    }

    private void inits() {
        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_tikit));

        bindSp();

        binding.rvTickets.setAdapter(new TicketsAdapter(this));


    }

    private void bindSp() {

        approvalList = getResources().getStringArray(R.array.array_approval);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item_spinner_white,approvalList);
        binding.spApproval.setAdapter(adapter);


        binding.spApproval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AppLog.e(TAG, "onItemSelected : " + position);
                Approval = approvalList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void add(int id) {
        startActivity(new Intent(getApplicationContext(),TicketDetailsActivity.class));
    }
}