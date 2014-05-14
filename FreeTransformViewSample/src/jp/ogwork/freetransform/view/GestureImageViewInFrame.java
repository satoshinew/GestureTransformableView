package jp.ogwork.freetransform.view;

import jp.ogwork.freetransform.gesture.DragGestureDetector;
import jp.ogwork.freetransform.gesture.RotateGestureDetector;
import jp.ogwork.freetransform.gesture.DragGestureDetector.DragGestureListener;
import jp.ogwork.freetransform.gesture.RotateGestureDetector.RotateGestureListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;


public class GestureImageViewInFrame extends ImageView implements OnTouchListener {

    public static final String TAG = GestureImageViewInFrame.class.getName();

    private float scaleFactor = 1.0f;

    private RotateGestureDetector rotateGestureDetector;

    private ScaleGestureDetector scaleGestureDetector;

    private DragGestureDetector dragGestureDetector;

    private Matrix matrix = new Matrix();

    private Paint paintRed = new Paint();

    private Paint paintBlue = new Paint();

    private Paint paintGreen = new Paint();

    private Rect rect = new Rect();

    private float currentX;

    private float currentY;

    private float angle;

    private Context context;

    private int parentViewWidth;

    private int parentViewHeight;

    private float convertedX;

    private float convertedY;

    public GestureImageViewInFrame(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public GestureImageViewInFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GestureImageViewInFrame(Context context) {
        super(context);
        init(context);
    }

    public void setParentViewWidthHeight(int width, int height) {
        this.parentViewWidth = width;
        this.parentViewHeight = height;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "■onTouch() getX() : " + event.getX() + " getY() : " + event.getY());

        currentX = event.getX();
        currentY = event.getY();

        Rotation rot = new Rotation(getWidth() / 2, getHeight() / 2);
        convertedX = event.getX();
        convertedY = event.getY();
        if (convertedX >= 0 && convertedX < getWidth() && convertedY >= 0 && convertedY < getHeight()) {
            // rot.rot(angle, event.getX(), event.getY());
            // draggerX = rot.getX();
            // draggerY = rot.getY();

            // PointF pf = rotateXY(getWidth() / 2, getHeight() / 2, angle,
            // draggerX, draggerY);
            // draggerX = pf.x;
            // draggerY = pf.y;

            // draggerX *= scaleFactor;
            // draggerY *= scaleFactor;
            Log.d(TAG, "■     convertedX : " + convertedX + " convertedY : " + convertedY + " angle : " + angle
                    + " scaleFactor : " + scaleFactor);
            dragGestureDetector.onTouchEvent(event, convertedX, convertedY);
        }

        scaleGestureDetector.onTouchEvent(event);
        rotateGestureDetector.onTouchEvent(event);

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        getDrawingRect(rect);
        canvas.drawRect(rect, paintRed);

        // canvas.drawPoint(currentX, currentY, paintBlue);
        // canvas.drawPoint(convertedX, convertedY, paintGreen);
    }

    private void init(Context context) {

        this.context = context;

        /** Matrix有効化 これやらないとsetImageMatrix()が動かない */
        // setScaleType(ScaleType.MATRIX);

        paintRed.setColor(Color.RED);
        paintRed.setStyle(Style.STROKE);
        paintRed.setStrokeWidth(10.f);

        paintBlue.setColor(Color.BLUE);
        paintBlue.setStyle(Style.STROKE);
        paintBlue.setStrokeWidth(10.f);
        paintGreen.setColor(Color.GREEN);
        paintGreen.setStyle(Style.STROKE);
        paintGreen.setStrokeWidth(10.f);

        currentX = getX();
        currentY = getY();

        setOnTouchListener(this);
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        rotateGestureDetector = new RotateGestureDetector(new RotateListener());
        dragGestureDetector = new DragGestureDetector(new DragListener());

    }

    private PointF rotateXY(float centerX, float centerY, float angle, float x, float y) {
        // http://d.hatena.ne.jp/speg03/20090305/1236218244

        float resultX = 0;
        float resultY = 0;

        double rad = Math.toRadians(angle);

        resultX = (float) ((x - centerX) * Math.cos(rad) - (y - centerY) * Math.sin(rad) + centerX);
        resultY = (float) ((x - centerX) * Math.sin(rad) + (y - centerY) * Math.cos(rad) + centerY);

        return new PointF(resultX, resultY);
    }

    private class ScaleListener extends SimpleOnScaleGestureListener {
        @Override
        synchronized public boolean onScale(ScaleGestureDetector detector) {
            /** A */
            scaleFactor *= detector.getScaleFactor();
            setScaleX(scaleFactor);
            setScaleY(scaleFactor);

            return true;
        }
    }

    private class RotateListener implements RotateGestureListener {

        @Override
        public void onRotation(RotateGestureDetector detector) {

            angle += detector.getDeltaAngle();

            /** A */
            setRotation(getRotation() + detector.getDeltaAngle());

            Log.d(TAG, "RotationListener : getX : " + getX() + " getY : " + getY());
        }

        @Override
        public void onRotationBegin(jp.ogwork.freetransform.gesture.RotateGestureDetector detector) {

        }

        @Override
        public void onRotationEnd(jp.ogwork.freetransform.gesture.RotateGestureDetector detector) {
        }
    }

    private class DragListener implements DragGestureListener {

        @Override
        synchronized public void onDragGestureListener(DragGestureDetector dragGestureDtector) {

            Log.d(TAG, "onDragGestureListener() getX() : " + getX() + " getY() : " + getY() + " getDeltaX() : "
                    + dragGestureDtector.getDeltaX() + " getDeltaY() : " + dragGestureDtector.getDeltaY());

            /** getX / getY が、拡大率問わずViewを中心に置いたところをx=0,y=0とするため、補正 */
            // float origX = (parentViewWidth - (getWidth() * scaleFactor)) /
            // 2f;
            // float origY = (parentViewHeight - (getHeight() * scaleFactor)) /
            // 2f;
            // Log.d(TAG, "onDragGestureListener() origX : " + origX +
            // " origY : " + origY);

            /** A */
            setX(getX() + dragGestureDtector.getDeltaX());
            setY(getY() + dragGestureDtector.getDeltaY());

            /** B */
            // setTranslationX(getTranslationX() +
            // dragGestureDtector.getDeltaX());
            // setTranslationY(getTranslationY() +
            // dragGestureDtector.getDeltaY());

        }
    }

    // 二次元座標回転 Class
    class Rotation {
        float cx, cy; // 回転軸の中心
        float x, y; // 回転後の座標

        // Constructor
        public Rotation(float a, float b) {
            cx = a;
            cy = b;
        }

        // 座標回転メソッド
        public void rot(double w, float xx, float yy) {
            double px, py;
            px = xx - cx;
            py = yy - cy;
            x = (int) (px * Math.cos(w / 180 * 3.14)) - (int) (py * Math.sin(w / 180 * 3.14)) + cx;
            y = (int) (px * Math.sin(w / 180 * 3.14)) + (int) (py * Math.cos(w / 180 * 3.14)) + cy;
        }

        public float getX() {
            return this.x;
        }

        public float getY() {
            return this.y;
        }
    }
}
