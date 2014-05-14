package jp.ogwork.freetransform.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class GestureParentView extends RelativeLayout {

    public interface OnDispatchTouchEventListener {
        void onDispatchTouchEvent(MotionEvent event);
    }

    private OnDispatchTouchEventListener onDispatchTouchEventListener;

    public GestureParentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public GestureParentView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GestureParentView(Context context) {
        super(context);
    }

    public void setOnDispatchTouchEventListener(OnDispatchTouchEventListener onDispatchTouchEventListener) {
        this.onDispatchTouchEventListener = onDispatchTouchEventListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        if (this.onDispatchTouchEventListener != null) {
            onDispatchTouchEventListener.onDispatchTouchEvent(event);
        }

        return super.dispatchTouchEvent(event);
    }
}
