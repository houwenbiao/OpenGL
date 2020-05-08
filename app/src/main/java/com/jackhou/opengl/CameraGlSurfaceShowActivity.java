package com.jackhou.opengl;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.jackhou.opengl.utils.AssetsUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Author: JackHou
 * Date: 2020/5/7.
 */
public class CameraGlSurfaceShowActivity extends AppCompatActivity implements SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = "JackHou";
    private GLSurfaceView mGLSurfaceView;
    private Button mButton;
    private MyRenderer mMyRenderer;
    public SurfaceTexture mSurfaceTexture;
    public static Camera mCamera;
    private int mCameraStatus = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glsurfaceview);
        mButton = findViewById(R.id.btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PropertyValuesHolder valuesHolder1 = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.5f, 1.0f);
                PropertyValuesHolder valuesHolder4 = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.5f, 1.0f);
                PropertyValuesHolder valuesHolder5 = PropertyValuesHolder.ofFloat("rotationY", 0.0f, 360.0f, 0.0F);
                ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mGLSurfaceView, valuesHolder1, valuesHolder4, valuesHolder5);
                objectAnimator.setDuration(3000).start();
            }
        });
        mGLSurfaceView = findViewById(R.id.glSurfaceView);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mMyRenderer = new MyRenderer();
        mGLSurfaceView.setRenderer(mMyRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);//有数据的时候渲染
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
//        Log.i(TAG, "onFrameAvailable");
        mGLSurfaceView.requestRender();
    }

    public class MyRenderer implements GLSurfaceView.Renderer {

        private FloatBuffer mPosBuffer;
        private FloatBuffer mTexBuffer;
        private float[] mPosCoordinate = {
                -1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, -1.0f,
                1.0f, 1.0f
        };
        private float[] mTexCoordinateBackRight = {1, 1, 0, 1, 1, 0, 0, 0};//顺时针转90并沿Y轴翻转  后摄像头正确，前摄像头上下颠倒
        private float[] mTexCoordinateFrontRight = {0, 1, 1, 1, 0, 0, 1, 0};//顺时针旋转90  后摄像头上下颠倒了，前摄像头正确

        public int mProgram;

        private int uPosHandle;
        private int aTexHandle;
        private int mMVPMatrixHandle;
        private float[] mProjectMatrix = new float[16];
        private float[] mCameraMatrix = new float[16];
        private float[] mMVPMatrix = new float[16];
        private float[] mTempMatrix = new float[16];

        public MyRenderer() {
            Matrix.setIdentityM(mProjectMatrix, 0);
            Matrix.setIdentityM(mCameraMatrix, 0);
            Matrix.setIdentityM(mMVPMatrix, 0);
            Matrix.setIdentityM(mTempMatrix, 0);
        }

        private void createProgram() {
            String vertexSource = AssetsUtils.read(CameraGlSurfaceShowActivity.this,
                    "vertex_texture.glsl");
            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSource);
            String fragmentSource = AssetsUtils.read(CameraGlSurfaceShowActivity.this,
                    "fragment_texture.glsl");
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSource);

            // 创建空的OpenGL ES程序, 并返回新创建程序对象的ID引用
            mProgram = GLES20.glCreateProgram();

            // 把之前编译的着色器附加到程序对象上
            // 添加顶点着色器到程序中
            GLES20.glAttachShader(mProgram, vertexShader);
            // 添加片段着色器到程序中
            GLES20.glAttachShader(mProgram, fragmentShader);
            // 创建OpenGL ES程序可执行文件
            GLES20.glLinkProgram(mProgram);
            // 释放shader资源
            GLES20.glDeleteShader(vertexShader);
            GLES20.glDeleteShader(fragmentShader);

        }

        private FloatBuffer convertToFloatBuffer(float[] buffer) {
            FloatBuffer fb = ByteBuffer.allocateDirect(buffer.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            fb.put(buffer);
            fb.position(0);
            return fb;
        }

        private int loadShader(int type, String shaderCode) {
            int shader = GLES20.glCreateShader(type);
            // 添加上面编写的着色器代码并编译它
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);
            return shader;
        }


        //添加程序到ES环境中
        private void activeProgram() {
            // 激活这个程序对象, 将程序添加到OpenGL ES环境
            //在glUseProgram函数调用之后，每个着色器调用和渲染调用都会使用这个程序对象（也就是之前写的着色器)了
            GLES20.glUseProgram(mProgram);
            mSurfaceTexture.setOnFrameAvailableListener(CameraGlSurfaceShowActivity.this);
            // 获取顶点着色器的位置的句柄
            uPosHandle = GLES20.glGetAttribLocation(mProgram, "position");
            aTexHandle = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "textureTransform");
            mPosBuffer = convertToFloatBuffer(mPosCoordinate);
            if (mCameraStatus == 0) {
                mTexBuffer = convertToFloatBuffer(mTexCoordinateBackRight);
            } else {
                mTexBuffer = convertToFloatBuffer(mTexCoordinateFrontRight);
            }

            //使用glVertexAttribPointer函数告诉OpenGL该如何解析顶点数据, 顶点缓冲对象与顶点属性链接
            //glVertexAttribPointer函数的参数非常多:
            //第一个参数指定我们要配置的顶点属性。还记得我们在顶点着色器中使用layout(location = 0)定义了position顶点属性的位置值(Location)吗？它可以把顶点属性的位置值设置为0。因为我们希望把数据传递到这一个顶点属性中，所以这里我们传入0。
            //第二个参数指定顶点属性的大小。顶点属性是一个vec3，它由3个值组成，所以大小是3。
            //第三个参数指定数据的类型，这里是GL_FLOAT(GLSL中vec*都是由浮点数值组成的)。
            //第四个参数定义我们是否希望数据被标准化(Normalize)。如果我们设置为GL_TRUE，所有数据都会被映射到0（对于有符号型signed数据是-1）到1之间。我们把它设置为GL_FALSE。
            //第五个参数叫做步长(Stride)，它告诉我们在连续的顶点属性组之间的间隔。由于下个组位置数据在3个GLfloat之后，我们把步长设置为3 * sizeof(GLfloat)。
            //要注意的是由于我们知道这个数组是紧密排列的（在两个顶点属性之间没有空隙）我们也可以设置为0来让OpenGL决定具体步长是多少（只有当数值是紧密排列时才可用）。
            //一旦我们有更多的顶点属性，我们就必须更小心地定义每个顶点属性之间的间隔，我们在后面会看到更多的例子(译注: 这个参数的意思简单说就是从这个属性第二次出现的地方到整个数组0位置之间有多少字节)。
            //最后一个参数的类型是GLvoid*，所以需要我们进行这个奇怪的强制类型转换。它表示位置数据在缓冲中起始位置的偏移量(Offset)。由于位置数据在数组的开头，所以这里是0。我们会在后面详细解释这个参数。
            GLES20.glVertexAttribPointer(uPosHandle, 2, GLES20.GL_FLOAT, false, 0, mPosBuffer);
            GLES20.glVertexAttribPointer(aTexHandle, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);

            // 启用顶点位置的句柄
            GLES20.glEnableVertexAttribArray(uPosHandle);
            GLES20.glEnableVertexAttribArray(aTexHandle);
        }

        //纹理是使用ID引用的
        int createOESTextureObject() {
            int[] tex = new int[1];
            //生成一个纹理, 首先需要输入生成纹理的数量，然后把它们储存在第二个参数的tex数组中
            GLES20.glGenTextures(1, tex, 0);
            //将此纹理绑定到外部纹理上
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, tex[0]);
            //设置纹理过滤参数
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                    GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
            GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
            return tex[0];
        }


        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.i(TAG, "onSurfaceCreated");
            GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            mSurfaceTexture = new SurfaceTexture(createOESTextureObject());
            createProgram();
            mCamera = Camera.open(mCameraStatus);
            try {
                mCamera.setPreviewTexture(mSurfaceTexture);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
            activeProgram();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            Log.i(TAG, "onSurfaceChanged");
            GLES20.glViewport(0, 0, width, height);
            Matrix.scaleM(mMVPMatrix, 0, 1, -1, 1);
            float ratio = (float) width / height;
            Matrix.orthoM(mProjectMatrix, 0, -1, 1, -ratio, ratio, 1, 7);// 3和7代表远近视点与眼睛的距离，非坐标点
            Matrix.setLookAtM(mCameraMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);// 3代表眼睛的坐标点
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mCameraMatrix, 0);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            if (mSurfaceTexture != null) {
                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
                mSurfaceTexture.updateTexImage();
                GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
                //绘制
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mPosCoordinate.length / 2);
            }
        }
    }
}
