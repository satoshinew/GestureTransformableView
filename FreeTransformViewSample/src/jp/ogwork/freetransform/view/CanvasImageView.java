package jp.ogwork.freetransform.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

public class CanvasImageView extends ImageView implements OnTouchListener {

    public static final String TAG = CanvasImageView.class.getName();

    public enum CanvasStateEnum {
        STATE_CHOICE, STATE_NEW_STAMP, STATE_MOVE,
    }

    /** drawItems */
    private ArrayList<DrawItem> drawItemList = new ArrayList<>();

    /** mode */
    private CanvasStateEnum state = CanvasStateEnum.STATE_CHOICE;

    /** stamp info */
    private int stampResId;

    /** focus info */
    private int focusedItemIndex;

    public CanvasImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CanvasImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CanvasImageView(Context context) {
        super(context);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (drawItemList == null || drawItemList.size() <= 0) {
            return;
        }

        for (DrawItem item : drawItemList) {
            canvas.drawBitmap(item.bitmap, item.matrix, item.paint);
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (state) {
        case STATE_CHOICE:

            break;
        case STATE_NEW_STAMP:
            onTouchChoise(event);
            break;
        case STATE_MOVE:
            // onTouchMove(event);
            break;

        default:
            break;
        }
        return true;
    }

    public void setStampResId(int id) {
        this.stampResId = id;
        setState(CanvasStateEnum.STATE_NEW_STAMP);
    }

    public void setState(CanvasStateEnum state) {
        this.state = state;
        Log.d(TAG, "state :" + state);
    }

    private void onTouchChoise(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:

            /** スタンプ読み込み */
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), stampResId);

            /** タッチ地点(スタンプ初期座標)を設定 */
            Matrix matrix = new Matrix();
            matrix.postTranslate(touchX, touchY);

            DrawItem newItem = new DrawItem(bitmap, matrix, new Paint());
            newItem.setFocus(true);

            /** 他のDrawiItemのfocusをクリア */
            for (DrawItem item : drawItemList) {
                item.setFocus(false);
            }
            /** newItemを追加 */
            this.drawItemList.add(newItem);

            this.focusedItemIndex = drawItemList.size() - 1;

            /** newItemがFocusされた状態でMOVEへ移行 */
            setState(CanvasStateEnum.STATE_MOVE);

            break;
        case MotionEvent.ACTION_MOVE:

            break;
        case MotionEvent.ACTION_UP:

            break;

        default:
            break;
        }
        invalidate();
    }

    private void onTouchMove(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:

            break;
        case MotionEvent.ACTION_MOVE:

            DrawItem focusedItem = this.drawItemList.get(focusedItemIndex);
            if (focusedItem.isFocused) {
                focusedItem.matrix.postTranslate(touchX, touchY);
            }

            break;
        case MotionEvent.ACTION_UP:

            break;

        default:
            break;
        }
        invalidate();
    }

    private void init() {
        setOnTouchListener(this);
    }

    class DrawItem {
        public Bitmap bitmap;
        public Matrix matrix;
        public Paint paint;
        public boolean isFocused;

        public DrawItem(Bitmap bitmap, Matrix matrix, Paint paint) {
            this.bitmap = bitmap;
            this.matrix = matrix;
            this.paint = paint;
            isFocused = false;
        }

        public void setFocus(boolean isFocused) {
            this.isFocused = isFocused;
        }
    }
}
