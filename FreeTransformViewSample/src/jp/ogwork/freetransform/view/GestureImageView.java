package jp.ogwork.freetransform.view;

import jp.ogwork.freetransform.gesture.DragGestureDetector;
import jp.ogwork.freetransform.gesture.RotateGestureDetector;
import jp.ogwork.freetransform.gesture.TranslationGestureDetector;
import jp.ogwork.freetransform.gesture.TranslationGestureListener;
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


public class GestureImageView extends ImageView implements OnTouchListener {

    public static final String TAG = GestureImageView.class.getName();

    private float scaleFactor = 1.0f;

    private RotateGestureDetector rotateGestureDetector;

    private ScaleGestureDetector scaleGestureDetector;

    private DragGestureDetector dragGestureDetector;

    private TranslationGestureDetector translationGestureDetector;

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

    private float convertX;

    private float convertY;

    public GestureImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public GestureImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GestureImageView(Context context) {
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

        convertX = event.getX();
        convertY = event.getY();

        if (convertX >= 0 && convertX < getWidth() && convertY >= 0 && convertY < getHeight()) {

            /** 座標回転 */
            // PointF pf = rotateXY(getWidth() / 2, getHeight() / 2, angle,
            // convertX, convertY);
            // convertX = pf.x;
            // convertY = pf.y;

            /** 座標Scaling */
            // convertX *= scaleFactor;
            // convertY *= scaleFactor;

            // Log.d(TAG, "■     convertX : " + convertX + " convertY : " +
            // convertY + " angle : " + angle
            // + " scaleFactor : " + scaleFactor);

            dragGestureDetector.onTouchEvent(event, convertX, convertY);
        }

        scaleGestureDetector.onTouchEvent(event);
        rotateGestureDetector.onTouchEvent(event);
        // translationGestureDetector.onTouch(v, event);

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        getDrawingRect(rect);
        canvas.drawRect(rect, paintRed);
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
        translationGestureDetector = new TranslationGestureDetector(new TranslationGestureListener() {
            @Override
            public void onTranslation(TranslationGestureDetector detector) {
                super.onTranslation(detector);
                final float dx = detector.getDeltaX();
                final float dy = detector.getDeltaY();
                post(new Runnable() {

                    @Override
                    public void run() {
                        setX(getX() + dx);
                        setY(getY() + dy);
                    }
                });
            }
        });

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

    private int getRelativeLeft(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    private int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
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
//            setRotation(getRotation() + detector.getDeltaAngle());

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
        synchronized public void onDragGestureListener(DragGestureDetector dragGestureDetector) {

            // Log.d(TAG, "onDragGestureListener() getX() : " + getX() +
            // " getY() : " + getY() + " getDeltaX() : "
            // + dragGestureDtector.getDeltaX() + " getDeltaY() : " +
            // dragGestureDtector.getDeltaY());

            float dx = dragGestureDetector.getDeltaX();
            float dy = dragGestureDetector.getDeltaY();

            /** getX / getY が、拡大率問わずViewを中心に置いたところをx=0,y=0とするため、補正 */
            // float origX = (parentViewWidth - (getWidth() * scaleFactor)) /
            // 2f;
            // float origY = (parentViewHeight - (getHeight() * scaleFactor)) /
            // 2f;
            // Log.d(TAG, "onDragGestureListener() origX : " + origX +
            // " origY : " + origY);

            /** A */
            setX(getX() + dx * scaleFactor);
            setY(getY() + dy * scaleFactor);

            /** B */
            // setTranslationX(getTranslationX() +
            // dragGestureDtector.getDeltaX());
            // setTranslationY(getTranslationY() +
            // dragGestureDtector.getDeltaY());

            /** C */
            // setX(getRelativeLeft(GestureImageView.this) +
            // dragGestureDtector.getDeltaX());
            // setY(getRelativeTop(GestureImageView.this) +
            // dragGestureDtector.getDeltaY());

            /** D */
            // Log.d(TAG, "currentX : " + currentX + " currentY : " + currentY +
            // " dy : " + dx + " dy :" + dy);
            // setX(currentX + dx);
            // setY(currentY + dy);
            // currentX += dx;
            // currentY += dy;
        }
    }
}
