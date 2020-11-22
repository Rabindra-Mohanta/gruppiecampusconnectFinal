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
import school.campusconnect.adapters.MultiImageAdapter;

public class FullScreenMultiActivity extends BaseActivity implements MultiImageAdapter.OnImageClickListener {

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    @Bind(R.id.iconBack)
    ImageView iconBack;

    ArrayList<String> listImages;

    MultiImageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_multiple);
        ButterKnife.bind(this);

        listImages = getIntent().getStringArrayListExtra("image_list");

        for (String s : listImages) {
            Log.e("Imagesssss", s);
        }

        final LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        adapter = new MultiImageAdapter(listImages, this);
        recyclerView.setAdapter(adapter);

        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onImageClick(String imagePath) {
        Intent i = new Intent(this, FullScreenActivity.class);
        i.putExtra("image", imagePath);
        startActivity(i);
    }
}

