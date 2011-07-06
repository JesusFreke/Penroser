package org.jf.Penroser;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.AttributeSet;
import android.view.View;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class PenroserGLView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private int level = 0;
    private HalfRhombus left, right;

    public PenroserGLView(Context context) {
        super(context);
        init();
    }

    public PenroserGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        this.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                level++;
                requestRender();
            }
        });

        left = new SkinnyHalfRhombus(0, HalfRhombus.LEFT, 0, 0, 1, 0);
        right = new SkinnyHalfRhombus(0, HalfRhombus.RIGHT, 0, 0, 1, 0);

        this.setEGLConfigChooser(new EGLConfigChooser() {
            public EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eglDisplay) {
                int[] config = new int[] {
                        EGL10.EGL_SAMPLE_BUFFERS, 1,
                        EGL10.EGL_NONE
                };

                EGLConfig[] returnedConfig = new EGLConfig[1];
                int[] returnedConfigCount = new int[1];

                egl10.eglChooseConfig(eglDisplay, config, returnedConfig, 1, returnedConfigCount);

                if (returnedConfigCount[0] == 0) {
                    throw new RuntimeException("Multisample not supported");
                }

                return returnedConfig[0];
            }
        });

        this.setRenderer(this);
        this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		gl.glEnable(GL10.GL_LINE_SMOOTH);
        gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_FASTEST);
        /*gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);*/

        gl.glEnable(GL11.GL_VERTEX_ARRAY);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glLoadIdentity();
        GLU.gluOrtho2D(gl, -width / 200, width / 200, -height / 200, height / 200);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    public void onDrawFrame(GL10 gl) {
        if (gl instanceof GL11) {
            GL11 gl11 = (GL11)gl;
            left.draw(gl11, level);
            right.draw(gl11, level);
        }
    }
}
