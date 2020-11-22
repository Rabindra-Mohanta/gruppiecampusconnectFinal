package school.campusconnect.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import school.campusconnect.R;


/**
 * Created by wahyu on 15/11/16.
 */

@SuppressLint("ValidFragment")
public class Fragment_Walkthrough extends Fragment {
    int wizard_page_position;
    Handler animHandler;
    Runnable r;

    ImageView img;
    int currentImage;
    int [] images = new int[]{R.drawable.intro1,R.drawable.intro2,R.drawable.intro3,R.drawable.intro4};

    int timer [] = new int[]{1200 , 1200 , 1200,1200};


    public Fragment_Walkthrough(int position)
    {
        this.wizard_page_position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layout_id = R.layout.walkthrough10_fragment;
        View view = inflater.inflate(layout_id, container, false);

        img = (ImageView) view.findViewById(R.id.imagePage1);

        animHandler = new Handler();
        r  =new Runnable()
        {
            @Override
            public void run()
            {
                loadImage();
            }
        };
        currentImage = 0 ;
        animHandler.removeCallbacks(r);
        animHandler.post(r);

        return view;

    }

 /*   @Override
    public void onResume() {
        super.onStart();


    }*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(animHandler != null)
        {
         animHandler.removeCallbacks(r);
        }
    }

    private void loadImage()
    {
        img.setImageResource(images[wizard_page_position]);
        animHandler.removeCallbacks(r);
        animHandler.postDelayed(r , timer[wizard_page_position]);
    }
}
