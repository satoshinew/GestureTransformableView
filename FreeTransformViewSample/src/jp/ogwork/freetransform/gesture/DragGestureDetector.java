package jp.ogwork.freetransform.gesture;

import android.util.Log;
import android.view.MotionEvent;

public class DragGestureDetector {

    public static final String TAG = DragGestureDetector.class.getName();

    private float downX = 0;
    private float downY = 0;
    private float downX2 = 0;
    private float downY2 = 0;
    private float deltaX = 0;
    private float deltaY = 0;

    private boolean isFirstPointerUp = false;

    private DragGestureListener dragGestureListener;

    public interface DragGestureListener {
        void onDragGestureListener(DragGestureDetector dragGestureDtector);
    }

    public DragGestureDetector(DragGestureListener dragGestureListener) {
        this.dragGestureListener = dragGestureListener;
    }

    public float getDeltaX() {
        return this.deltaX;
    }

    public float getDeltaY() {
        return this.deltaY;
    }

    @SuppressWarnings("deprecation")
    synchronized public boolean onTouchEvent(MotionEvent event, float convertedX, float convertedY) {

        float eventX = event.getX();
        float eventY = event.getY();
        int count = event.getPointerCount();

        Log.d(TAG, "MotionEvent : " + event.getAction() + " downX : " + downX + " downY : " + downY + " eventX : "
                + eventX + " eventY : " + eventY);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            downX = eventX;
            downY = eventY;
            if (count >= 2) {
                downX2 = event.getX(1);
                downY2 = event.getY(1);
            }
            break;
        case MotionEvent.ACTION_MOVE:

            deltaX = eventX - downX;
            deltaY = eventY - downY;

            // if (dragGestureListener != null && (count < 2)) {
            dragGestureListener.onDragGestureListener(this);
            // }

            break;
        case MotionEvent.ACTION_POINTER_UP:

            int upId = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
            Log.d(TAG, "ACTION_POINTER_UP id : " + upId);

            switch (event.getAction()) {
            case MotionEvent.ACTION_POINTER_1_UP:
                isFirstPointerUp = true;
                break;
            default:
            }
            downX = eventX;
            downY = eventY;
            break;
        case MotionEvent.ACTION_POINTER_DOWN:

            int downId = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
            Log.d(TAG, "ACTION_POINTER_DOWN id : " + downId);

            downX2 = event.getX(1);
            downY2 = event.getY(1);
            break;
        default:
        }

        if (isFirstPointerUp) {
            downX = downX2;
            downY = downY2;
            isFirstPointerUp = false;
        } else {
            // downX = eventX;
            // downY = eventY;
        }

        return false;
    }
}
