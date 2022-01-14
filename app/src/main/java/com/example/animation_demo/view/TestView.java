package com.example.animation_demo.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.view.animation.OvershootInterpolator;

public class TestView extends View {
    private Path path;
    private Paint paint;

    //屏幕中心坐标
    private Float centerX,centerY;

    //手指当前触摸位置
    private Float currentY = 0f, currentX = 0f;

    //突起的长和宽
    private Float dragWidth = 100f, dragHeight = 0f;

    //突起的上半部分贝塞尔曲线起始点和控制点
    private PointF point1 = new PointF(0f, 0F);//起始点
    private PointF point2 = new PointF(0f, 0F);//控制点
    private PointF point3 = new PointF(0f, 0F);//控制点

    private PointF point4 = new PointF(0f, 0F);//突起中点

    private PointF point5 = new PointF(0f, 0F);//控制点
    private PointF point6 = new PointF(0f, 0F);//控制点
    private PointF point7 = new PointF(0f, 0F);//结束点

    //放手后的回弹动画
    private ValueAnimator dragReboundAnimator = ValueAnimator.ofFloat(0f, 1f);
    private float reboundLength = 0f; //需要回弹的距离是多少
    private float dragReboundX = 0f; // 初始回弹地点的X值



    public TestView(Context context) {
        this(context, null);
    }

    public TestView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        path = new Path();
        paint = new Paint();
        paint.setColor(Color.RED);

        //此方法为空心描边
        //paint.setStyle(Paint.Style.STROKE);

        paint.setAntiAlias(true);

        dragReboundAnimator.setDuration(700);
        dragReboundAnimator.setInterpolator(new OvershootInterpolator(3f));
        dragReboundAnimator.addUpdateListener(v ->{
            dragWidth = dragReboundX - v.getAnimatedFraction() * reboundLength;
            invalidate();
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = (float)(w/2);
        centerY = (float)(h/2);
        currentY = centerY;
        currentX = 180f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        dragHeight = dragWidth * 1.5f;
        //下面分别是设置对应的七个点的坐标
        point1.x=100f;
        point1.y=currentY-dragHeight;

        point2.x=100f;
        point2.y=(currentY+point1.y)/2 + 30;

        point3.x=(100f+dragWidth)*0.94f;
        point3.y=(point1.y+currentY)/2;

        point4.x=100f+dragWidth;
        point4.y=currentY;

        point7.x=100f;
        point7.y=currentY+dragHeight;

        point5.x=(100f+dragWidth)*0.94f;
        point5.y=(currentY+point7.y)/2;

        point6.x=100f;
        point6.y=currentY+dragHeight/2 - 30;

        //注意：path是路径，lineTo 方法默认从（0，0）点开始绘制
        //第一步
        path.lineTo(100f,0f);
        //第二步
        path.lineTo(point1.x,point1.y);
        //第三步，画上半部分
        path.cubicTo(point2.x,point2.y,point3.x,point3.y,point4.x,point4.y);
        //第四步，画下半部分
        path.cubicTo(point5.x,point5.y,point6.x,point6.y,point7.x,point7.y);
        //第五步
        path.lineTo(100f,centerY*2);
        //第六步
        path.lineTo(0f,centerY*2);
        //第七步
        path.close();

        canvas.drawPath(path, paint); //画出路径
        path.reset(); //画完后删除之前的路径
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                currentY = event.getY();
                float X = event.getX();
                if (X > centerX) {
                    dragWidth = centerX - 30;
                } else {
                    dragWidth = X - 30;
                }
                invalidate(); //重绘View，其实也就是调用onDraw
                break;

            case MotionEvent.ACTION_UP:
                reboundLength = dragWidth -100;
                dragReboundX = dragWidth;
                dragReboundAnimator.start();
                invalidate();
        }
        return true;
    }
}
