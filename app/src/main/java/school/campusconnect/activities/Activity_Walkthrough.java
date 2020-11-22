package school.campusconnect.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import school.campusconnect.R;
import school.campusconnect.database.LeafPreference;
import school.campusconnect.fragments.Fragment_Walkthrough;

public class Activity_Walkthrough extends AppCompatActivity implements View.OnClickListener {
    private ViewPager viewPager;
    private View indicator1;
    private View indicator2;
    private View indicator3;
    private View indicator4;

    private TextView tvTitle, tvDescription, tvSkip;

    ImageView imgLeft, imgRight;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.walkthrough10_layout);

        indicator1 = findViewById(R.id.indicator1);
        indicator2 = findViewById(R.id.indicator2);
        indicator3 = findViewById(R.id.indicator3);
        indicator4 = findViewById(R.id.indicator4);
        imgLeft = findViewById(R.id.imgLeft);
        imgRight = findViewById(R.id.imgRight);
        tvSkip = findViewById(R.id.tvSkip);

        tvTitle = (TextView) findViewById(R.id.title);
        tvDescription = (TextView) findViewById(R.id.description);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new WizardPageChangeListener());

        imgLeft.setOnClickListener(this);
        imgRight.setOnClickListener(this);
        tvSkip.setOnClickListener(this);

        updateIndicators(0);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private int WIZARD_PAGES_COUNT = 4;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new Fragment_Walkthrough(position);
        }

        @Override
        public int getCount() {
            return WIZARD_PAGES_COUNT;
        }

    }

    private class WizardPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int position) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            // TODO Auto-generated method stub

        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onPageSelected(int position) {
            updateIndicators(position);
            if (position == 3) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LeafPreference.getInstance(getApplicationContext()).setBoolean(LeafPreference.ISWALKTHROUGHDONE, true);
                        Intent intent = new Intent(Activity_Walkthrough.this, LoginActivity2.class);
                        startActivity(intent);
                        finish();
                    }
                }, 4000);

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void updateIndicators(int position) {

        updateTexts(position);

        switch (position) {
            case 0:
                imgLeft.setVisibility(View.GONE);
                indicator1.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_dot));
                indicator2.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_dot_grey));
                indicator3.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_dot_grey));
                indicator4.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_dot_grey));

                break;
            case 1:
                imgLeft.setVisibility(View.VISIBLE);
                imgRight.setVisibility(View.VISIBLE);
                indicator1.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_dot_grey));
                indicator2.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_dot));
                indicator3.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_dot_grey));
                indicator4.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_dot_grey));
                break;
            case 2:
                imgLeft.setVisibility(View.VISIBLE);
                imgRight.setVisibility(View.VISIBLE);
                indicator1.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_dot_grey));
                indicator2.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_dot_grey));
                indicator3.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_dot));
                indicator4.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_dot_grey));
                break;
            case 3:
                imgLeft.setVisibility(View.VISIBLE);
                imgRight.setVisibility(View.GONE);
                indicator1.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_dot_grey));
                indicator2.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_dot_grey));
                indicator3.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_dot_grey));
                indicator4.setBackground(ContextCompat.getDrawable(this, R.drawable.icon_dot));
                break;
        }
    }

    private void updateTexts(int position) {

        switch (position) {
          /*  case 0 :

                tvTitle.setText("Create New Group");
                tvDescription.setText("Click Menu , Choose Create New Group , Fill Details And It's Created!!");

                break;*/

            case 0:

                tvTitle.setText("Multiple Group");
                tvDescription.setText(getResources().getString(R.string.lorem_small));

                break;

            case 1:

                tvTitle.setText("News Feed");
                tvDescription.setText(getResources().getString(R.string.lorem_small));

                break;

            case 2:

                tvTitle.setText("Personal Chats");
                tvDescription.setText(getResources().getString(R.string.lorem_small));

                break;
            case 4:
                tvTitle.setText("Group Chats");
                tvDescription.setText(getResources().getString(R.string.lorem_small));
                break;

            default:

                break;


        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvSkip:
                LeafPreference.getInstance(getApplicationContext()).setBoolean(LeafPreference.ISWALKTHROUGHDONE, true);
                Intent intent = new Intent(Activity_Walkthrough.this, LoginActivity2.class);
                startActivity(intent);
                finish();
                //Toast.makeText(this, "Button Get Started clicked!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.imgLeft:
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                break;
            case R.id.imgRight:
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                break;
            default:
                break;
        }
    }
}
