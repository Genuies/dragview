package zx.com.genius.cn.pointview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import zx.com.genius.cn.pointview.listener.DragListener;
import zx.com.genius.cn.pointview.util.Utils;

/**
 * @author: Genius on 2018/11/9
 * @package: zx.com.genius.cn.pointview
 * @function:
 */
public class DragView extends AppCompatTextView {

    /**
     * 大圆半径
     */
    private float bigCircle = 0;

    /**
     * 小圆半径
     */
    private float smallCircle = 0;

    /**
     * 圆的颜色
     */
    private int backColor;

    /**
     * 是否根据字体宽度自动调整圆的大小
     */
    private boolean autoFit;

    /**
     * 圆画笔
     */
    private Paint mBackPaint;

    /**
     * 真正实现拖拽效果的对象
     */
    private DragPointView mPointView;
    private FrameLayout mParentLayout;
    private DragListener mDragListener;

    public DragView(Context context) {
        this(context, null);
    }

    public DragView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DragView);
        bigCircle = typedArray.getFloat(R.styleable.DragView_big_circle, Utils.dip2px(getContext(), 20));
        smallCircle = typedArray.getFloat(R.styleable.DragView_small_circle, Utils.dip2px(getContext(), 10));
        backColor = typedArray.getColor(R.styleable.DragView_back_color, getResources().getColor(R.color.colorPrimary));
        autoFit = typedArray.getBoolean(R.styleable.DragView_auto_fit, false);
        typedArray.recycle();
        mBackPaint = new Paint();
        mBackPaint.setColor(backColor);
        mBackPaint.setAntiAlias(true);
        setGravity(Gravity.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getMeasureValue(widthMeasureSpec);
        int height = getMeasureValue(heightMeasureSpec);
        bigCircle = Math.min(width, height)/2;
        if(smallCircle > bigCircle) {
            smallCircle = bigCircle * 0.6f;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(getWidth()/2, getHeight()/2, bigCircle, mBackPaint);
        super.onDraw(canvas);
    }

    /**
     * 根据测量大小重新获取控件大小
     * @param measureValue 测量尺寸
     * @return 控件尺寸
     */
    private int getMeasureValue(int measureValue) {
        int mode = MeasureSpec.getMode(measureValue);
        int size = MeasureSpec.getSize(measureValue);
        float value = bigCircle * 2;
        if (value < (getTextSize() + Utils.dip2px(getContext(), 6))) {
            value = getTextSize() + Utils.dip2px(getContext(), 6);
        }
        if (autoFit){
            float textWidth = getTextWidth() + Utils.dip2px(getContext(), 6);
            value = value < textWidth?textWidth:value;
        }
        switch (mode) {
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.EXACTLY:
                value = size;
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
            default:
                break;
        }
        return (int) value;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            addPointView();
            setVisibility(INVISIBLE);
        }
        mPointView.onTouchEvent(event);
        return true;
    }

    /**
     * 找到顶级父控件，decorview--linearlayout--framelayout
     * @param view 此控件
     * @return 顶级父控件
     */
    private FrameLayout findTopParent(View view) {
        ViewGroup group = null;
        while (view.getParent() != null) {
            ViewParent parent = view.getParent();
            try {
                group = (ViewGroup) parent;
                view = group;
            } catch (ClassCastException e) {
                break;
            }
        }
        if (group != null) {
            group = (ViewGroup) ((ViewGroup) group.getChildAt(0)).getChildAt(1);
        }
        return (FrameLayout) group;
    }

    /**
     * 设置滑动监听事件
     * @param listener 滑动监听事件
     */
    public void setDragListener(DragListener listener){
        mDragListener = listener;
    }

    /**
     * 将实现拖拽效果的控件添加到顶级父控件中
     */
    private void addPointView(){
        int[] lo = new int[2];
        getLocationInWindow(lo);
        PointF pointF = new PointF();
        pointF.x = lo[0] + getWidth()/2;
        pointF.y = lo[1] + getHeight()/2;
        if (mPointView == null) {
            mPointView = new DragPointView.Builer(getContext())
                    .setDragView(this)
                    .setBigCircle(bigCircle)
                    .setSmallCircle(smallCircle)
                    .setBackColor(backColor)
                    .setText((String) getText())
                    .setTextColor(getCurrentTextColor())
                    .setTextSize(getTextSize())
                    .setCenterPointF(pointF)
                    .builder();
        }
        if(mParentLayout == null) {
            mParentLayout = findTopParent(this);
        }else {
            mParentLayout.removeView(mPointView);
        }
        mPointView.setDragListener(mDragListener);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        mParentLayout.addView(mPointView, params);
    }

    /**
     * 获取字体宽度
     * @return 字体宽度
     */
    private float getTextWidth(){
        Paint paint = new Paint();
        paint.setTextSize(getTextSize());
        return paint.measureText((String) getText());
    }
}
