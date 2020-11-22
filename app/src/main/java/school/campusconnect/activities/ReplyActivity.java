package school.campusconnect.activities;

import android.content.Intent;
import androidx.databinding.BaseObservable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import android.os.Bundle;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import school.campusconnect.utils.AppLog;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.fragments.ReplyFragment;

/**
 * Created by frenzin04 on 1/20/2017.
 */
public class ReplyActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    public Toolbar mToolBar;
    Intent intent;
    String group_id;
    String team_id;
    String post_id;
    String comment_id;
    String type;
    ReplyFragment fragment;
    public static ReplyActivity replyActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_teams);
        ButterKnife.bind(this);
        setSupportActionBar(mToolBar);
        setBackEnabled(true);
        setTitle(R.string.lbl_replies);

        replyActivity = this;

        if (getIntent().getExtras() != null)
        {
            fragment = ReplyFragment.newInstance(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            getSupportFragmentManager().executePendingTransactions();
        }


    }

    public void addTextChangedListener(View view) {
       AppLog.e("CLICK", "getText clicked");
        fragment.dataBinding.addTextChangedListener((EditText) view, 0);
    }

    public void onClickAddComment(View view) {
       AppLog.e("CLICK", "btn_comment clicked");
        fragment.onClickAddComment(view);
    }

    public class ObservableString extends BaseObservable {

        private String value = "";

        public ObservableString(String value) {
            this.value = value;
        }

        public ObservableString() {
        }

        public String get() {
            return value != null ? value : "";
        }

        public void set(String value) {
            if (value == null) value = "";
            if (!this.value.contentEquals(value)) {
                this.value = value;
                notifyChange();
            }
        }

        public boolean isEmpty() {
            return value == null || value.isEmpty();
        }

        public void clear() {
            set(null);
        }
    }

    @BindingAdapter("android:text")
    public static void bindIntegerInText(AppCompatEditText tv, int value) {
        tv.setText(String.valueOf(value));
    }

    @InverseBindingAdapter(attribute = "android:text")
    public static String getIntegerFromBinding(TextView view) {
        return view.getText().toString();
    }

}
