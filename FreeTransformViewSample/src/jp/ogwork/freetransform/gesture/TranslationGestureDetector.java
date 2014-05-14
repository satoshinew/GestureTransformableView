package jp.ogwork.freetransform.gesture;

import android.view.MotionEvent;
import android.view.View;

public class TranslationGestureDetector {
    private TranslationGestureListener mListener;
    private float mX, mY;
    private float mDeltaX, mDeltaY;
    private int mPointerID1, mPointerID2;
    
    public TranslationGestureDetector(TranslationGestureListener listener)
    {
        mListener = listener;
    }

    /**
     * タッチ処理
     */
    public boolean onTouch(View v, MotionEvent event) {
        int eventAction = event.getActionMasked();
        int pointerIndex = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIndex);
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);
        
        switch (eventAction) {
        case MotionEvent.ACTION_DOWN:
            // 最初の指の設定
            mPointerID1 = pointerId;
            mPointerID2 = -1;
            mX = x;
            mY = y;

            mListener.onTranslationBegin(this);
            break;
            
        case MotionEvent.ACTION_POINTER_DOWN:
            // 3本目の指以降は無視する
            if (mPointerID2 == -1)
            {
                mPointerID2 = pointerId;
            }
            break;
            
        case MotionEvent.ACTION_POINTER_UP:
            if (mPointerID1 == pointerId)
            {
                mPointerID1 = -1;
                mX = x;
                mY = y;
                mListener.onTranslationEnd(this);
            }
            else if (mPointerID2 == pointerId)
            {
                mPointerID2 = -1;
            }
            break;

        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            if (mPointerID1 != -1)
            {
                mX = x;
                mY = y;
                mListener.onTranslationEnd(this);
            }
            mPointerID1 = -1;
            mPointerID2 = -1;
            break;
            
        case MotionEvent.ACTION_MOVE:
            // 指の座標の更新
            if (mPointerID1 >= 0)
            {
                int ptrIndex = event.findPointerIndex(mPointerID1);
                float px = event.getX(ptrIndex);
                float py = event.getY(ptrIndex);
                mDeltaX = px - mX;
                mDeltaY = py - mY;
                mX = px;
                mY = py;
            }
            
            // 1本目の指だけが動いている時のみ処理する
            if (mPointerID1 >= 0 && mPointerID2 == -1)
            {
                mListener.onTranslation(this);
            }
            break;
        }
        
        return true;
    }
    
    public float getX()
    {
        return mX;
    }

    public float getY()
    {
        return mY;
    }
    
    public float getDeltaX()
    {
        return mDeltaX;
    }
    
    public float getDeltaY()
    {
        return mDeltaY;
    }
}
