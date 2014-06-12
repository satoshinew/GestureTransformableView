package jp.ogwork.gesturetransformableview.gesture;

import java.util.HashMap;

import android.util.Log;
import android.view.MotionEvent;

public class DragGestureDetector {

    public static final String TAG = DragGestureDetector.class.getName();

    private float deltaX = 0;

    private float deltaY = 0;

    private int originalIndex;

    private HashMap<Integer, TouchPoint> pointMap = new HashMap<>();

    private DragGestureListener dragGestureListener;

    public interface DragGestureListener {
        void onDragGestureListener(DragGestureDetector dragGestureDtector);
    }

    public DragGestureDetector(DragGestureListener dragGestureListener) {
        this.dragGestureListener = dragGestureListener;
        pointMap.put(0, createPoint(0.f, 0.f));
        originalIndex = 0;
    }

    public float getDeltaX() {
        return this.deltaX;
    }

    public float getDeltaY() {
        return this.deltaY;
    }

    synchronized public boolean onTouchEvent(MotionEvent event) {

        if (event.getPointerCount() >= 3) {
            return false;
        }

        float eventX = event.getX(originalIndex);
        float eventY = event.getY(originalIndex);

        int action = event.getAction() & MotionEvent.ACTION_MASK;
        int actionPointer = event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK;

        switch (action) {
        case MotionEvent.ACTION_DOWN: {

            /** 最初のpointしか来ない */

            TouchPoint downPoint = pointMap.get(0);
            if (downPoint != null) {
                downPoint.setXY(eventX, eventY);
            } else {
                downPoint = createPoint(eventX, eventY);
                pointMap.put(0, downPoint);
            }

            originalIndex = 0;

            break;
        }
        case MotionEvent.ACTION_MOVE: {

            TouchPoint originalPoint = pointMap.get(originalIndex);
            if (originalPoint != null) {
                deltaX = eventX - originalPoint.x;
                deltaY = eventY - originalPoint.y;

                if (dragGestureListener != null) {// && (count < 2)) {
                    dragGestureListener.onDragGestureListener(this);
                }
            }

            break;
        }
        case MotionEvent.ACTION_POINTER_DOWN: {

            int downId = actionPointer >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

            float multiTouchX = event.getX(downId);
            float multiTouchY = event.getY(downId);

            TouchPoint p = pointMap.get(downId);

            if (p != null) {
                p.x = multiTouchX;
                p.y = multiTouchY;
            } else {
                pointMap.put(downId, createPoint(multiTouchX, multiTouchY));
            }

            break;
        }
        case MotionEvent.ACTION_POINTER_UP: {

            int upId = actionPointer >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
            Log.d(TAG, "ACTION_POINTER_UP id : " + upId);

            if (originalIndex == upId) {
                Log.d(TAG, "ACTION_POINTER_UP orig up");
                /** 起点の指が離れた */
                pointMap.remove(upId);

                TouchPoint secondPoint = null;
                for (int index = 0; index < pointMap.size(); index++) {
                    if (originalIndex != index) {
                        secondPoint = pointMap.get(index);
                        if (secondPoint != null) {
                            secondPoint.setXY(event.getX(index), event.getY(index));
                            originalIndex = index;
                            break;
                        }
                    }
                }
            }

            break;
        }

        default:
        }
        return false;
    }

    private TouchPoint createPoint(float x, float y) {
        return new TouchPoint(x, y);
    }

    class TouchPoint {
        public float x;
        public float y;

        public TouchPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public TouchPoint setXY(float x, float y) {
            this.x = x;
            this.y = y;
            return this;
        }
    }
}
