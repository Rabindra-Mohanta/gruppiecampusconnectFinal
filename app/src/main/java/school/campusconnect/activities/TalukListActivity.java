package school.campusconnect.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import school.campusconnect.R;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.datamodel.TaluksRes;
import school.campusconnect.fragments.HomeFragment;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

public class TalukListActivity extends BaseActivity implements LeafManager.OnCommunicationListener {

    private static final String TAG = "TalukListActivity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    private AlertDialog editDialog;

    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taluk_list);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setTitle(getResources().getString(R.string.app_name));

        _init();

        getTaluksList();
    }

    private void _init() {
        rvClass.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        AppLog.e(TAG, "BackStack count : " + fm.getBackStackEntryCount());

        editDialog = SMBDialogUtils.showSMBDialogOKCancel_(this, getResources().getString(R.string.msg_app_exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TalukListActivity.super.onBackPressed();
            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (editDialog != null) {
            editDialog.cancel();
        }
    }

    public boolean isConnectionAvailable() {
        ConnectivityManager connectivityManager;
        NetworkInfo networkinfo;
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        networkinfo = connectivityManager.getActiveNetworkInfo();

        return (networkinfo != null && networkinfo.isConnectedOrConnecting());

    }

    private void getTaluksList() {
        if (isConnectionAvailable()) {
            showLoadingBar(progressBar);
         //   progressBar.setVisibility(View.VISIBLE);
            LeafManager leafManager = new LeafManager();
            leafManager.getTaluks(this);
        } else {
            showNoNetworkMsg();
        }

    }

    public void showNoNetworkMsg() {
        /*SMBDialogUtils.showSMBDialogOK(getActivity(), getString(R.string.no_internet), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });*/
        try {
            Snackbar snackbar = Snackbar.make(findViewById(R.id.toolbar), R.string.no_internet, Snackbar.LENGTH_SHORT)
                    .setAction(getResources().getString(R.string.action_settings), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                        }
                    }).setActionTextColor(Color.WHITE);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.snackbar_textsize));
            textView.setTextColor(Color.WHITE);
            snackbar.show();

        } catch (Exception e) {
            AppLog.e("SnackBar", "error is " + e.toString());
            SMBDialogUtils.showSMBDialogOK(this, getString(R.string.no_internet), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();
        // progressBar.setVisibility(View.GONE);
        TaluksRes taluksRes = (TaluksRes) response;
        AppLog.e(TAG, "taluksRes " + taluksRes);
        rvClass.setAdapter(new GroupAdapterNew(taluksRes.data));
    }

    @Override
    public void onFailure(int apiId, String msg) {

    }

    @Override
    public void onException(int apiId, String msg) {

    }

    public class GroupAdapterNew extends RecyclerView.Adapter<GroupAdapterNew.ViewHolder> {
        List<TaluksRes.TalusData> list;
        private Context mContext;

        public GroupAdapterNew(List<TaluksRes.TalusData> list) {
            this.list = list;
        }

        @Override
        public GroupAdapterNew.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_taluks, parent, false);
            return new GroupAdapterNew.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final GroupAdapterNew.ViewHolder holder, final int position) {
            final TaluksRes.TalusData item = list.get(position);

            if (!TextUtils.isEmpty(item.image)) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).resize(200, 200).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgGroupNew,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.imgGroupNewDefault.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).resize(200, 200).into(holder.imgGroupNew, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.imgGroupNewDefault.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.imgGroupNewDefault.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.talukName), ImageUtil.getRandomColor(position));
                                        holder.imgGroupNewDefault.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.imgGroupNewDefault.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.talukName), ImageUtil.getRandomColor(position));
                holder.imgGroupNewDefault.setImageDrawable(drawable);
            }

            holder.txtName.setText(item.talukName);
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText(getResources().getString(R.string.txt_no_taluks_found));
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText(getResources().getString(R.string.txt_no_taluks_found));
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.txt_group_name)
            TextView txtName;

            @Bind(R.id.img_group_new)
            CircleImageView imgGroupNew;

            @Bind(R.id.img_group_new_default)
            ImageView imgGroupNewDefault;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            @OnClick({R.id.relative})
            public void onMovieClick(View v) {
                switch (v.getId()) {

                    case R.id.relative:
                        onTaluksSelect(list.get(getLayoutPosition()));
                        break;
                }
            }
        }
    }

    private void onTaluksSelect(TaluksRes.TalusData talusData) {
        Intent intent = new Intent(TalukListActivity.this, Home.class);
        intent.putExtra("from","TALUK");
        intent.putExtra("talukName", talusData.talukName);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        //menu.findItem(R.id.menu_change_pin).setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:

                SMBDialogUtils.showSMBDialogConfirmCancel(this, getResources().getString(R.string.smb_logout), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                        finish();
                    }
                });


                return true;

            case R.id.menu_change_pin:
                Intent intentpin= new Intent(this, ChangePinActivity.class);
                startActivity(intentpin);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}