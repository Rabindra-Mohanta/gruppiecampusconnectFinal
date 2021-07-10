package school.campusconnect.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Binder;
import android.os.IBinder;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.andremion.counterfab.CounterFab;

import school.campusconnect.R;
import school.campusconnect.activities.VideoClassActivity;
import school.campusconnect.utils.AppLog;


/**
 * Created by anupamchugh on 01/08/17.
 */

public class FloatingWidgetService extends Service {

    private WindowManager windowManager;
    private ImageView floatingFaceBubble;
    private final IBinder mBinder = new LocalBinder();

    public void onCreate() {
        super.onCreate();
        AppLog.e("BubbleService", "onCreate called");
        floatingFaceBubble = new ImageView(this);
        //a face floating bubble as imageView
        floatingFaceBubble.setImageResource(R.drawable.icon_record);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        //here is all the science of params

        final WindowManager.LayoutParams myParams;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            myParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
        else
        {
            myParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }
        myParams.gravity = Gravity.TOP | Gravity.LEFT;
        myParams.x = 10;
        myParams.y = 200;
        // add a floatingfacebubble icon in window
        windowManager.addView(floatingFaceBubble, myParams);
        try {
            //for moving the picture on touch and slide
            floatingFaceBubble.setOnTouchListener(new View.OnTouchListener() {
                WindowManager.LayoutParams paramsT = myParams;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;
                private long touchStartTime = 0;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //remove face bubble on long press
                    /*if (System.currentTimeMillis() - touchStartTime > ViewConfiguration.getLongPressTimeout() && initialTouchX == event.getX()) {
                        windowManager.removeView(floatingFaceBubble);
                        stopSelf();
                        return false;
                    }*/
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touchStartTime = System.currentTimeMillis();
                            initialX = myParams.x;
                            initialY = myParams.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            if (System.currentTimeMillis() - touchStartTime < 150)
                            {
                                Intent intent = new Intent("recording");
                                // You can also include some extra data.
                                intent.putExtra("action", "start");
                                LocalBroadcastManager.getInstance(v.getContext()).sendBroadcast(intent);
                                windowManager.removeView(floatingFaceBubble);
                            }
                            break;
                        case MotionEvent.ACTION_MOVE:
                            myParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                            myParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(v, myParams);
                            break;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppLog.e("BubbleService", "onStartCommand called");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AppLog.e("BubbleService", "onDestroy called");
    }

    public class LocalBinder extends Binder {
        public FloatingWidgetService getService() {
            // Return this instance of LocalService so clients can call public methods
            return FloatingWidgetService.this;
        }
    }

    public void removeBubble() {
        try {
            windowManager.removeView(floatingFaceBubble);
            AppLog.e("BubbleService", "removeView");
        } catch (Exception e) {
            AppLog.e("BubbleService", "error is " + e.toString());
        }
        stopSelf();
    }

}
