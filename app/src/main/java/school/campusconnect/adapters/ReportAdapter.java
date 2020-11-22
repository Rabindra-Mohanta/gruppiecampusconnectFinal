package school.campusconnect.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.recyclerview.widget.RecyclerView;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.datamodel.reportlist.ReportItemList;
import school.campusconnect.views.SMBDialogUtils;

/**
 * Created by frenzin04 on 2/1/2017.
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ImageViewHolder> {

    private List<ReportItemList> list = Collections.emptyList();
    //    private ReportAdapter.OnItemClickListener listener;
    private Context mContext;
    String type;
    ReportItemList item;
    int mGroupId;
    public static int selected_position = -1;

    public ReportAdapter(List<ReportItemList> list) {
        if (list == null) return;
        this.list = list;
    }

    public ReportAdapter() {

    }

    public void addItems(List<ReportItemList> items) {
        list.addAll(new ArrayList<ReportItemList>(items));
       AppLog.e("GeneralPostScroll ", "count " + list.size());
        this.notifyDataSetChanged();
    }

    @Override
    public ReportAdapter.ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view;
//        if (type.equals("group"))
//            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_posts, parent, false);
//        else
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_list, parent, false);
        //videoView = (VideoView) view.findViewById(R.id.videoview);
        return new ReportAdapter.ImageViewHolder(view);
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ReportAdapter.ImageViewHolder holder, int position) {

        if (position == selected_position) {
            holder.radio.setChecked(true);
           AppLog.e("RADIO", "selected position " + position);
        } else {
            holder.radio.setChecked(false);
           AppLog.e("RADIO", "non selected position " + position);
        }

        holder.radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AppLog.e("RADIO", "selectRadio called " + holder.getAdapterPosition());
                holder.radio.setChecked(true);
                selectRadio(holder.getAdapterPosition());
            }
        });

        /*holder.radio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                   AppLog.e("RADIO", "selectRadio called");
                    selectRadio(position);
                }

            }
        });*/

        holder.radio.setText(list.get(position).getReasons());

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(hasStableIds);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.radio)
        RadioButton radio;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        /*@OnClick({R.id.txt_like, R.id.txt_fav, R.id.rel, R.id.txt_readmore, R.id.iv_edit, R.id.iv_delete, R.id.txt_comments, R.id.txt_drop_delete, R.id.txt_drop_report})
        public void OnLikeClick(View v) {
            item = list.get(getLayoutPosition());
            switch (v.getId()) {
                case R.id.txt_like:
                    if (isConnectionAvailable()) {
                        listener.onLikeClick(item, getLayoutPosition());
                    } else {
                        showNoNetworkMsg();
                    }
                    break;
                case R.id.txt_fav:
                    if (isConnectionAvailable()) {
                        listener.onFavClick(item, getLayoutPosition());
                    } else {
                        showNoNetworkMsg();
                    }
                    break;
                case R.id.rel:
                    if (isConnectionAvailable()) {
                        listener.onPostClick(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;
                case R.id.txt_readmore:
                    //listener.onReadMoreClick(item);
                    Intent intent = new Intent(mContext, ReadMoreActivity.class);
                    Gson gson = new Gson();
                    String data = gson.toJson(item);
                    intent.putExtra("data", data);
                    intent.putExtra("type", type);
                    intent.putExtra("from", "post");
                    mContext.startActivity(intent);
                    break;

                case R.id.iv_delete:
                    *//*if (isConnectionAvailable()) {
                        listener.onDeleteClick(item);
                    } else {
                        showNoNetworkMsg();
                    }*//*
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else
                        lin_drop.setVisibility(View.VISIBLE);
                    break;

                case R.id.iv_edit:
                    if (isConnectionAvailable()) {
                        listener.onEditClick(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;

                case R.id.txt_comments:
                   AppLog.e("COMMETS_", "type is " + type);
                    if (type.equals("group") || type.equals("favourite")) {
                        if (isConnectionAvailable()) {
                            Intent intent1 = new Intent(mContext, CommentsActivity.class);
                            intent1.putExtra("id", mGroupId);
                            intent1.putExtra("post_id", list.get(getAdapterPosition()).id);
                            intent1.putExtra("type", "group");
                            mContext.startActivity(intent1);
                        } else {
                            showNoNetworkMsg();
                        }
                    }
                    break;

                case R.id.txt_drop_delete:
                    lin_drop.setVisibility(View.GONE);
                    if (isConnectionAvailable()) {
                        listener.onDeleteClick(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;

                case R.id.txt_drop_report:
                    lin_drop.setVisibility(View.GONE);
                    if (isConnectionAvailable()) {
                        listener.onReportClick(item);
                    } else {
                        showNoNetworkMsg();
                    }
                    break;

            }
        }*/
    }

    public List<ReportItemList> getList() {
        return list;
    }

   /* public interface OnItemClickListener {
        void onFavClick(ReportItemList item, int pos);

        void onLikeClick(ReportItemList item, int pos);

        void onPostClick(ReportItemList item);

        void onReadMoreClick(ReportItemList item);

        void onEditClick(ReportItemList item);

        void onDeleteClick(ReportItemList item);

        void onReportClick(ReportItemList item);
    }*/

    public boolean isConnectionAvailable() {
        ConnectivityManager connectivityManager;
        NetworkInfo networkinfo;
        connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkinfo = connectivityManager.getActiveNetworkInfo();
        return (networkinfo != null && networkinfo.isConnected());

    }

    public void showNoNetworkMsg() {
        SMBDialogUtils.showSMBDialogOK((Activity) mContext, mContext.getString(R.string.no_internet), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //onBackPressed();
            }
        });
    }

    private void selectRadio(int position) {

        selected_position = position;

       AppLog.e("RADIO", "selected position is " + selected_position);

        for (int i = 0; i < list.size(); i++) {
            if (i == selected_position) {
                list.get(position).setSelected(true);
               AppLog.e("RADIO", "i(selected) is " + i);
            } else {
                list.get(position).setSelected(false);
               AppLog.e("RADIO", "i(non selected) is " + i);
            }
        }

        notifyDataSetChanged();
    }

}
