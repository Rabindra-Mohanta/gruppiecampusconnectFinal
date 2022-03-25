package school.campusconnect.fragments;

import android.content.Context;
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
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.GroupItem;
import school.campusconnect.datamodel.teamdiscussion.MyTeamData;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.BaseFragment;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;

public class BoothPresidentListMyTeamFragment extends BaseFragment implements LeafManager.OnCommunicationListener {

    public static String TAG = "BoothPresidentListMyTeamFragment";

    private List<MyTeamData> filteredList = new ArrayList<>();
    private List<MyTeamData> myTeamDataList = new ArrayList<>();

    ClassesAdapter adapter;

    @Bind(R.id.rvTeams)
    public RecyclerView rvTeams;

    @Bind(R.id.edtSearch)
    public EditText edtSearch;

    @Bind(R.id.txtEmpty)
    public TextView txtEmpty;

    @Bind(R.id.progressBar)
    public ProgressBar progressBar;

    private GroupItem mGroupItem;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_team_discuss,container,false);
        ButterKnife.bind(this,view);
        Log.e(TAG,"onViewCreated");
        inits();

        getDataLocally();

        return view;
    }

    private void getDataLocally() {

        if (mGroupItem.boothCount > 1) {

        } else {

        }


    }

    private void inits() {


        rvTeams.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ClassesAdapter();
        rvTeams.setAdapter(adapter);

        mGroupItem = new Gson().fromJson(LeafPreference.getInstance(getContext()).getString(Constants.GROUP_DATA), GroupItem.class);

        edtSearch.setVisibility(View.VISIBLE);

        edtSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {}

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                if (s.toString().length() > 2)
                {
                    searchData(s.toString());
                }
                else
                {
                    adapter.add(myTeamDataList);
                }
            }
        });
    }

    private void searchData(String text) {

        filteredList = new ArrayList<>();

        for(MyTeamData item : myTeamDataList){

            if (item.name.toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }

        adapter.add(filteredList);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

    }

    @Override
    public void onFailure(int apiId, String msg) {

    }

    @Override
    public void onException(int apiId, String msg) {

    }

    public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ViewHolder>
    {
        List<MyTeamData> list;
        private Context mContext;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_student,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final MyTeamData item = list.get(position);

            if (!TextUtils.isEmpty(item.image)) {
                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).resize(50,50).networkPolicy(NetworkPolicy.OFFLINE).into(holder.imgTeam,
                        new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.img_lead_default.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onError() {
                                Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).resize(50,50).into(holder.imgTeam, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        holder.img_lead_default.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onError() {
                                        holder.img_lead_default.setVisibility(View.VISIBLE);
                                        TextDrawable drawable = TextDrawable.builder()
                                                .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position));
                                        holder.img_lead_default.setImageDrawable(drawable);
                                        AppLog.e("Picasso", "Error : ");
                                    }
                                });
                            }
                        });
            } else {
                holder.img_lead_default.setVisibility(View.VISIBLE);
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position));
                holder.img_lead_default.setImageDrawable(drawable);
            }

            holder.txt_name.setText(item.name);
            holder.txt_count.setText("Member : "+String.valueOf(item.members));
        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {
                    txtEmpty.setText("No Booths Teams found.");
                }
                else {
                    txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                txtEmpty.setText("No Booths Teams found.");
                return 0;
            }

        }

        public void add(List<MyTeamData> list) {
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

            @Bind(R.id.txt_count)
            TextView txt_count;

            @Bind(R.id.img_tree)
            ImageView img_tree;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this,itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });

                img_tree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onTreeClick(list.get(getAdapterPosition()));
                    }
                });

            }
        }
    }

    private void onTreeClick(MyTeamData classData) {

        ((GroupDashboardActivityNew) getActivity()).onBoothTeams(classData.name,classData.teamId,"myTeam",true);
    }
}
