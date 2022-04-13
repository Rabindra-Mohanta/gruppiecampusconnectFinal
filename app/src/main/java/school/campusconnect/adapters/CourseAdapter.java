package school.campusconnect.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import school.campusconnect.R;
import school.campusconnect.activities.EditCourseActivity;
import school.campusconnect.activities.VendorReadMoreActivity;
import school.campusconnect.datamodel.CoursePostResponse;
import school.campusconnect.datamodel.VendorPostResponse;
import school.campusconnect.utils.AmazoneDownload;
import school.campusconnect.utils.Constants;
import school.campusconnect.utils.MixOperations;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private final ArrayList<CoursePostResponse.CoursePostData> listData;
    private final String role;
    private Context mContext;
    CourseListener listener;
    public CourseAdapter(ArrayList<CoursePostResponse.CoursePostData> listData, CourseListener listener, String role) {
        this.listData=listData;
        this.listener=listener;
        this.role=role;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.course_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CoursePostResponse.CoursePostData item = listData.get(position);
        holder.txt_title.setText(item.courseName);

        if(!TextUtils.isEmpty(item.description))
        {
            holder.txt_content.setVisibility(View.VISIBLE);
            if(item.description.length()>100)
            {
                StringBuilder stringBuilder=new StringBuilder(item.description);
                stringBuilder.setCharAt(97,'.');
                stringBuilder.setCharAt(98,'.');
                stringBuilder.setCharAt(99,'.');
                holder.txt_content.setText(stringBuilder);
            }
            else
            {
                holder.txt_content.setText(item.description);
            }
        }
        else
        {
            holder.txt_content.setVisibility(View.GONE);
        }
        if ("admin".equalsIgnoreCase(role))
        {
            holder.txt_drop_delete.setVisibility(View.VISIBLE);
            holder.iv_delete.setVisibility(View.VISIBLE);
        }
        else
         {
            holder.txt_drop_delete.setVisibility(View.GONE);
            holder.iv_delete.setVisibility(View.GONE);
        }

        holder.rlContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPostClick(listData.get(position));
            }
        });


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_delete)
        ImageView iv_delete;

        @Bind(R.id.txt_title)
        TextView txt_title;

        @Bind(R.id.txt_content)
        TextView txt_content;

        @Bind(R.id.txt_drop_delete)
        TextView txt_drop_delete;

        @Bind(R.id.lin_drop)
        LinearLayout lin_drop;

        @Bind(R.id.rl_container)
        RelativeLayout rlContainer;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @OnClick({R.id.iv_delete,R.id.txt_drop_delete,R.id.rl_container})
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.rl_container:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);

                    listener.onPostClick(listData.get(getAdapterPosition()));

                    break;

                case R.id.iv_delete:
                    if (lin_drop.getVisibility() == View.VISIBLE)
                        lin_drop.setVisibility(View.GONE);
                    else
                        lin_drop.setVisibility(View.VISIBLE);
                    break;

                case R.id.txt_drop_delete:
                    lin_drop.setVisibility(View.GONE);
                    listener.onDeleteClick(listData.get(getAdapterPosition()));
                    break;
            }

        }
    }

    public interface CourseListener
    {
        public void onPostClick(CoursePostResponse.CoursePostData galleryData);
        public void onDeleteClick(CoursePostResponse.CoursePostData galleryData);
    }
}
