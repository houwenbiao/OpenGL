package com.jackhou.opengl;

import android.os.Bundle;

import java.security.spec.MGF1ParameterSpec;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private JGLSurfaceView mJGLSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mJGLSurfaceView = new JGLSurfaceView(this);
        setContentView(mJGLSurfaceView);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
