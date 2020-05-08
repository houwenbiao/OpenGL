package com.jackhou.opengl.textureview;

import android.os.Bundle;
import android.view.TextureView;

import com.jackhou.opengl.R;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Author: JackHou
 * Date: 2020/5/7.
 */
public class TextureViewActivity extends AppCompatActivity {
    private TextureView mTextureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_textureview);
        mTextureView = findViewById(R.id.cameraTextureView);
    }
}
