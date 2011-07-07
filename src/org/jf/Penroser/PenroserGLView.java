package org.jf.Penroser;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import org.metalev.multitouch.controller.MultiTouchController;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

public class PenroserGLView extends GLSurfaceView implements GLSurfaceView.Renderer, MultiTouchController.MultiTouchObjectCanvas<Object> {
    private int level = 0;
    private HalfRhombus left, right;

    private MultiTouchController<Object> multiTouchController = new MultiTouchController<Object>(this);
    private GestureDetector gestureDetector;

    private float offsetX=0, offsetY=0;
    private float scale=100;
    private float angle=0;

    public PenroserGLView(Context context) {
        super(context);
        init();
    }

    public PenroserGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (e.getEventTime() - e.getDownTime() < 250) {
                    level++;
                    requestRender();
                    return true;
                }
                return false;
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
        gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);

        gl.glEnable(GL11.GL_VERTEX_ARRAY);

        if (gl instanceof GL11) {
            GL11 gl11 = (GL11) gl;
            FatHalfRhombus.onSurfaceCreated(gl11);
            SkinnyHalfRhombus.onSurfaceCreated(gl11);
        }
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glLoadIdentity();
        GLU.gluOrtho2D(gl, -width / 2, width / 2, -height / 2, height / 2);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    public void onDrawFrame(GL10 gl) {
        long start = System.nanoTime();
        if (gl instanceof GL11) {
            GL11 gl11 = (GL11)gl;

            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

            gl.glPushMatrix();

            gl.glTranslatef(offsetX, -offsetY, 0);
            gl.glRotatef((float)(angle * -180 / Math.PI), 0, 0, 1);
            gl.glScalef(scale, scale, 0);

            left.draw(gl11, level);
            right.draw(gl11, level);

            gl.glPopMatrix();
        }
        long end = System.nanoTime();

        Log.v("PenroserGLView", "Drawing took " + (end-start)/1000000000d + " seconds");
    }

    @Override
	public boolean onTouchEvent(MotionEvent event) {
        boolean res = multiTouchController.onTouchEvent(event);
        res |=  gestureDetector.onTouchEvent(event);
        return res;
	}

    public Object getDraggableObjectAtPoint(MultiTouchController.PointInfo touchPoint) {
        return "";
    }

    public void getPositionAndScale(Object obj, MultiTouchController.PositionAndScale objPosAndScaleOut) {
        objPosAndScaleOut.set(offsetX + getWidth()/2, offsetY + getHeight()/2, true, scale, false, 0, 0, true, angle);
    }

    public boolean setPositionAndScale(Object obj, MultiTouchController.PositionAndScale newObjPosAndScale, MultiTouchController.PointInfo touchPoint) {
        offsetX = newObjPosAndScale.getXOff() - getWidth()/2;
        offsetY = newObjPosAndScale.getYOff() - getHeight()/2;
        scale = newObjPosAndScale.getScale();
        angle = newObjPosAndScale.getAngle();

        this.requestRender();
        return true;
    }

    public void selectObject(Object obj, MultiTouchController.PointInfo touchPoint) {
    }
}
