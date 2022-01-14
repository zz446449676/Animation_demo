package com.example.animation_demo.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.animation_demo.Particle;
import com.example.animation_demo.util.ViewHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiresApi(api = Build.VERSION_CODES.N)
public class DimpleView extends View {
    private List<Particle> particleList= new ArrayList<Particle>();
    private Paint paint;
    private float centerX;
    private int screenHeight;
    private Context mContext;
    private float centerY;
    private float topY;
    private ValueAnimator animator;
    private Random random;
    private int radius;
    private Path path;
    private boolean isDimple = true;

    //PathMeasure这个类，它可以帮助我们得到在这个路径上任意一点的位置和方向
    private PathMeasure pathMeasure;     //路径，用于测量扩散圆某一处的X,Y值
    private float[] pos = new float[2];  //扩散圆上某一点的x,y
    private float[] tan = new float[2];  //扩散圆上某一点切线

    public DimpleView(Context context) {
        this(context, null);
    }


    public DimpleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private void init() {
        Log.d("zhang","初始化动画");
        radius = ViewHelper.dp2px(mContext, 175) / 2;
        random = new Random();
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setRepeatCount(-1);
        animator.setDuration(2000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener( v -> {
            updateParticle();//更新粒子位置
            invalidate();//重绘界面,加入了这句代码意味着需要重绘View，进而再一次调用onDraw
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint = new Paint();
        long startTime = System.currentTimeMillis(); // 获取开始时间
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);

//        onDraw里进行循环会导致超过16ms，出现掉帧卡顿情况
//        for (int i = 0; i < 5000; i++ ) {
//            Random random = new Random();
//            int randomX = random.nextInt((int) centerX * 2);
//            @SuppressLint("DrawAllocation") Particle particle = new Particle((float) randomX, centerY, 2f, 2f, 100);
//            canvas.drawCircle(particle.x, particle.y, particle.radius, paint);
//        }

        //正确做法是先将粒子添加进集合中
        particleList.forEach(item -> {
            paint.setAlpha(item.alpha);
            canvas.drawCircle(item.x, item.y, item.radius, paint);
        });

        long endTime = System.currentTimeMillis(); // 获取结束时间
        //Log.d("zhang","DimpleView onDraw : " + (endTime - startTime) + " ms");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = (float) w/2;
        centerY = (float) h/2;
        screenHeight = h;
        topY = (float) 1;

        //下雪特效
        //snow();

        //涟漪特效
        dimple();
        animator.start();
        Log.d("zhang","启动动画");
    }

    private void updateParticle() {
        particleList.forEach( item -> {
            if (isDimple) {
                if (item.offset >= item.maxOffset) {
                    item.offset = 0;
                    item.speed = (float) random.nextInt(8) + 3;
                    item.x = pos[0] + random.nextInt(10) - 3f;
                    item.y = pos[1] + random.nextInt(10) - 3f;
                }
                item.alpha= (int) ((1f - item.offset / item.maxOffset)  * 255f);
                item.x = (float) (centerX+ Math.cos(item.angle) * ((float)radius + item.offset));

                if (item.y > centerY) {
                    item.y = (float) (Math.sin(item.angle) * ((float)radius + item.offset) + centerY);
                } else {
                    item.y = (float) (centerY - Math.sin(item.angle) * ((float)radius + item.offset));
                }
                item.offset += item.speed;
                return;
            }

            item.y += item.speed;
            if (item.y - topY >= item.maxOffset) {
                item.y = topY;
                if (!isDimple) {
                    item.radius = (float) random.nextInt(4) +2 ;
                    item.speed = (float) random.nextInt(20) + 5;
                } else {
                    item.x = (float) random.nextInt((int) centerX * 2);
                    item.speed = (float) random.nextInt(10) + 5;
                }
            }
            item.alpha= (int) ((1f - item.y / item.maxOffset) * 255f);
            item.y += item.speed;
        });
    }

    private void snow() {
        isDimple = false;
        int nextX = 0;
        int nextY = 0;
        int speed = 0;
        int radius = 0;
        for (int i = 0; i < 500; i++) {
            //给粒子赋初始位置值
            nextX = random.nextInt((int) centerX * 2);
            nextY= (int) (random.nextInt(400) + topY);
            radius = random.nextInt(4) + 1;
            speed = random.nextInt(20) + 5;
            particleList.add(new Particle((float) nextX, nextY, (float)radius, (float)speed, 100, (float) screenHeight));
        }
    }

    private void dimple() {
        isDimple = true;
        float nextX = 0;
        float nextY = 0;
        int speed = 0;
        double angle = 0.0;

        //添加path
        path = new Path();
        pathMeasure = new PathMeasure();
        path.addCircle(centerX, centerY, (float)radius, Path.Direction.CCW);
        pathMeasure.setPath(path, false);

        for (int i = 0; i < 2000; i++) {
            //按比例去测量路径上每一点的值
            pathMeasure.getPosTan(i / 2000f * pathMeasure.getLength(), pos, tan);
            //反余弦函数可以得到角度值，是弧度
            angle = Math.acos(((pos[0] - centerX) / (float)radius));

            //给粒子赋初始位置值
            nextX = pos[0] + random.nextInt(150) - 3f;
            nextY = pos[1] + random.nextInt(150) - 3f;
            speed = random.nextInt(10) + 4;
            particleList.add(new Particle(nextX, nextY, 2f, (float)speed, 100, 0, angle));
        }
    }
}
