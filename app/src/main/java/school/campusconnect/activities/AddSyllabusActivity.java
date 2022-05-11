package school.campusconnect.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.databinding.ActivityAddSyllabusBinding;
import school.campusconnect.datamodel.subjects.SubjectStaffResponse;
import school.campusconnect.datamodel.syllabus.TopicModelModel;
import school.campusconnect.fragments.HWSubjectListFragment;

public class AddSyllabusActivity extends BaseActivity implements View.OnClickListener {
ActivityAddSyllabusBinding binding;
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    TopicAdapter topicAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_add_syllabus);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_add_syllabus));

        inits();
        setListner();


    }

    private void inits() {
        topicAdapter = new TopicAdapter();
        binding.rvTopic.setAdapter(topicAdapter);
        topicAdapter.add(" ");
    }
    private void setListner()
    {
        binding.imgAdd.setOnClickListener(this);
        binding.btnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.imgAdd:
                addTopic();
                break;

            case R.id.btnAdd:

                break;
        }

    }

    private void addTopic() {
        topicAdapter.add(" ");
    }

    public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {
        ArrayList<String> list = new ArrayList<>();
        private Context mContext;

        public ArrayList<String> getList() {
            return this.list;
        }
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_topic, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            String item = list.get(position);
            holder.etName.setText(item);

            if (position == 0)
            {
                holder.imgDelete.setVisibility(View.GONE);
            }
            else
            {
                holder.imgDelete.setVisibility(View.VISIBLE);
            }

            holder.etName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    list.set(position, s.toString());
                }
            });

        }

        @Override
        public int getItemCount() {
            return list.size();

        }

        public void add(String item) {
            list.add(item);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.etTopicName)
            EditText etName;

            @Bind(R.id.imgDelete)
            ImageView imgDelete;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);

                imgDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list.remove(getAdapterPosition());
                        notifyDataSetChanged();
                    }
                });

            }
        }
    }
}