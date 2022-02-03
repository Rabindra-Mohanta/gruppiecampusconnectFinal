package school.campusconnect.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.activities.AddBoothActivity;
import school.campusconnect.activities.BoothStudentActivity;
import school.campusconnect.activities.ClassStudentActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.adapters.TeamListAdapterNew;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.booths.BoothResponse;
import school.campusconnect.datamodel.classs.ClassResponse;
import school.campusconnect.datamodel.staff.StaffResponse;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class BoothListFragment extends BaseFragment implements LeafManager.OnCommunicationListener {
    private static final String TAG = "TeamDiscussFragment";
    @Bind(R.id.rvTeams)
    public RecyclerView rvClass;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;
    @Bind(R.id.etSearch)
    public EditText etSearch;
    private ArrayList<MyTeamData> result;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss_search,container,false);
        ButterKnife.bind(this,view);

        init();

        rvClass.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        return view;
    }

    private void init() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(result!=null){
                    if(!TextUtils.isEmpty(s.toString())){
                        ArrayList<MyTeamData> newList = new ArrayList<>();
                        for (int i=0;i<result.size();i++){
                            if(result.get(i).name.toLowerCase().contains(s.toString().toLowerCase())){
                                newList.add(result.get(i));
                            }
                        }
                        rvClass.setAdapter(new ClassesAdapter(newList));
                    }else {
                        rvClass.setAdapter(new ClassesAdapter(result));
                    }
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.menu_add_booth).setVisible(true);
        menu.findItem(R.id.menu_search).setVisible(true);
        menu.findItem(R.id.action_notification_list).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_add_booth:
                startActivity(new Intent(getContext(), AddBoothActivity.class));
                return true;
            case R.id.menu_search:
                showHideSearch();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((GroupDashboardActivityNew) getActivity()).tvToolbar.setText(GroupDashboardActivityNew.group_name);
            ((GroupDashboardActivityNew) getActivity()).tv_Desc.setVisibility(View.GONE);
        }
        etSearch.setText("");
        LeafManager leafManager = new LeafManager();
        progressBar.setVisibility(View.VISIBLE);
        leafManager.getBooths(this,GroupDashboardActivityNew.groupId,"");
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        progressBar.setVisibility(View.GONE);
        BoothResponse res = (BoothResponse) response;
        result = res.getData();
        AppLog.e(TAG, "ClassResponse " + result);

        rvClass.setAdapter(new ClassesAdapter(result));
    }

    @Override
    public void onFailure(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {
        progressBar.setVisibility(View.GONE);
    }



    public void showHideSearch() {
        if (etSearch.getVisibility() == View.VISIBLE) {
            etSearch.setVisibility(View.GONE);
        } else {
            etSearch.setVisibility(View.VISIBLE);
        }
    }

    public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder>
    {
        List<MyTeamData> list;
        private Context mContext;

        public ClassesAdapter(List<MyTeamData> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_team_list,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
            final MyTeamData team = list.get(position);

            holder.tvTeamName.setText(team.name);
            holder.tvPostCount.setText("");
            holder.tvPostCount.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(team.image)) {
                holder.imgTeam.setVisibility(View.VISIBLE);
                holder.imgDefault.setVisibility(View.GONE);

                Picasso.with(mContext).load(Constants.decodeUrlToBase64(team.image)).networkPolicy(NetworkPolicy.OFFLINE)/*.resize(150,150)*/.into(holder.imgTeam, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(mContext).load(Constants.decodeUrlToBase64(team.image))/*.resize(150,150)*/.into(holder.imgTeam, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                holder.imgTeam.setVisibility(View.GONE);
                                holder.imgDefault.setVisibility(View.VISIBLE);

                                TextDrawable drawable = TextDrawable.builder()
                                        .buildRoundRect(ImageUtil.getTextLetter(team.name), ImageUtil.getRandomColor(position),30);
                                holder.imgDefault.setImageDrawable(drawable);
                            }
                        });
                    }
                });


            } else {
                holder.imgTeam.setVisibility(View.GONE);
                holder.imgDefault.setVisibility(View.VISIBLE);

                TextDrawable drawable = TextDrawable.builder()
                        .buildRoundRect(ImageUtil.getTextLetter(team.name), ImageUtil.getRandomColor(position),30);
                holder.imgDefault.setImageDrawable(drawable);
            }
            holder.imgTeamAdd.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText("No Booths found.");
                }
                else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText("No Booths found.");
                return 0;
            }

        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgTeamAdd;
            TextView tvTeamName, tvBlur, tvPostCount;
            ImageView imgTeam,imgDefault;


            public ViewHolder(View itemView) {
                super(itemView);

                imgTeam = itemView.findViewById(R.id.imgTeam);
                imgTeamAdd = itemView.findViewById(R.id.imgTeamAdd);
                imgDefault = itemView.findViewById(R.id.imgDefault);
                tvTeamName = itemView.findViewById(R.id.tvTeamName);
                tvBlur = itemView.findViewById(R.id.tvBlur);
                tvPostCount = itemView.findViewById(R.id.tvPostCount);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }

                    //}
                });

            }
        }
    }

    private void onTreeClick(MyTeamData classData) {

        ((GroupDashboardActivityNew) getActivity()).onTeamSelected(classData);
//
//        Intent intent = new Intent(getActivity(), BoothStudentActivity.class);
//        intent.putExtra("class_data",new Gson().toJson(classData));
//        intent.putExtra("title",classData.boothName);
//        startActivity(intent);
    }
}
