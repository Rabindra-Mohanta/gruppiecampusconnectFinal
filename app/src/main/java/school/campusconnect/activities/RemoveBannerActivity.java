package school.campusconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.databinding.ActivityRemoveBannerBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.banner.BannerAddReq;
import school.campusconnect.datamodel.banner.BannerRes;
import school.campusconnect.fragments.BoothListMyTeamFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppDialog;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;

public class RemoveBannerActivity extends BaseActivity implements LeafManager.OnCommunicationListener {

    public static String TAG = "RemoveBannerActivity";
    ActivityRemoveBannerBinding binding;
    BannerRes.BannerData bannerData = new BannerRes.BannerData();
    BannerAdapater bannerAdapater;

    LeafManager manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_remove_banner);
        inits();
    }

    private void inits() {

        if (getIntent() != null)
        {
            bannerData = (BannerRes.BannerData) getIntent().getSerializableExtra("data");
        }
        Log.e(TAG,"banner DAta"+new Gson().toJson(bannerData));

        manager = new LeafManager();
        bannerAdapater = new BannerAdapater();
        binding.rvBannerList.setAdapter(bannerAdapater);
        bannerAdapater.add(bannerData.fileName);


        binding.updateBannerList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BannerAddReq req = new BannerAddReq();
                req.setBannerFile(bannerData.fileName);
                req.setBannerFileType("image");

                AppLog.e(TAG, "send data : " + new Gson().toJson(req));
                showLoadingBar(binding.progressBar);
                //  progressBar.setVisibility(View.VISIBLE);
             //   binding.progressBar.setVisibility(View.VISIBLE);
                manager.addBannerList(RemoveBannerActivity.this,GroupDashboardActivityNew.groupId,req);

            }
        });

        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED,returnIntent);
                finish();
            }
        });
    }


    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        //binding.progressBar.setVisibility(View.GONE);
       if (apiId == LeafManager.API_ADD_BANNER_LIST)
       {
           Intent returnIntent = new Intent();
           setResult(Activity.RESULT_OK,returnIntent);
           finish();
       }
    }


    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        //binding.progressBar.setVisibility(View.GONE);
        if (msg.contains("401:Unauthorized") || msg.contains("401")) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else if (msg.contains("404")) {
            Toast.makeText(getApplicationContext(),  getResources().getString(R.string.toast_no_post), Toast.LENGTH_SHORT).show();
        } else if (msg.contains("418")) {
            if (apiId == LeafManager.API_REPORT)
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast_already_reported), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }

    }


    public void onException(int apiId, String msg) {
        hideLoadingBar();
        //binding.progressBar.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.api_exception_msg), Toast.LENGTH_SHORT).show();
    }

    public class BannerAdapater extends RecyclerView.Adapter<BannerAdapater.ViewHolder>
    {
        ArrayList<String> fileName;
        Context context;


        @NonNull
        @Override
        public BannerAdapater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            context = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner_remove,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull BannerAdapater.ViewHolder holder, int position) {

            int size = dpToPx(context.getResources().getDisplayMetrics(), 80);

            RequestOptions reqOption = new RequestOptions();
            reqOption.override(size, size);

            Glide.with(context).load(Constants.decodeUrlToBase64(fileName.get(position))).apply(reqOption).diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true).into(holder.img);

            holder.imgRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AppDialog.showConfirmDialog(RemoveBannerActivity.this, getResources().getString(R.string.dialog_remove_banner), new AppDialog.AppDialogListener() {
                        @Override
                        public void okPositiveClick(DialogInterface dialog) {
                            onClickRemove(position);
                            dialog.dismiss();

                        }
                        @Override
                        public void okCancelClick(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    });
                }
            });
        }
        public int dpToPx(DisplayMetrics displayMetrics, int dp) {

            return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        }


        @Override
        public int getItemCount() {
            return fileName.size();
        }

        public void add(ArrayList<String> fileName) {
            this.fileName = fileName;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.img)
            ImageView img;

            @Bind(R.id.imgRemove)
            ImageView imgRemove;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

            }
        }
    }

    private void onClickRemove(int position) {

        Log.e(TAG,"position"+position);

        bannerData.fileName.remove(position);

        Log.e(TAG,"banner DAta"+bannerData.fileName);

        bannerAdapater.add(bannerData.fileName);
    }
}