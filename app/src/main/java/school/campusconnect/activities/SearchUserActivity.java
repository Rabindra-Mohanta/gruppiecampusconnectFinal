package school.campusconnect.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.adapters.TeamListAdapter;
import school.campusconnect.databinding.ActivitySearchUserBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.booths.BoothsTBL;
import school.campusconnect.datamodel.searchUser.SearchUserModel;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.fragments.BoothListMyTeamFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class SearchUserActivity extends BaseActivity implements LeafManager.OnCommunicationListener{

    public static String TAG = "SearchUserActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    ActivitySearchUserBinding binding;
    LeafManager manager;

    ClassesAdapter adapter;

    private int REQUEST_UPDATE_PROFILE = 8;

    private List<SearchUserModel.SearchUserData> filteredList = new ArrayList<>();

    private Handler mHandler = new Handler();
    String text;
    Runnable myRunnable = new Runnable() {
        @Override
        public void run() {

            try{
                mHandler.postDelayed(myRunnable, 1000);
                binding.progressBar.setVisibility(View.VISIBLE);
                manager.getSearchUser(SearchUserActivity.this,GroupDashboardActivityNew.groupId,text);
            }catch (Exception e)
            {
                Log.e(TAG,"exception"+ e.getMessage());
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_search_user);
        inits();
    }

    private void inits() {

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.hint_search_voter));

        manager = new LeafManager();
        binding.edtSearch.setHint(getResources().getString(R.string.hint_search_voter));

        binding.rvSearch.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new ClassesAdapter();
        binding.rvSearch.setAdapter(adapter);

        if (!checkPermissionForCall())
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 10);
        }

        binding.edtSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                mHandler.removeCallbacks(myRunnable);
                if (s.toString().length() > 2)
                {
                    mHandler.post(myRunnable);
                    searchData(s.toString());
                }
                else
                {
                    adapter.add(filteredList);
                }
            }
        });

    }

    private boolean checkPermissionForCall() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            Log.e("External" + "permission", "checkpermission , granted");
            return true;
        } else {
            Log.e("External" + "permission", "checkpermission , denied");
            return false;
        }
    }

    private void searchData(String text) {
        this.text = text;
        adapter.add(filteredList);

    }



    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        binding.progressBar.setVisibility(View.GONE);

        if (apiId == LeafManager.API_SEARCH_USER)
        {
            SearchUserModel res = (SearchUserModel) response;
            adapter.add(res.getData());
        }
        super.onSuccess(apiId, response);
    }

    @Override
    public void onFailure(int apiId, String msg) {
        binding.progressBar.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        super.onFailure(apiId, msg);
    }

    @Override
    public void onException(int apiId, String msg) {
        binding.progressBar.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
        super.onException(apiId, msg);

    }

    public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder>
    {
        List<SearchUserModel.SearchUserData> list;
        private Context mContext;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_voter,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final SearchUserModel.SearchUserData item = list.get(position);

            if (!TextUtils.isEmpty(item.getImage())) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50,50).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(50,50).into(holder.imgTeam, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.img_lead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.img_lead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));
                                        holder.img_lead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.img_lead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.getName()), ImageUtil.getRandomColor(position));
                holder.img_lead_default.setImageDrawable(drawable);
            }

            holder.txt_name.setText(item.getName());



            holder.img_lead_default.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (GroupDashboardActivityNew.isAdmin)
                    {
                        Intent i = new Intent(getApplicationContext(), VoterProfileActivity.class);
                        i.putExtra("userID",item.getUserId());
                        i.putExtra("name",item.getName());
                        i.putExtra("teamID",item.getVoterId());
                        startActivityForResult(i,REQUEST_UPDATE_PROFILE);
                    }

                }
            });

            holder.img_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (item.getPhone() != null &&!item.getPhone().isEmpty())
                    {
                        String uri = "tel:" + item.getPhone().trim();
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {

                    binding.txtEmpty.setText(getResources().getString(R.string.txt_no_booth_found));
                }
                else {
                    binding.txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                binding.txtEmpty.setText(getResources().getString(R.string.txt_no_booth_found));
                return 0;
            }

        }

        public void add(List<SearchUserModel.SearchUserData> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.img_lead)
            ImageView imgTeam;

            @Bind(R.id.img_lead_default)
            ImageView img_lead_default;

            @Bind(R.id.txt_name)
            TextView txt_name;

            @Bind(R.id.img_call)
            ImageView img_call;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_UPDATE_PROFILE)
        {
            if (resultCode == Activity.RESULT_OK)
            {

            }
        }
    }
}