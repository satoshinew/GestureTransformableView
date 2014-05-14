package jp.ogwork.freetransform.gesture;

import android.util.Log;
import android.view.MotionEvent;

public class DragGestureDetector {

    private float downX = 0;
    private float downY = 0;
    private float downX2 = 0;
    private float downY2 = 0;
    private float deltaX = 0;
    private float deltaY = 0;

    private boolean isFirstPointerUp = false;

    private DragGestureListener dragGestureListener;

    private float offsetX;
    private float offsetY;

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

    /** 原点オフセット */
    public void setOriginalOffset(float offsetX, float offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;

        downX += downX;
        downY += downY;
        downX2 += downX;
        downY2 += downY;
    }

    @SuppressWarnings("deprecation")
    synchronized public boolean onTouchEvent(MotionEvent event, float convertedX, float convertedY) {

        // float eventX = convertedX;
        // float eventY = convertedY;
        float eventX = event.getX();
        float eventY = event.getY();
        int count = event.getPointerCount();

        Log.d(DragGestureDetector.class.getName(), "MotionEvent : " + event.getAction() + " downX : " + downX
                + " downY : " + downY + " eventX : " + eventX + " eventY : " + eventY);

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN:
            downX = eventX;
            downY = eventY;
            if (count >= 2) {
                downX2 = event.getX(1);
                downY2 = event.getY(1);
            }
            break;
        case MotionEvent.ACTION_POINTER_DOWN:
            downX2 = event.getX(1);
            downY2 = event.getY(1);
            break;
        case MotionEvent.ACTION_MOVE:

            deltaX = eventX - downX;
            deltaY = eventY - downY;

            if (dragGestureListener != null && (count < 2)) {
                dragGestureListener.onDragGestureListener(this);
            }

            break;
        case MotionEvent.ACTION_POINTER_UP:
            switch (event.getAction()) {
            case MotionEvent.ACTION_POINTER_1_UP:
                isFirstPointerUp = true;
                break;
            default:
            }
            downX = eventX;
            downY = eventY;
            break;
        default:
        }

        if (isFirstPointerUp) {
            downX = downX2;
            downY = downY2;
            isFirstPointerUp = false;
        } else {
//            downX = eventX;
//            downY = eventY;
        }

        return false;
    }
}
