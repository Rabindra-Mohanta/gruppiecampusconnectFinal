package school.campusconnect.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.BuildConfig;
import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.datamodel.AddPostResponse;
import school.campusconnect.datamodel.BaseResponse;
import school.campusconnect.datamodel.ErrorResponseModel;
import school.campusconnect.datamodel.LeadItem;
import school.campusconnect.datamodel.LeadResponse;
import school.campusconnect.datamodel.LeaveReq;
import school.campusconnect.network.LeafManager;
import school.campusconnect.utils.AppLog;

public class LeaveActivity extends BaseActivity implements LeafManager.OnAddUpdateListener {
    private static final String TAG = "";
    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    @Bind(R.id.etReason)
    public EditText etReason;
    @Bind(R.id.rvKids)
    public RecyclerView rvKids;


    @Bind(R.id.progressBar)
    ProgressBar progressBar;


    private LeafManager leafManager;
    private String group_id;
    private String team_id;
    private LeaveReq leaveReq;
    private KidsAdapter kidsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);
        init();
    }

    private void init() {
        ButterKnife.bind(this);
        leafManager = new LeafManager();
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(getResources().getString(R.string.leave_request));

        group_id = getIntent().getStringExtra("group_id");
        team_id = getIntent().getStringExtra("team_id");
        AppLog.e(TAG, "group_id : " + group_id);
        AppLog.e(TAG, "team_id : " + team_id);

        showLoadingBar(progressBar,false);
      //  progressBar.setVisibility(View.VISIBLE);
        leafManager.leaveForm(this, group_id, team_id);
    }

    public void submit(View view) {
        if (isValid()) {
            hide_keyboard();
            if (!isConnectionAvailable()) {
                showNoNetworkMsg();
                return;
            }

            leaveReq = new LeaveReq(etReason.getText().toString());
            showLoadingBar(progressBar,false);
           // progressBar.setVisibility(View.VISIBLE);
            leafManager.leaveRequest(this, group_id, team_id, leaveReq,kidsAdapter.getSelectedKids());
        }
    }

    public boolean isValid() {
        boolean valid = true;
        if (!isValueValid(etReason)) {
            valid = false;
        }
        else if (kidsAdapter == null) {
            Toast.makeText(this, getResources().getString(R.string.lbl_select_kids), Toast.LENGTH_SHORT).show();
            valid = false;
        }
        else {
            if (kidsAdapter.getSelectedKidsCount() ==0) {
                Toast.makeText(this, getResources().getString(R.string.lbl_select_kids), Toast.LENGTH_SHORT).show();
                valid = false;
            }
        }
        return valid;
    }

    public class KidsAdapter extends RecyclerView.Adapter<KidsAdapter.ViewHolder> {
        List<LeadItem> list;
        private Context mContext;

        public KidsAdapter(List<LeadItem> list) {
            this.list = list;
        }

        @Override
        public KidsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            mContext = parent.getContext();
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_kids, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final KidsAdapter.ViewHolder holder, final int position) {
            final LeadItem item = list.get(position);
            holder.chkName.setText(item.name);
        }

        @Override
        public int getItemCount() {
            if (list != null) {
                if (list.size() == 0) {
                    Toast.makeText(mContext, getResources().getString(R.string.toast_no_kid_found), Toast.LENGTH_SHORT).show();
                }
                return list.size();
            } else {
                Toast.makeText(mContext, getResources().getString(R.string.toast_no_kid_found), Toast.LENGTH_SHORT).show();
                return 0;
            }

        }

        public int getSelectedKidsCount() {
            if (list == null)
                return 0;

            int cnt = 0;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isSelected) {
                    cnt++;
                }
            }
            return cnt;
        }

        public String getSelectedKids() {
            if (list == null)
                return "";
            StringBuilder val = new StringBuilder();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isSelected) {
                    val.append(list.get(i).name).append(",");
                }
            }
            return !TextUtils.isEmpty(val.toString()) ? val.substring(0, val.length() - 1) : "";
        }


        public class ViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.chkName)
            CheckBox chkName;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                chkName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        list.get(getAdapterPosition()).isSelected=isChecked;
                    }
                });
            }
        }
    }

    @Override
    public void onSuccess(int apiId, BaseResponse response) {
        super.onSuccess(apiId, response);
        if (progressBar != null)
            hideLoadingBar();
       //     progressBar.setVisibility(View.GONE);
        if (apiId == LeafManager.API_LEAVE_REQ_FORM) {
            LeadResponse res = (LeadResponse) response;
            List<LeadItem> result = res.getResults();
            kidsAdapter = new KidsAdapter(result);
            rvKids.setAdapter(kidsAdapter);
        } else {
            AddPostResponse leaveReq = (AddPostResponse) response;
            AppLog.e(TAG, "leaveReq res : " + leaveReq);
            if(leaveReq.data!=null)
            {
                for (int i=0;i<leaveReq.data.size();i++){
                    new SendNotification(leaveReq.data.get(i).deviceToken).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }

            Toast.makeText(this, getString(R.string.msg_leave_request_success), Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    public void onFailure(int apiId, ErrorResponseModel error) {
        AppLog.e(TAG, "errors : " + error);
        if (progressBar != null)
            hideLoadingBar();
        //     progressBar.setVisibility(View.GONE);

        if (error.status.equals("401")) {
            Toast.makeText(this, getResources().getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            logout();
        } else {
            Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onException(int apiId, String msg) {
        AppLog.e(TAG, "msg : " + msg);
        super.onException(apiId, msg);
        if (progressBar != null)
            hideLoadingBar();
        //     progressBar.setVisibility(View.GONE);
    }

    private class SendNotification extends AsyncTask<String, String, String> {
        private String server_response;
        String receiverToken;

        public SendNotification(String deviceToken) {
            receiverToken = deviceToken;
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL("https://fcm.googleapis.com/fcm/send");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", BuildConfig.API_KEY_FIREBASE1+BuildConfig.API_KEY_FIREBASE2);
                urlConnection.setRequestProperty("Content-Type", "application/json");

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                try {
                    JSONObject object = new JSONObject();
                    String title = getResources().getString(R.string.app_name);
                    String message = "";

                    message = leaveReq.reason;
                    object.put("to", receiverToken);

                    JSONObject notificationObj = new JSONObject();
                    notificationObj.put("title", title);
                    notificationObj.put("body", message);
                    object.put("notification", notificationObj);

                    JSONObject dataObj = new JSONObject();
                    dataObj.put("groupId", group_id);
                    dataObj.put("createdById", LeafPreference.getInstance(LeaveActivity.this).getString(LeafPreference.LOGIN_ID));
                    dataObj.put("postId", "");
                    dataObj.put("teamId", team_id);
                    dataObj.put("title", title);
                    dataObj.put("Notification_type", "leave");
                    dataObj.put("body", message);
                    object.put("data", dataObj);

                    wr.writeBytes(object.toString());
                    Log.e(TAG, " JSON input : " + object.toString());
                    wr.flush();
                    wr.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                AppLog.e(TAG, "responseCode :" + responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return server_response;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            AppLog.e(TAG, "server_response :" + server_response);

            if (!TextUtils.isEmpty(server_response)) {
                AppLog.e(TAG, "Notification Sent");
            } else {
                AppLog.e(TAG, "Notification Send Fail");
            }
        }
    }
}
