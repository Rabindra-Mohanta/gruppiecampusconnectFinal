package school.campusconnect.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TitleView extends TextView {
    private static final String ROBOTO_BOLD = "fonts/Roboto-Bold.ttf";

    public TitleView(Context context) {
        super(context);
        typeface(context);
    }

    private void typeface(Context context) {
        Typeface face = Typeface.createFromAsset(context.getAssets(), ROBOTO_BOLD);
        this.setTypeface(face);
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        typeface(context);
    }

    public TitleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        typeface(context);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
