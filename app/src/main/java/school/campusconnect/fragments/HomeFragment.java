package school.campusconnect.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.baoyz.widget.PullRefreshLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupDetailResponse;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.datamodel.GroupResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppDialog;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class HomeFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "HomeFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    @Bind(R.id.etSearch)
    public EditText etSearch;

    /*
        @Bind(R.id.swipeRefreshLayout)
        public PullRefreshLayout swipeRefreshLayout;
    */
    private String from = "";
    private String category = "";
    private String categoryName = "";
    private String talukName;
    private List<GroupItem> result;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups_new, container, false);
        ButterKnife.bind(this, view);
        rvClass.setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false));

        _init();

        return view;
    }

    public void showHideSearch() {
        if (etSearch.getVisibility() == View.VISIBLE) {
            etSearch.setVisibility(View.GONE);
        } else {
            etSearch.setVisibility(View.VISIBLE);
        }
    }

    private void _init() {
        if (getArguments() != null) {
            from = getArguments().getString("from");
            if ("TALUK".equalsIgnoreCase(from)) {
                talukName = getArguments().getString("talukName");
            } else if ("CONSTITUENCY".equalsIgnoreCase(from)) {
                category = getArguments().getString("category");
                categoryName = getArguments().getString("categoryName");
            }
        }

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (result != null) {
                    if (!TextUtils.isEmpty(s.toString())) {
                        ArrayList<GroupItem> list = new ArrayList<>();
                        for (int i = 0; i < result.size(); i++) {
                            if (result.get(i).name.toLowerCase().contains(s.toString().toLowerCase())) {
                                list.add(result.get(i));
                            }
                        }
                        rvClass.setAdapter(new GroupAdapterNew(list));
                    } else {
                        rvClass.setAdapter(new GroupAdapterNew(result));
                    }
                }
            }
        });
    }

    private void getGroupList() {
        showLoadingBar(progressBar);
      //  progressBar.setVisibility(View.VISIBLE);
        LeafManager leafManager = new LeafManager();
        leafManager.getGroups(this, from, talukName, category, categoryName);
    }

    @Override
    public void onStart() {
        super.onStart();
        etSearch.setText("");
        if (isConnectionAvailable()) {
            if (LeafPreference.getInstance(getActivity()).getBoolean("group_list_refresh")) {
                LeafPreference.getInstance(getActivity()).setBoolean("group_list_refresh", false);
                getGroupList();
            } else {
                String groupList;
                if (!TextUtils.isEmpty(talukName)) {
                    groupList = LeafPreference.getInstance(getActivity()).getString(talukName);
                } else if (!TextUtils.isEmpty(categoryName)) {
                    groupList = LeafPreference.getInstance(getActivity()).getString(categoryName);
                } else {
                    groupList = LeafPreference.getInstance(getActivity()).getString(LeafPreference.SCHOOL_LIST);
                }

                if (groupList != null && !TextUtils.isEmpty(groupList)) {
                    result = new Gson().fromJson(groupList, new TypeToken<List<GroupItem>>() {
                    }.getType());
                    rvClass.setAdapter(new GroupAdapterNew(result));
                } else {
                    getGroupList();
                }
            }
        } else {
            showNoNetworkMsg();
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        hideLoadingBar();
        //progressBar.setVisibility(View.GONE);
        if (apiId == LeafManager.API_ID_GROUP_DETAIL) {
            AppLog.e("UserExist->", "getGroupDetail api response");

            GroupDetailResponse gRes = (GroupDetailResponse) response;
            AppLog.e(TAG, "group detail ->" + new Gson().toJson(gRes));
            LeafPreference.getInstance(getActivity()).setInt(Constants.TOTAL_MEMBER, gRes.data.get(0).totalUsers);
            LeafPreference.getInstance(getActivity()).setString(Constants.TYPE,categoryName);


            //save group detail
      //      LeafPreference.getInstance(getActivity()).setString(Constants.TYPE,  gRes.data.get(0).type);

            LeafPreference.getInstance(getActivity()).setInt(Constants.TOTAL_MEMBER, gRes.data.get(0).totalUsers);
            Intent login = new Intent(getActivity(), GroupDashboardActivityNew.class);
            startActivity(login);
        } else {
            GroupResponse res = (GroupResponse) response;
            AppLog.e(TAG, "ClassResponse " + res.data);
            result = res.data;
            if (!TextUtils.isEmpty(talukName)) {
                LeafPreference.getInstance(getActivity()).setString(talukName, new Gson().toJson(res.data));
            } else if (!TextUtils.isEmpty(categoryName)) {
                LeafPreference.getInstance(getActivity()).setString(categoryName, new Gson().toJson(res.data));
            } else {
                LeafPreference.getInstance(getActivity()).setString(LeafPreference.SCHOOL_LIST, new Gson().toJson(res.data));
            }


            rvClass.setAdapter(new GroupAdapterNew(res.data));

            for (int i = 0; i < res.data.size(); i++) {
                FirebaseMessaging.getInstance().subscribeToTopic(res.data.get(i).getGroupId())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    AppLog.e(TAG, "subscribeToTopic : Successful()");
                                } else {
                                    AppLog.e(TAG, "subscribeToTopic : Fail()");
                                }

                            }
                        });
            }

            checkVersionUpdate(res.appVersion);

        }
    }

    private void checkVersionUpdate(int appVersion) {
        if (getActivity() != null) {
            AppLog.e(TAG, "appVersion : " + appVersion);
            AppLog.e(TAG, "BuildConfig.VERSION_CODE : " + BuildConfig.VERSION_CODE);
            if (BuildConfig.VERSION_CODE < appVersion) {
                AppDialog.showUpdateDialog(getActivity(), getResources().getString(R.string.dialog_new_version_available), new AppDialog.AppUpdateDialogListener() {
                    @Override
                    public void onUpdateClick(DialogInterface dialog) {
                        final String appPackageName = BuildConfig.APPLICATION_ID; // getPackageName() from Context or Activity object
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }
                    }
                });
            } else {
                AppLog.e(TAG, "checkVersionUpdate : latest");
            }
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
       // progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
      //  progressBar.setVisibility(View.GONE);
    }

    public class GroupAdapterNew extends RecyclerView.Adapter<GroupAdapterNew.ViewHolder> {
        List<GroupItem> list;
        private Context mContext;

        public GroupAdapterNew(List<GroupItem> list) {
            this.list = list;
        }

        @Override
        public GroupAdapterNew.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_group, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final GroupAdapterNew.ViewHolder holder, final int position) {
            final GroupItem item = list.get(position);

            if (!TextUtils.isEmpty(item.getImage())) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(200, 200).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgGroupNew,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.imgGroupNewDefault.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.getImage())).resize(200, 200).into(holder.imgGroupNew, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.imgGroupNewDefault.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.imgGroupNewDefault.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position));
                                        holder.imgGroupNewDefault.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.imgGroupNewDefault.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position));
                holder.imgGroupNewDefault.setImageDrawable(drawable);
            }

            holder.txtName.setText(item.getName());
            if (item.isAdmin) {
                holder.imgAdmin.setVisibility(View.VISIBLE);
            } else {
                holder.imgAdmin.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    txtEmpty.setText("No Group found.");
                } else {
                    txtEmpty.setText("");
                }

                return list.size();
            } else {
                txtEmpty.setText("No Group found.");
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.txt_group_name)
            TextView txtName;

            @Bind(R.id.img_admin_icon)
            ImageView imgAdmin;

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
                        AppLog.e("GIT", "sd is " + list.get(getLayoutPosition()).name);
                        onGroupSelect(list.get(getLayoutPosition()));
                        break;
                }
            }
        }
    }

    private void onGroupSelect(GroupItem groupItem) {

        if ("constituency".equalsIgnoreCase(category)) {
            LeafManager manager = new LeafManager();
            showLoadingBar(progressBar);
         //   progressBar.setVisibility(View.VISIBLE);
            manager.getGroupDetail(this, groupItem.getGroupId());
        } else {
            LeafPreference.getInstance(getActivity()).setInt(Constants.TOTAL_MEMBER, groupItem.totalUsers);
            LeafPreference.getInstance(getActivity()).setString(Constants.GROUP_DATA, new Gson().toJson(groupItem));
            Intent login = new Intent(getActivity(), GroupDashboardActivityNew.class);
            startActivity(login);
        }

    }
}
