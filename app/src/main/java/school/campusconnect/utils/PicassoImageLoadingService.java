package school.campusconnect.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ss.com.bannerslider.ImageLoadingService;

public class PicassoImageLoadingService implements ImageLoadingService {
    public Context context;

    public PicassoImageLoadingService(Context context) {
        this.context = context;
    }

    @Override
    public void loadImage(String url, ImageView imageView) {

        if (url == null && !url.equalsIgnoreCase("")) {
            Picasso.with(context).load(url).into(imageView);
        }

 //       Picasso.get().load(url).into(imageView);
    }

    @Override
    public void loadImage(int resource, ImageView imageView) {
     Picasso.with(context).load(resource).into(imageView);
   //     Picasso.get().load(resource).into(imageView);
    }

    @Override
    public void loadImage(String url, int placeHolder, int errorDrawable, ImageView imageView) {
        if (url != null && !url.equalsIgnoreCase("")) {
            Picasso.with(context).load(url).placeholder(placeHolder).error(errorDrawable).into(imageView);
        }


//        Picasso.get().load(url).placeholder(placeHolder).error(errorDrawable).into(imageView);
    }
}