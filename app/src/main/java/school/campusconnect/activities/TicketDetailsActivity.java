package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.databinding.ActivityTicketDetailsBinding;

public class TicketDetailsActivity extends BaseActivity implements View.OnClickListener {

    ActivityTicketDetailsBinding binding;
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    private Boolean expandable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_ticket_details);
        inits();
        listner();
    }

    private void listner() {

        binding.btnDeny.setOnClickListener(this);
        binding.btnDeny.setOnClickListener(this);
        binding.imgDropdown.setOnClickListener(this);
        binding.btnsendComment.setOnClickListener(this);
    }

    private void inits() {

        ButterKnife.bind(this);

        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_tikit_details));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imgDropdown:
                setExpandable();
                break;
        }
    }

    private void setExpandable() {

        if (expandable)
        {
            expandable = false;
            binding.llExpand.setVisibility(View.GONE);
            binding.imgDropdown.setRotation(180);
        }else
        {
            expandable = true;
            binding.llExpand.setVisibility(View.VISIBLE);
            binding.imgDropdown.setRotation(360);
        }

    }
}