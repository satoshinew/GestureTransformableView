package jp.ogwork.gesturetransformableview.view;

import jp.ogwork.gesturetransformableview.gesture.DragGestureDetector;
import jp.ogwork.gesturetransformableview.gesture.DragGestureDetector.DragGestureListener;
import jp.ogwork.gesturetransformableview.gesture.PinchGestureDetector;
import jp.ogwork.gesturetransformableview.gesture.PinchGestureDetector.PinchGestureListener;
import jp.ogwork.gesturetransformableview.gesture.RotateGestureDetector;
import jp.ogwork.gesturetransformableview.gesture.RotateGestureDetector.RotateGestureListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class GestureTransformImageView extends ImageView implements OnTouchListener {

    public static final String TAG = GestureTransformImageView.class.getName();

    public static final float DEFAULT_LIMIT_SCALE_MAX = 2.7f;

    public static final float DEFAULT_LIMIT_SCALE_MIN = 0.5f;

    private float limitScaleMax = DEFAULT_LIMIT_SCALE_MAX;

    private float limitScaleMin = DEFAULT_LIMIT_SCALE_MIN;

    private float scaleFactor = 1.0f;

    private RotateGestureDetector rotateGestureDetector;

    private ScaleGestureDetector scaleGestureDetector;

    private DragGestureDetector dragGestureDetector;

    private PinchGestureDetector pinchGestureDetector;

    private float angle;

    public GestureTransformImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public GestureTransformImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GestureTransformImageView(Context context) {
        super(context);
        init(context);
    }

    public void setLimitScaleMax(float limit) {
        this.limitScaleMax = limit;
    }

    public void setLimitScaleMin(float limit) {
        this.limitScaleMin = limit;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        // scaleGestureDetector.onTouchEvent(event);
        rotateGestureDetector.onTouchEvent(event);
        dragGestureDetector.onTouchEvent(event);
        pinchGestureDetector.onTouchEvent(event);

        return true;
    }

    @SuppressLint("NewApi")
    private void init(Context context) {

        setOnTouchListener(this);

        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            scaleGestureDetector.setQuickScaleEnabled(false);
        }
        rotateGestureDetector = new RotateGestureDetector(new RotateListener());
        dragGestureDetector = new DragGestureDetector(new DragListener());

        pinchGestureDetector = new PinchGestureDetector(new PinchGestureListener() {

            @Override
            public void onPinchGestureListener(final PinchGestureDetector dragGestureDtector) {

                float scale = dragGestureDtector.getDistance() / dragGestureDtector.getPreDistance();
                float tmpScale = scaleFactor * scale;

                if (limitScaleMin <= tmpScale && tmpScale <= limitScaleMax) {
                    scaleFactor = tmpScale;
                    setScaleX(scaleFactor);
                    setScaleY(scaleFactor);

                    return;
                }

                return;
            }
        });

    }

    private PointF rotateXY(float centerX, float centerY, float angle, float x, float y) {

        float resultX = 0;
        float resultY = 0;

        double rad = Math.toRadians(angle);

        resultX = (float) ((x - centerX) * Math.cos(rad) - (y - centerY) * Math.sin(rad) + centerX);
        resultY = (float) ((x - centerX) * Math.sin(rad) + (y - centerY) * Math.cos(rad) + centerY);

        return new PointF(resultX, resultY);
    }

    private class ScaleListener extends SimpleOnScaleGestureListener {

        @SuppressLint("NewApi")
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return super.onScaleBegin(detector);
        }

        @Override
        synchronized public boolean onScale(ScaleGestureDetector detector) {

            Log.d(TAG, "scaleFactor : " + scaleFactor);

            float scale = detector.getCurrentSpan() / detector.getPreviousSpan();
            float tmpScale = scaleFactor * scale;

            // scaleFactor *= detector.getScaleFactor();

            if (limitScaleMin <= tmpScale && tmpScale <= limitScaleMax) {
                scaleFactor = tmpScale;
                setScaleX(scaleFactor);
                setScaleY(scaleFactor);
                return false;
            } else {
                super.onScale(detector);
            }
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }
    }

    private class RotateListener implements RotateGestureListener {

        @Override
        public void onRotation(RotateGestureDetector detector) {

            angle += detector.getDeltaAngle();

            /** A */
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
            PointF pf = rotateXY(0, 0, angle, dx, dy);

            dx = pf.x;
            dy = pf.y;

            setX(getX() + dx * scaleFactor);
            setY(getY() + dy * scaleFactor);
        }
    }
}
