package school.campusconnect.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.databinding.ActivityAddClassV2Binding;
import school.campusconnect.databinding.ItemClassDataV2Binding;
import school.campusconnect.databinding.ItemClassesV2Binding;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.classs.AddClassReq;
import school.campusconnect.datamodel.classs.ClassResV2;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppDialog;

public class AddClassV2Activity extends BaseActivity implements LeafManager.OnCommunicationListener {


    public static final String TAG = "AddClassV2Activity";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;

    LeafManager manager;

    ClassAdapater adapater;
    ActivityAddClassV2Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding = DataBindingUtil.setContentView(this,R.layout.activity_add_class_v2);

        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.lbl_add_class));

        inits();

    }

    private void inits() {

        manager = new LeafManager();
        adapater = new ClassAdapater();
        binding.rvClass.setAdapter(adapater);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showLoadingBar(binding.progressBar,false);
        manager.getClassListV2(this,GroupDashboardActivityNew.groupId);
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        hideLoadingBar();

        if (apiId == LeafManager.API_GET_CLASS_LIST_V2)
        {
            ClassResV2 resV2 = (ClassResV2) response;
            Log.e(TAG,new Gson().toJson(resV2));

            adapater.add(resV2.getData().get(0).getClassData());
        }

        if (apiId == LeafManager.API_ADD_CLASS_V2)
        {
           Toast.makeText(getApplicationContext(),getResources().getString(R.string.toast_success),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(int apiId, String msg) {
        hideLoadingBar();
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onException(int apiId, String msg) {
        hideLoadingBar();
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    public class ClassAdapater extends RecyclerView.Adapter<ClassAdapater.ViewHolder>{

        ArrayList<ClassResV2.Data> data = new ArrayList<>();
        @NonNull
        @Override
        public ClassAdapater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemClassesV2Binding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.item_classes_v2,parent,false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ClassAdapater.ViewHolder holder, int position) {

            ClassResV2.Data classData = data.get(position);

            holder.binding.cbType.setText(classData.getType());

            holder.binding.cbType.setChecked(true);
            holder.binding.llBottomData.setVisibility(View.VISIBLE);

            holder.binding.cbType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    if (b)
                    {
                        holder.binding.llBottomData.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        holder.binding.llBottomData.setVisibility(View.GONE);
                    }
                }
            });

            holder.binding.rcvClass.setAdapter(new ClassDataAdapater(classData.getClassData(),classData.getClassTypeId()));
        }

        @Override
        public int getItemCount() {
            return data != null ? data.size() : 0;
        }

        public void add(ArrayList<ClassResV2.Data> data) {
            this.data = data;
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ItemClassesV2Binding binding;
            public ViewHolder(@NonNull ItemClassesV2Binding itemView) {
                super(itemView.getRoot());
               binding = itemView;
            }
        }
    }

    public class ClassDataAdapater extends RecyclerView.Adapter<ClassDataAdapater.ViewHolder>{

        ArrayList<ClassResV2.classData> classData;
        String classId;
        Context context;
        public ClassDataAdapater(ArrayList<ClassResV2.classData> classData,String classId) {
            this.classData = classData;
            this.classId = classId;
        }

        @NonNull
        @Override
        public ClassDataAdapater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            context = parent.getContext();
            ItemClassDataV2Binding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.item_class_data_v2,parent,false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ClassDataAdapater.ViewHolder holder, int position) {
            ClassResV2.classData data = classData.get(position);

            holder.binding.tvClassName.setText(data.getClassName());
            holder.binding.etSections.setText(String.valueOf(data.getNoOfSections()));

            holder.binding.imgMax1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AppDialog.showConfirmDialog(context, getResources().getString(R.string.dialog_are_you_sure_you_want_to_add_class), new AppDialog.AppDialogListener() {
                        @Override
                        public void okPositiveClick(DialogInterface dialog) {

                            int i = Integer.parseInt(holder.binding.etSections.getText().toString());
                            i++;
                            holder.binding.etSections.setText(String.valueOf(i));
                            onTreeClick(data.getClassName(),classId);
                        }

                        @Override
                        public void okCancelClick(DialogInterface dialog) {

                        }
                    });

                }
            });
        }

        @Override
        public int getItemCount() {
            return classData != null ? classData.size() : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ItemClassDataV2Binding binding;
            public ViewHolder(@NonNull ItemClassDataV2Binding itemView) {
                super(itemView.getRoot());

                binding = itemView;
            }
        }
    }

    private void onTreeClick(String className, String classId) {

        Log.e(TAG," className "+className +" ClassID "+classId);
       /* LeafManager leafManager = new LeafManager();
        showLoadingBar(binding.progressBar,true);*/

        AddClassReq req = new AddClassReq();
        req.setClassName(className);
        req.setNoOfSection(1);
        req.setClassTypeId(classId);


        Log.e(TAG," REQ"+new Gson().toJson(req));

        showLoadingBar(binding.progressBar,true);
        manager.addClassListV2(this,GroupDashboardActivityNew.groupId,req);

    }

}