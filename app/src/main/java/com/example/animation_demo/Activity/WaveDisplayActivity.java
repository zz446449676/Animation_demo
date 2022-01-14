package com.example.animation_demo.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.animation_demo.R;
import com.example.animation_demo.view.TestView;

public class WaveDisplayActivity extends AppCompatActivity {
    private ImageView imgBg;
    private TestView testView;
    private ConstraintLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wave_display);
        root = this.findViewById(R.id.root);
        imgBg = this.findViewById(R.id.imgBg);
        Glide.with(this).load(R.drawable.music).into(imgBg);
        testView = new TestView(this);

        root.addView(testView);
    }
}