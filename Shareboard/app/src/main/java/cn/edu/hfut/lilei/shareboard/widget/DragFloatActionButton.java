package cn.edu.hfut.lilei.shareboard.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;

import cn.edu.hfut.lilei.shareboard.utils.ScreenUtil;

import static cn.edu.hfut.lilei.shareboard.utils.MyAppUtil.showLog;

public class DragFloatActionButton extends android.support.design.widget.FloatingActionButton {

    private int screenWidth;
    private int screenHeight;
    private int screenWidthHalf;
    private int statusHeight;

    public DragFloatActionButton(Context context) {
        super(context);
        init();
    }

    public DragFloatActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragFloatActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        screenWidth = ScreenUtil.getScreenWidth(getContext());
        screenWidthHalf = screenWidth / 2;
        screenHeight = ScreenUtil.getHeight((Activity) getContext());
        statusHeight = ScreenUtil.getStatusBarHeight(getContext());
    }

    public void setTitleBarSize(int height) {
        titleBarSize = height;
    }

    public void setBottomBarSize(int height) {
        bottomBarSize = height;
    }

    private int titleBarSize;
    private int bottomBarSize;
    private int lastX;
    private int lastY;

    private boolean isDrag;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                showLog("MotionEvent.ACTION_DOWN,rawY" + rawY);
                isDrag = false;
                getParent().requestDisallowInterceptTouchEvent(true);
                lastX = rawX;
                lastY = rawY;
                break;
            case MotionEvent.ACTION_MOVE:
                isDrag = true;
                //计算手指移动了多少
                int dx = rawX - lastX;
                int dy = rawY - lastY;
                //这里修复一些华为手机无法触发点击事件的问题
                int distance = (int) Math.sqrt(dx * dx + dy * dy);
                if (distance == 0) {
                    isDrag = false;
                    break;
                }
                float x = getX() + dx;
                float y = getY() + dy;
                //检测是否到达边缘 左上右下
                x = x < 0 ? 0 : x > screenWidth - getWidth() ? screenWidth - getWidth() : x;
                y = y < statusHeight + titleBarSize ? statusHeight + titleBarSize :
                        y + getHeight() > screenHeight - bottomBarSize ?
                                screenHeight - bottomBarSize - getHeight() : y;
                setX(x);
                setY(y);
                lastX = rawX;
                lastY = rawY;
                //Log.i("getX="+getX()+";getY="+getY()+";screenHeight="+screenHeight);
                break;
            case MotionEvent.ACTION_UP:
                if (isDrag) {
                    //恢复按压效果
                    setPressed(false);
//                    Log.i("getX="+getX()+"；screenWidthHalf="+screenWidthHalf);
//                    if (rawX >= screenWidthHalf) {
                    animate().setInterpolator(new DecelerateInterpolator())
                            .setDuration(500)
                            .xBy(screenWidth - getWidth() - getX())
                            .start();
//                    }
//                    else {
//                        ObjectAnimator oa = ObjectAnimator.ofFloat(this, "x", getX(), 0);
//                        oa.setInterpolator(new DecelerateInterpolator());
//                        oa.setDuration(500);
//                        oa.start();
//                    }
                }
                break;
        }
        //如果是拖拽则消耗事件，否则正常传递即可。
        return isDrag || super.onTouchEvent(event);
    }
}
