package jp.ogwork.freetransform.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class GestureFrame extends FrameLayout {

    GestureImageViewInFrame imageView;

    public GestureFrame(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public GestureFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GestureFrame(Context context) {
        super(context);
        init(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect rect = new Rect();
        Paint paintBlue = new Paint();
        paintBlue.setColor(Color.BLUE);
        paintBlue.setStyle(Style.STROKE);
        paintBlue.setStrokeWidth(10.f);

        getDrawingRect(rect);
        canvas.drawRect(rect, paintBlue);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setParentViewWidthHeight(int width, int height) {
        imageView.setParentViewWidthHeight(width, height);
    }

    private void init(Context context) {
        FrameLayout.LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        imageView = new GestureImageViewInFrame(context);
        addView(imageView, params);
    }
}
