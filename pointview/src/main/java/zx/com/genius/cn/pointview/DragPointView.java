package zx.com.genius.cn.pointview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import zx.com.genius.cn.pointview.factory.FallingParticleFactory;
import zx.com.genius.cn.pointview.listener.DragListener;
import zx.com.genius.cn.pointview.util.Utils;

/**
 * @author: Genius on 2018/11/9
 * @package: zx.com.genius.cn.pointview
 * @function:
 */
public class DragPointView extends View {

    private float bigCircle;
    private float smallCircle;
    private String text;
    private PointF mDragPointF;
    private PointF mStickPointF;
    private float tempCircle;
    private float maxDistance;
    private float smallMinCircle;

    private Paint mPathPaint;
    private Paint mTextPaint;

    private boolean beyondDis;

    private DragView mDragView;
    private DragListener mDragListener;

    private int statusHeight;

    private ExplosionListener mListener = new ExplosionListener() {
        @Override
        public void explosionEnd() {
            setVisibility(GONE);
            mDragView.setVisibility(INVISIBLE);
            mDragPointF.x = mStickPointF.x;
            mDragPointF.y = mStickPointF.y;
            if(mDragListener != null){
                mDragListener.disappear();
            }
        }
    };

    public DragPointView(Context context) {
        super(context);
    }

