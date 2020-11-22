package school.campusconnect.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.recyclerview.widget.RecyclerView;
import school.campusconnect.utils.AppLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.database.DatabaseHandler;
import school.campusconnect.datamodel.authorizeduser.AuthorizedUserModel;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.ImageUtil;
import school.campusconnect.views.SMBDialogUtils;

/**
 * Created by frenzin04 on 1/12/2017.
 */
public class AuthorizedUserAdapter extends RecyclerView.Adapter<AuthorizedUserAdapter.ImageViewHolder> {

    private List<AuthorizedUserModel> list = Collections.emptyList();
    private Context mContext;
    String type;
    AuthorizedUserModel item;
    int mGroupId;
    int count;
    UnAuthorizeUser unAuthorizeUser;

    public AuthorizedUserAdapter(ArrayList<AuthorizedUserModel> list, UnAuthorizeUser unAuthorizeUser, int count) {
        if (list == null) return;
        this.list = list;
        this.count = count;
        this.unAuthorizeUser = unAuthorizeUser;
    }

    public void addItems(List<AuthorizedUserModel> items) {
        list.addAll(new ArrayList<AuthorizedUserModel>(items));
       AppLog.e("items ", "count " + list.size());
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_authorized_user, parent, false);
        //videoView = (VideoView) view.findViewById(R.id.videoview);
        return new ImageViewHolder(view);
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, final int position) {
        final AuthorizedUserModel item = list.get(position);
       AppLog.e("asf", new Gson().toJson(item));
       AppLog.e("Authorized", "Group Id : " + mGroupId);
        DatabaseHandler databaseHandler = new DatabaseHandler(mContext);

        if (count != 0) {
            try {
//               AppLog.e("CONTACTSS", "count is " + databaseHandler.getCount());
                String name = databaseHandler.getNameFromNum(item.phone.replaceAll(" ", ""));
//               AppLog.e("CONTACTSS", "api name is " + item.name);
//               AppLog.e("CONTACTSS", "name is " + name);
//               AppLog.e("CONTACTSS", "num is " + item.phone);
                if (!name.equals("")) {
                    holder.txtName.setText(name);
                } else {
                    holder.txtName.setText(item.name);
                }
            } catch (NullPointerException e) {
                holder.txtName.setText(item.name);
            }
        } else {
           AppLog.e("CONTACTSS", "count is 0");
            holder.txtName.setText(item.name);
        }

//        holder.txtName.setText(item.name);

       AppLog.e("ASDF", "image is " + item.image);
        if (item.image != null && !item.image.equals(""))
        {
            holder.img_lead_default.setVisibility(View.INVISIBLE);
            Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).networkPolicy(NetworkPolicy.OFFLINE).resize(dpToPx(), dpToPx()).into(holder.img_lead, new Callback() {
                @Override
                public void onSuccess() {
                    holder.img_lead_default.setVisibility(View.INVISIBLE);
                   AppLog.e("Picasso", "onSuccess : " + position);
                }

                @Override
                public void onError() {
                   AppLog.e("Picasso", "onError : " + position);
                    holder.img_lead_default.setVisibility(View.VISIBLE);
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRound(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position));
                    holder.img_lead_default.setImageDrawable(drawable);

                /*    Picasso.with(mContext).load(R.drawable.icon_default_user).into(holder.img_lead, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {

                           AppLog.e("Picasso", "Error : ");
                        }
                    });*/
                }
            });
    }
        else
        {
            holder.img_lead_default.setVisibility(View.VISIBLE);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRoundRect(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position), mContext.getResources().getDimensionPixelSize(R.dimen.padding_25dp));
            holder.img_lead_default.setImageDrawable(drawable);
        }


        holder.img_lead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog settingsDialog = new Dialog(mContext);
                LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
                View newView = (View) inflater.inflate(R.layout.img_layout, null);
                settingsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                settingsDialog.setContentView(newView);

                settingsDialog.show();
                ImageView iv = (ImageView) newView.findViewById(R.id.img_popup);

                if (item.image != null && !item.image.isEmpty()) {
                    Picasso.with(mContext).load(Constants.decodeUrlToBase64(item.image)).into(iv);
                } else {
                  //  holder.img_lead_default.setVisibility(View.VISIBLE);
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRect(ImageUtil.getTextLetter(item.name), ImageUtil.getRandomColor(position) );
                    iv.setImageDrawable(drawable);
                   // Picasso.with(mContext).load(R.drawable.icon_default_user).into(iv);
                }
                settingsDialog.show();
            }
        });

        holder.switchAllowPost.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    if (isConnectionAvailable())
                        unAuthorizeUser.unAuthorize(item);
                    else {
                        showNoNetworkMsg();
                        holder.switchAllowPost.setChecked(true);
                    }
                }
            }
        });

    }
    private int dpToPx() {
        return mContext.getResources().getDimensionPixelSize(R.dimen.group_list_image_size);
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

        @Bind(R.id.txt_name)
        TextView txtName;

        @Bind(R.id.img_lead)
        ImageView img_lead;

        @Bind(R.id.img_lead_default)
        ImageView img_lead_default;

        @Bind(R.id.switchAllowPost)
        Switch switchAllowPost;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

    }

    public List<AuthorizedUserModel> getList() {
        return list;
    }

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

    public interface UnAuthorizeUser {

        void unAuthorize(AuthorizedUserModel item);

    }

}
