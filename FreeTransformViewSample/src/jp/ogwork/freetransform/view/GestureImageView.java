package jp.ogwork.freetransform.view;

import jp.ogwork.freetransform.gesture.DragGestureDetector;
import jp.ogwork.freetransform.gesture.DragGestureDetector.DragGestureListener;
import jp.ogwork.freetransform.gesture.RotateGestureDetector;
import jp.ogwork.freetransform.gesture.RotateGestureDetector.RotateGestureListener;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
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

    private Paint paintRed = new Paint();

    private Paint paintBlue = new Paint();

    private Paint paintGreen = new Paint();

    private Rect rect = new Rect();

    private float angle;

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

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        convertX = event.getX();
        convertY = event.getY();

        // if (convertX >= 0 && convertX < getWidth() && convertY >= 0 &&
        // convertY < getHeight()) {
        dragGestureDetector.onTouchEvent(event, convertX, convertY);
        // }

        scaleGestureDetector.onTouchEvent(event);
        rotateGestureDetector.onTouchEvent(event);

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        getDrawingRect(rect);
        canvas.drawRect(rect, paintRed);
    }

    private void init(Context context) {

        paintRed.setColor(Color.RED);
        paintRed.setStyle(Style.STROKE);
        paintRed.setStrokeWidth(10.f);

        paintBlue.setColor(Color.BLUE);
        paintBlue.setStyle(Style.STROKE);
        paintBlue.setStrokeWidth(10.f);
        paintGreen.setColor(Color.GREEN);
        paintGreen.setStyle(Style.STROKE);
        paintGreen.setStrokeWidth(10.f);

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

            float dx = dragGestureDetector.getDeltaX();
            float dy = dragGestureDetector.getDeltaY();

            PointF pf = rotateXY(0, 0, angle, dx, dy);
            dx = pf.x;
            dy = pf.y;

            float moveX = getX() + dx * scaleFactor;
            float moveY = getY() + dy * scaleFactor;

            setX(moveX);
            setY(moveY);
        }
    }
}
