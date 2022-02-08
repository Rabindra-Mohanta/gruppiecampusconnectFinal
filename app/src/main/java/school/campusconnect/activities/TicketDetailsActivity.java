package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.Assymetric.multiimages.ItemImage;
import school.campusconnect.R;
import school.campusconnect.adapters.GalleryChildAdapter;
import school.campusconnect.databinding.ActivityTicketDetailsBinding;
import school.campusconnect.datamodel.ticket.TicketListResponse;

public class TicketDetailsActivity extends BaseActivity implements View.OnClickListener {

    public static String TAG = "TicketDetailsActivity";
    ActivityTicketDetailsBinding binding;
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    private TicketListResponse.TicketData taskData;
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

        if (getIntent() != null)
        {
            taskData = (TicketListResponse.TicketData) getIntent().getSerializableExtra("data");
            Log.e(TAG,"get Issue Text"+taskData.getIssueText());

            if (taskData != null)
            {
                binding.issueTitle.setText("Constituency Issue "+taskData.getConstituencyIssueJurisdiction());
                binding.tvName.setText(taskData.getConstituencyIssue());
                binding.tvDecs.setText(taskData.getConstituencyIssueDepartmentTaskForce().getConstituencyDesignation());

                binding.tvAttachmentDecs.setText(taskData.getConstituencyIssue());
                binding.tvAttachmentLocation.setText(taskData.getIssueLocation().getLandmark());
                binding.tvAttachmentAddress.setText(taskData.getIssueLocation().getAddress());

                if (taskData.getFileName() != null &&  taskData.getFileName().size()> 0)
                {
                  /*  ArrayList<ItemImage> itemImages = new ArrayList<>();

                    for(int i=0;i<taskData.getFileName().size();i++)
                    {
                        ItemImage itemImage = null;
                        itemImage.setImagePath(taskData.getFileName().get(i).toString());
                        itemImages.add(itemImage);
                    }
                    GalleryChildAdapter adapter;
                    if ( taskData.getFileName().size() == 3)
                    {
                        adapter = new GalleryChildAdapter(2, taskData.getFileName().size(), getApplicationContext(), item);
                    }*/

                }

                else
                {

                }

            }
        }
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