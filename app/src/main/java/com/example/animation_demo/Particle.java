package com.example.animation_demo;

/**
 * 粒子动效
 */
public class Particle {
    //粒子坐标
    public float x,y;

    //粒子半径
    public float radius;

    //粒子速度
    public float speed;

    //粒子透明度
    public int alpha;

    //最大移动距离，一般来说是屏幕高度
    public float maxOffset;

    //当前移动距离
    public int offset;

    //粒子移动角度
    public double angle;

    public Particle(float x, float y, float radius, float speed, int alpha, float maxOffset) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.speed = speed;
        this.alpha = alpha;
        this.maxOffset = maxOffset;
    }

    public Particle(float x, float y, float radius, float speed, int alpha, int offset, double angle) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.speed = speed;
        this.alpha = alpha;
        this.offset = offset;
        this.angle = angle;
        this.maxOffset = 300f;
    }
}
