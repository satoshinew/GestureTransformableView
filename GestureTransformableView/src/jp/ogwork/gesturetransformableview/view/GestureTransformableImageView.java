package jp.ogwork.gesturetransformableview.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jp.ogwork.gesturetransformableview.gesture.DragGestureDetector;
import jp.ogwork.gesturetransformableview.gesture.DragGestureDetector.DragGestureListener;
import jp.ogwork.gesturetransformableview.gesture.PinchGestureDetector;
import jp.ogwork.gesturetransformableview.gesture.PinchGestureDetector.PinchGestureListener;
import jp.ogwork.gesturetransformableview.gesture.RotateGestureDetector;
import jp.ogwork.gesturetransformableview.gesture.RotateGestureDetector.RotateGestureListener;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class GestureTransformableImageView extends ImageView implements OnTouchListener {

    static {
//        System.loadLibrary("GestureTransformableView");
    }

    public static native float[] nativeRotateXY(float centerX, float centerY, float angle, float x, float y);

    public static final int GESTURE_DRAGGABLE = 0x0001;

    public static final int GESTURE_ROTATABLE = 0x0002;

    public static final int GESTURE_SCALABLE = 0x0004;

    public static final String TAG = GestureTransformableImageView.class.getName();

    public static final float DEFAULT_LIMIT_SCALE_MAX = 2.7f;

    public static final float DEFAULT_LIMIT_SCALE_MIN = 0.5f;

    private float limitScaleMax = DEFAULT_LIMIT_SCALE_MAX;

    private float limitScaleMin = DEFAULT_LIMIT_SCALE_MIN;

    private float scaleFactor = 1.0f;

    private RotateGestureDetector rotateGestureDetector;

    private DragGestureDetector dragGestureDetector;

    private PinchGestureDetector pinchGestureDetector;

    private float angle;

    public GestureTransformableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, GESTURE_DRAGGABLE | GESTURE_ROTATABLE | GESTURE_SCALABLE);
        loadLibrary(context);
    }

    public GestureTransformableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, GESTURE_DRAGGABLE | GESTURE_ROTATABLE | GESTURE_SCALABLE);
        loadLibrary(context);
    }

    public GestureTransformableImageView(Context context) {
        super(context);
        init(context, GESTURE_DRAGGABLE | GESTURE_ROTATABLE | GESTURE_SCALABLE);

        loadLibrary(context);
    }

    public GestureTransformableImageView(Context context, int gestureFlag) {
        super(context);
        init(context, gestureFlag);
    }

    public void setLimitScaleMax(float limit) {
        this.limitScaleMax = limit;
    }

    public void setLimitScaleMin(float limit) {
        this.limitScaleMin = limit;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (rotateGestureDetector != null) {
            rotateGestureDetector.onTouchEvent(event);
        }

        if (dragGestureDetector != null) {
            dragGestureDetector.onTouchEvent(event);
        }

        if (pinchGestureDetector != null) {
            pinchGestureDetector.onTouchEvent(event);
        }

        return true;
    }

    private void init(Context context, int gestureFlag) {

        setOnTouchListener(this);

        if ((gestureFlag & GESTURE_DRAGGABLE) == GESTURE_DRAGGABLE) {
            dragGestureDetector = new DragGestureDetector(new DragListener());
        }
        if ((gestureFlag & GESTURE_ROTATABLE) == GESTURE_ROTATABLE) {
            rotateGestureDetector = new RotateGestureDetector(new RotateListener());
        }
        if ((gestureFlag & GESTURE_SCALABLE) == GESTURE_SCALABLE) {
            pinchGestureDetector = new PinchGestureDetector(new ScaleListener());
        }

    }

    private void loadLibrary(Context context) {
        String path = "/data/data/jp.ogwork.gesturetransformableview.gesture/libGestureTransformableView.so";
        InputStream is;
        try {
            is = context.getResources().getAssets().open("libGestureTransformableView.so");
            File fileout = new File(path);
            OutputStream os = new FileOutputStream(fileout);
            final int DEFAULT_BUFFER_SIZE = 1024 * 4;
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int n = 0;
            while (-1 != (n = is.read(buffer))) {
                os.write(buffer, 0, n);
            }
            is.close();
            os.close();
            System.load(fileout.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static PointF rotateXY(float centerX, float centerY, float angle, float x, float y) {
        /** unused. instead nativeRotateXY() */

        float resultX = 0;
        float resultY = 0;

        double rad = Math.toRadians(angle);

        resultX = (float) ((x - centerX) * Math.cos(rad) - (y - centerY) * Math.sin(rad) + centerX);
        resultY = (float) ((x - centerX) * Math.sin(rad) + (y - centerY) * Math.cos(rad) + centerY);

        return new PointF(resultX, resultY);
    }

    private class ScaleListener implements PinchGestureListener {

        @Override
        public void onPinchGestureListener(PinchGestureDetector dragGestureDetector) {

            float scale = dragGestureDetector.getDistance() / dragGestureDetector.getPreDistance();
            float tmpScale = scaleFactor * scale;

            if (limitScaleMin <= tmpScale && tmpScale <= limitScaleMax) {
                scaleFactor = tmpScale;
                setScaleX(scaleFactor);
                setScaleY(scaleFactor);

                return;
            }

        }
    }

    private class RotateListener implements RotateGestureListener {

        @Override
        public void onRotation(RotateGestureDetector detector) {

            angle += detector.getDeltaAngle();

            setRotation(getRotation() + detector.getDeltaAngle());
        }

        @Override
        public void onRotationBegin(RotateGestureDetector detector) {

        }

        @Override
        public void onRotationEnd(RotateGestureDetector detector) {
        }
    }

    private class DragListener implements DragGestureListener {

        @Override
        synchronized public void onDragGestureListener(DragGestureDetector dragGestureDetector) {

            float dx = dragGestureDetector.getDeltaX();
            float dy = dragGestureDetector.getDeltaY();
            float[] rotatedPoints = nativeRotateXY(0, 0, angle, dx, dy);

            setX(getX() + rotatedPoints[0] * scaleFactor);
            setY(getY() + rotatedPoints[1] * scaleFactor);
        }
    }
}
