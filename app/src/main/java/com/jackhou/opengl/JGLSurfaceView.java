package com.jackhou.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Author: JackHou
 * Date: 2020/5/7.
 */
public class JGLSurfaceView extends GLSurfaceView {
    private GLRenderer mGLRenderer;

    public JGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        mGLRenderer = new GLRenderer();
        setRenderer(mGLRenderer);
    }

    public JGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        mGLRenderer = new GLRenderer();
        setRenderer(mGLRenderer);
    }
}
