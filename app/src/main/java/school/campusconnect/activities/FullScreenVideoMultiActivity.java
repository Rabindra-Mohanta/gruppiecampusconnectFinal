package school.campusconnect.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import school.campusconnect.R;
import school.campusconnect.adapters.MultiVideoAdapter;

public class FullScreenVideoMultiActivity extends BaseActivity implements MultiVideoAdapter.OnImageClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Bind(R.id.iconBack)
    ImageView iconBack;

    ArrayList<String> listImages;
    ArrayList<String> thumbnailImages;

    MultiVideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_multiple);
        ButterKnife.bind(this);

        listImages = getIntent().getStringArrayListExtra("video_list");
        thumbnailImages = getIntent().getStringArrayListExtra("thumbnailImages");

        for (String s : listImages) {
            Log.e("videos ", s);
        }

        final LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        adapter = new MultiVideoAdapter(listImages,thumbnailImages, this);
        recyclerView.setAdapter(adapter);

        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onImageClick(String imagePath) {
        Intent i = new Intent(this, VideoPlayActivity.class);
        i.putExtra("video", imagePath);
        startActivity(i);
    }
}

