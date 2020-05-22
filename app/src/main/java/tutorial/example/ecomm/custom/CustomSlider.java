package tutorial.example.ecomm.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.smarteist.autoimageslider.SliderView;

public class CustomSlider extends SliderView {
    public CustomSlider(Context context) {
        super(context);
    }

    public CustomSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
