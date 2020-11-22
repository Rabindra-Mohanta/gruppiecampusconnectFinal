package school.campusconnect.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.Holder> {

    private Context mcontext;
    private String[] list;

    public GroupListAdapter(Context context, String[] list) {
        this.mcontext = context;
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        mcontext = parent.getContext();
        View view = LayoutInflater.from(mcontext).inflate(R.layout.list_group_items, parent, false);
        return new Holder(view);
    }


    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        holder.tvName.setText(list[position]);
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public class Holder extends RecyclerView.ViewHolder {

        @Bind(R.id.txt_name)
        TextView tvName;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }
    }

}


