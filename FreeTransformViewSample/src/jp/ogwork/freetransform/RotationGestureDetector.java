package jp.ogwork.freetransform;

import android.view.MotionEvent;

public class RotationGestureDetector {
    private RotationGestureListener mListener;

    private float mX1, mY1, mX2, mY2;
    private int mPointerID1, mPointerID2;
    private float mFocusX, mFocusY;
    private float mAngle;

    public RotationGestureDetector(RotationGestureListener listener)
    {
        mListener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {
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
            mX1 = x;
            mY1 = y;
            break;
            
        case MotionEvent.ACTION_POINTER_DOWN:
            // 3本目の指以降は無視する
            if (mPointerID1 == -1)
            {
                mPointerID1 = pointerId;
                mX1 = x;
                mY1 = y;
                if (mPointerID2 != -1)
                {
                    int ptrIndex = event.findPointerIndex(mPointerID2);
                    mX2 = event.getX(ptrIndex);
                    mY2 = event.getY(ptrIndex);
                    mListener.onRotationBegin(this);
                }
            }
            else if (mPointerID2 == -1)
            {
                mPointerID2 = pointerId;
                mX2 = x;
                mY2 = y;
                if (mPointerID1 != -1)
                {
                    int ptrIndex = event.findPointerIndex(mPointerID1);
                    mX1 = event.getX(ptrIndex);
                    mY1 = event.getY(ptrIndex);
                    mListener.onRotationBegin(this);
                }
            }
            break;
            
        case MotionEvent.ACTION_MOVE:
            if(mPointerID1 != -1 && mPointerID2 != -1)
            {
                // 1
                float x1 = event.getX(event.findPointerIndex(mPointerID1));
                float y1 = event.getY(event.findPointerIndex(mPointerID1));
                float x2 = event.getX(event.findPointerIndex(mPointerID2));
                float y2 = event.getY(event.findPointerIndex(mPointerID2));

                // 2
                float angle1 = calcAngle(mX1, mY1, mX2, mY2);
                float angle2 = calcAngle(x1, y1, x2, y2);

                // 3
                float deltaAngle = angle2 - angle1;
                if (deltaAngle < -180.0f)
                {
                    deltaAngle += 360.0f;
                }
                else if (deltaAngle > 180.0f)
                {
                    deltaAngle -= 360.0f;
                }
                mAngle = deltaAngle;

                // 4
                mX2 = x2;
                mY2 = y2;
                mX1 = x1;
                mY1 = y1;

                // 5
                mFocusX = calcCenter(x1, x2);
                mFocusY = calcCenter(y1, y2);

                // 6
                mListener.onRotation(this);
            }
            break;
            
        case MotionEvent.ACTION_UP:
            mPointerID1 = -1;
            mPointerID2 = -1;
            break;
            
        case MotionEvent.ACTION_POINTER_UP:
            if (mPointerID1 == pointerId)
            {
                mPointerID1 = -1;
                if (mPointerID2 != -1)
                {
                    mListener.onRotationEnd(this);
                }
            }
            else if (mPointerID2 == pointerId)
            {
                mPointerID2 = -1;
                if (mPointerID1 != -1)
                {
                    mListener.onRotationEnd(this);
                }
            }
            break;
        }
        
        return true;
    }
    
    /**
     * 中点を計算する。
     */
    private float calcCenter(float p1, float p2)
    {
        return (p1 + p2) / 2;
    }
    
    /**
     * 線分から角度を計算する。
     */
    private float calcAngle(float x1, float y1, float x2, float y2)
    {
        return (float)Math.toDegrees(Math.atan2((y2 - y1), (x2 - x1)));
    }
    
    // 以下ゲッター
    public float getX1()
    {
        return mX1;
    }
    
    public float getY1()
    {
        return mY1;
    }
    
    public float getX2()
    {
        return mX2;
    }
    
    public float getY2()
    {
        return mY2;
    }
    
    public float getFocusX()
    {
        return mFocusX;
    }
    
    public float getFocusY()
    {
        return mFocusY;
    }
    
    public float getDeltaAngle()
    {
        return mAngle;
    }
}
