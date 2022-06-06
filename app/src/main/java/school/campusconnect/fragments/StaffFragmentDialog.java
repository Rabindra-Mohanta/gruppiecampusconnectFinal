package school.campusconnect.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;
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
import school.campusconnect.activities.AddStaffActivity;
import school.campusconnect.activities.BoothStudentActivity;
import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.activities.StaffClassListActivity;
import school.campusconnect.databinding.DialogStaffListBinding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.issue.IssueListResponse;
import school.campusconnect.datamodel.staff.StaffResponse;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SearchIssueFragmentDialog;

public class StaffFragmentDialog extends DialogFragment implements LeafManager.OnCommunicationListener{
    DialogStaffListBinding binding;
    Context context;
    String role,Type;
    private ArrayList<StaffResponse.StaffData> result;

    public static StaffFragmentDialog newInstance()
    {
        return new StaffFragmentDialog();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //setStyle(DialogFragment.STYLE_NORMAL, R.style.dialog_theme);
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_staff_list, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d!=null){
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            d.getWindow().setLayout(width, height);
        }

        LeafManager leafManager = new LeafManager();
        leafManager.getStaff(this, GroupDashboardActivityNew.groupId);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        inits();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }


    private void inits() {

        //  binding.btnSave.setOnClickListener(this);
        binding.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {

        //progressBar.setVisibility(View.GONE);
        StaffResponse res = (StaffResponse) response;
        result = res.getData();


        binding.rvStaff.setAdapter(new StaffAdapter(result));
    }

    @Override
    public void onFailure(int apiId, String msg) {

        //progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onException(int apiId, String msg) {

        //progressBar.setVisibility(View.GONE);
    }

    public void setData(String type, String role) {
        this.role = role;
        this.Type = type;
    }

    public class StaffAdapter extends RecyclerView.Adapter<StaffAdapter.ViewHolder>
    {
        List<StaffResponse.StaffData> list;
        private Context mContext;

        public StaffAdapter(List<StaffResponse.StaffData> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            final StaffResponse.StaffData item = list.get(position);

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
            if(!TextUtils.isEmpty(item.designation)){
                holder.txt_count.setText("[" + item.getDesignation() + "]");
                holder.txt_count.setVisibility(View.VISIBLE);
            }else {
                holder.txt_count.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            if(list!=null)
            {
                if(list.size()==0)
                {

                    binding.txtEmpty.setText(getResources().getString(R.string.txt_no_staff_found));
                }
                else {
                    binding.txtEmpty.setText("");
                }

                return list.size();
            }
            else
            {
                binding.txtEmpty.setText(getResources().getString(R.string.txt_no_staff_found));
                return 0;
            }

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

    private void onTreeClick(StaffResponse.StaffData classData) {
        dismiss();
        Intent intent = new Intent(getActivity(), StaffClassListActivity.class);
        intent.putExtra("staff_id",classData.getStaffId());
        intent.putExtra("title",classData.getName());
        intent.putExtra("role",role);
        intent.putExtra("type",Type);
        startActivity(intent);
    }
}