    public DragPointView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DragPointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        maxDistance = Utils.dip2px(getContext(), 100);
        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        statusHeight = getStatusBarHeight(getContext());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 画圆及计算坐标时，要减去状态栏的高度，因为当初设置坐标时包含了状态栏的高度
         */
        canvas.drawCircle(mDragPointF.x, mDragPointF.y - statusHeight, bigCircle, mPathPaint);
        if (!beyondDis) {
            canvas.drawCircle(mStickPointF.x, mStickPointF.y - statusHeight, tempCircle, mPathPaint);
            drawLink(canvas);
        }
        Paint.FontMetrics metrics = mTextPaint.getFontMetrics();
        float textHeight = (-metrics.ascent - metrics.descent)/2;
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text, mDragPointF.x, mDragPointF.y + textHeight - statusHeight, mTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setVisibility(VISIBLE);
                beyondDis = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if(Utils.evaluateTwoPointDis(mDragPointF, mStickPointF) > maxDistance){
                    beyondDis = true;
                    updateCenter(event.getRawX(), event.getRawY());
                    return false;
                }
                updateCenter(event.getRawX(), event.getRawY());
                break;
            case MotionEvent.ACTION_UP:
                handleUp();
                break;
            default:
                beyondDis = false;
                break;
        }
        return true;
    }

    /**
     * 画两个圆之间的贝塞尔曲线
     * @param canvas 画布
     */
    private void drawLink(Canvas canvas) {
        Path path = new Path();
        calculationSmallCircle();
        float xdiff = mDragPointF.x - mStickPointF.x;
        Double d = null;
        if (xdiff != 0) {
            d = (double) ((mDragPointF.y - mStickPointF.y) / xdiff);
        }
        PointF[] dragPointFs = calculationBesselPoints(mDragPointF, bigCircle, d);
        PointF[] stickPointFs = calculationBesselPoints(mStickPointF, tempCircle, d);
        PointF controlPoint = controlPoint(mDragPointF, mStickPointF, 0.618f);
        path.moveTo(stickPointFs[0].x, stickPointFs[0].y);
        path.quadTo(controlPoint.x, controlPoint.y, dragPointFs[0].x, dragPointFs[0].y );
        path.lineTo(dragPointFs[1].x, dragPointFs[1].y);
        path.quadTo(controlPoint.x, controlPoint.y, stickPointFs[1].x, stickPointFs[1].y);
        path.close();
        canvas.drawPath(path, mPathPaint);
    }

    /**
     * 根据两个圆心的距离计算小圆的半径
     */
    private void calculationSmallCircle() {
        float distance = (float) Math.sqrt(Math.pow(mDragPointF.x - mStickPointF.x, 2) + Math.pow(mDragPointF.y - mStickPointF.y, 2));
        distance = Math.min(distance, maxDistance);
        float per = 0.2f + 0.8f * distance / maxDistance;
        tempCircle = Math.max(evaluateValue(smallCircle, smallMinCircle, per), smallMinCircle);
    }

    /**
     * 计算贝塞尔曲线的终点和起点
     * @param pointF 圆心的坐标
     * @param circle 半径
     * @param d 比例
     * @return 贝塞尔曲线的终点或起点
     */
    private PointF[] calculationBesselPoints(PointF pointF, float circle, Double d) {
        PointF[] pointFS = new PointF[2];
        double xOffset;
        double yOffset;
        if (d != null) {
            double angle = Math.atan(d);
            xOffset = circle * Math.sin(angle);
            yOffset = circle * Math.cos(angle);
        } else {
            xOffset = circle;
            yOffset = 0;
        }
        pointFS[0] = new PointF((float) (pointF.x + xOffset), (float) (pointF.y - statusHeight - yOffset));
        pointFS[1] = new PointF((float) (pointF.x - xOffset), (float) (pointF.y - statusHeight + yOffset));
        return pointFS;
    }

    /**
     * 极端贝塞尔曲线的控制点
     * @param startPoint 贝塞尔曲线的起点
     * @param endPoint 贝塞尔曲线的终点
     * @param per 比例
     * @return 控制点
     */
    private PointF controlPoint(PointF startPoint, PointF endPoint, float per) {
        float newX = evaluateValue(startPoint.x, endPoint.x, per);
        float newY = evaluateValue(startPoint.y - statusHeight, endPoint.y - statusHeight, per);
        return new PointF(newX, newY);
    }

    private float evaluateValue(float start, float end, float per) {
        return start + (end - start) * per;
    }

    /**
     * 更新大圆的圆心坐标
     * @param x 坐标x
     * @param y 坐标y
     */
    private void updateCenter(float x, float y){
        mDragPointF.x = x;
        mDragPointF.y = y;
        invalidate();
    }

    /**
     * 处理手抬起事件
     * 如果移动过程中，大圆和小圆的距离超出了最大距离，则出现爆炸效果，消失
     * 如果没有，则回弹
     */
    private void handleUp(){
        if(!beyondDis){
            upAnima();
        }else {
            ExplosionField field = new ExplosionField(getContext(), new FallingParticleFactory());
            field.setExplosionListener(mListener);
            field.explode(this);
        }
    }

    /**
     * 大圆回弹效果
     */
    private void upAnima(){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.0f, -0.6f, 0.4f, -0.2f, 0);
        valueAnimator.setInterpolator(new AccelerateInterpolator(2.0f));
        final PointF startPoint = new PointF(mStickPointF.x, mStickPointF.y);
        final PointF endPoint = new PointF(mDragPointF.x, mDragPointF.y);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float per = (float) valueAnimator.getAnimatedValue();
                PointF pointF = calcutationAnimPoints(startPoint, endPoint, per);
                updateCenter(pointF.x, pointF.y);
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(GONE);
                mDragView.setVisibility(VISIBLE);
                mDragPointF.x = mStickPointF.x;
                mDragPointF.y = mStickPointF.y;
                if(mDragListener != null){
                    mDragListener.reset();
                }
            }
        });
        if(Utils.evaluateTwoPointDis(mDragPointF, mStickPointF) < 10){
        }else {
            valueAnimator.setDuration(300);
        }
        valueAnimator.start();
    }

    /**
     * 计算回弹动画时，大圆坐标
     * @param startPoint 起点
     * @param endPoint 终点
     * @param per 比例
     * @return 坐标
     */
    private PointF calcutationAnimPoints(PointF startPoint, PointF endPoint, float per) {
        float newX = evaluateValue(startPoint.x, endPoint.x, per);
        float newY = evaluateValue(startPoint.y, endPoint.y, per);
        return new PointF(newX, newY);
    }

    /**
     * 设置监听事件
     * @param listener 监听事件
     */
    public void setDragListener(DragListener listener){
        mDragListener = listener;
    }

    /**
     * 获取状态栏高度
     * @param context 上下午环境
     * @return 状态栏高度
     */
    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * DragPointView的构造器
     */
    public static class Builer{

        private DragPointView mPointView;

        public Builer(Context context){
            mPointView = new DragPointView(context);
            mPointView.init();
        }

        public Builer setDragView(DragView dragView){
            mPointView.mDragView = dragView;
            return this;
        }

        public Builer setBigCircle(float bigCircle){
            mPointView.bigCircle = bigCircle;
            mPointView.smallMinCircle = bigCircle * 0.3f;
            return this;
        }

        public Builer setSmallCircle(float smallCircle){
            mPointView.smallCircle = smallCircle;
            mPointView.tempCircle = smallCircle;
            return this;
        }

        public Builer setText(String text){
            mPointView.text = text;
            return this;
        }

        public Builer setCenterPointF(PointF centerPointF){
            mPointView.mDragPointF = new PointF(centerPointF.x, centerPointF.y);
            mPointView.mStickPointF = new PointF(centerPointF.x, centerPointF.y);
            return this;
        }

        public Builer setTextColor(int textColor){
            mPointView.mTextPaint.setColor(textColor);
            return this;
        }

        public Builer setBackColor(int backColor){
            mPointView.mPathPaint.setColor(backColor);
            return this;
        }

        public Builer setTextSize(float textSize){
            mPointView.mTextPaint.setTextSize(textSize);
            return this;
        }

        public DragPointView builder(){
            return mPointView;
        }
    }
}
