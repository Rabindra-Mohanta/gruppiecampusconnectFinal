package school.campusconnect.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.ContextCompat;

import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import school.campusconnect.R;


public class DrawableEditText extends RelativeLayout {

    ImageView imgLeftDrawable;
    public EditText editText;
    TextInputLayout textInputLayout;

    public DrawableEditText(Context context) {
        super(context);
    }

    public DrawableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context, attrs);
    }

    public DrawableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context, attrs);
    }

    private void initViews(Context context, AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.DrawableEditText);
        String textHint = a.getString(R.styleable.DrawableEditText_textHint);
       /* String textHintTop = a.getString(R.styleable.DrawableEditText_textHintTop);*/
        int inputType = a.getInt(R.styleable.DrawableEditText_android_inputType, EditorInfo.TYPE_CLASS_TEXT);
        final TypedValue value = new TypedValue();
        a.getValue(R.styleable.DrawableEditText_leftDrawable, value);
        inflate(getContext(), R.layout.layout_drawable_editext, this);
        editText = (EditText) findViewById(R.id.editText);
        imgLeftDrawable = (ImageView) findViewById(R.id.img_leftDrawable);
        textInputLayout = (TextInputLayout) findViewById(R.id.textInputLayout);

    /*    if(!TextUtils.isEmpty(textHintTop)){
            textInputLayout.setHint(textHintTop);
        }else {*/
            textInputLayout.setHint(textHint);
        //}

        editText.setInputType(inputType);
        imgLeftDrawable.setImageResource(value.resourceId);
        a.recycle();

        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Drawable upArrow = ContextCompat.getDrawable(getContext(), value.resourceId);
                    upArrow.mutate().setColorFilter(ContextCompat.getColor(getContext(), R.color.color_orange_hover), PorterDuff.Mode.SRC_ATOP);
                    imgLeftDrawable.setImageDrawable(upArrow);

                } else {
                    Drawable upArrow = ContextCompat.getDrawable(getContext(), value.resourceId);
                    upArrow.mutate().setColorFilter(ContextCompat.getColor(getContext(), R.color.color_grey_icon), PorterDuff.Mode.SRC_ATOP);
                    imgLeftDrawable.setImageDrawable(upArrow);

                }
            }
        });

    }

    public void setHint(String hint) {
        textInputLayout.setHint(hint);
    }

}