/**
 * Created with JackHou
 * Date: 2020/5/7
 * Time: 19:42
 * Description:
 */

/**
 * Author: JackHou
 * Date: 2020/5/7.
 */

#extension GL_OES_EGL_image_external : require
precision mediump float;
uniform samplerExternalOES videoTex;
varying vec2 textureCoordinate;

void main() {
    vec4 tc = texture2D(videoTex, textureCoordinate);
    //float color = tc.r * 0.3 + tc.g * 0.59 + tc.b * 0.11;//这里进行的颜色变换处理，传说中的黑白滤镜。
    //gl_FragColor = vec4(color,color,color,1.0);
    gl_FragColor = vec4(tc.r,tc.g,tc.b,1.0);//彩色
}