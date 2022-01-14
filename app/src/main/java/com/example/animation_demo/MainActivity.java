package com.example.animation_demo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.animation_demo.Activity.WaveDisplayActivity;
import com.example.animation_demo.util.BitmapUtil;
import com.example.animation_demo.util.ViewHelper;
import com.example.animation_demo.view.DimpleView;

public class MainActivity extends AppCompatActivity {
    private ImageView music_avatar;
    private DimpleView dimpleView;
    private ConstraintLayout root;
    private Bitmap backgroundBitmap, avatarBitmap;
    private ImageView imgBg;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = this.findViewById(R.id.root);
        imgBg = this.findViewById(R.id.imgBg);

        music_avatar = this.findViewById(R.id.music_avatar);
        dimpleView = new DimpleView(this);
        root.addView(dimpleView);
        loadImage();
    }

    private void loadImage() {
        //魔鬼细节优化，使用options.inSampleSize可以采样高效加载，这样可以瞬间显示图片，不会造成延迟，以后优化可以用上
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        if (music_avatar != null) {
            avatarBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.music, options);
            Glide.with(this).load(avatarBitmap).circleCrop().override(ViewHelper.dp2px(this, 176),ViewHelper.dp2px(this, 176)).into(music_avatar);
            ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(music_avatar, View.ROTATION, 0f, 360f);
            rotateAnimation.setDuration(6000);
            rotateAnimation.setRepeatCount(-1);
            rotateAnimation.setInterpolator(new LinearInterpolator());
            rotateAnimation.start();
        }
        //设置背景高斯模糊

        backgroundBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.music, options);
        Glide.with(this).load(BitmapUtil.doBlur(this, backgroundBitmap, 10)).into(imgBg);
    }

    public void waveDisplay (View v) {
        Intent intent = new Intent(this, WaveDisplayActivity.class);
        this.startActivity(intent);
    }
}