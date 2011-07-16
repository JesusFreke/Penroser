package org.jf.Penroser;

import android.content.Context;
import android.graphics.Matrix;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import org.metalev.multitouch.controller.MultiTouchController;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class PenroserGLView extends GLSurfaceView implements GLSurfaceView.Renderer, MultiTouchController.MultiTouchObjectCanvas<Object> {
    private static final String TAG="PenroserGLView";

    /**
     * Setting this to true causes the drawing logic to change, so that the drawing surface is kept in the same
     * position and a white box denoting the viewport is drawn and moved around instead
     */
    private static final boolean DRAW_VIEWPORT = false;

    private int level = 0;
    private HalfRhombus halfRhombus;

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
                    int edge = Penroser.random.nextInt(2);
                    int parentType = halfRhombus.getRandomParentType(edge);
                    halfRhombus = halfRhombus.getParent(parentType);
                    Log.d(TAG, "Level " + halfRhombus.level);
                    requestRender();
                    return true;
                }
                return false;
            }
        });

        halfRhombus = new FatHalfRhombus(0, HalfRhombus.LEFT, 0, 0, 1, 0);

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

    private Geometry getViewport() {
        Matrix m = new Matrix();
        m.preTranslate(offsetX, -offsetY);
        m.preRotate((float)(angle * -180 / Math.PI));
        m.preScale(scale, scale);
        if (!m.invert(m)) {
            throw new RuntimeException("Could not invert transformation matrix");
        }

        float width = getWidth();
        float height = getHeight();
        float[] viewport = new float[] {
                -width/2, height/2,
                width/2, height/2,
                width/2, -height/2,
                -width/2, -height/2
        };

        m.mapPoints(viewport);
        LinearRing shell = Penroser.geometryFactory.createLinearRing(new Coordinate[] {
                new Coordinate(viewport[0], viewport[1]),
                new Coordinate(viewport[2], viewport[3]),
                new Coordinate(viewport[4], viewport[5]),
                new Coordinate(viewport[6], viewport[7]),
                new Coordinate(viewport[0], viewport[1]),
        });

        return Penroser.geometryFactory.createPolygon(shell, null);
    }

    public void onDrawFrame(GL10 gl) {
        long start = System.nanoTime();
        int num=0;
        if (gl instanceof GL11) {
            GL11 gl11 = (GL11)gl;

            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

            gl.glPushMatrix();

            if (DRAW_VIEWPORT) {
                gl.glScalef(100, 100, 0);
            } else {
                gl.glTranslatef(offsetX, -offsetY, 0);
                gl.glRotatef((float)(angle * -180 / Math.PI), 0, 0, 1);
                gl.glScalef(scale, scale, 0);
            }

            Geometry viewport = getViewport();

            num += halfRhombus.draw(gl11, viewport, level);

            if (DRAW_VIEWPORT) {
                drawViewport(gl11, viewport);
            }

            gl.glPopMatrix();
        }
        long end = System.nanoTime();

        Log.v("PenroserGLView", "Drawing took " + (end-start)/1E6d + " ms, with " + num + " leaf tiles drawn");
    }

    private void drawViewport(GL11 gl, Geometry viewport) {
        Coordinate[] coordinates = viewport.getCoordinates();

        float[] vertices = new float[] {
                (float)coordinates[0].x, (float)coordinates[0].y,
                (float)coordinates[1].x, (float)coordinates[1].y,
                (float)coordinates[2].x, (float)coordinates[2].y,
                (float)coordinates[0].x, (float)coordinates[0].y,
                (float)coordinates[2].x, (float)coordinates[2].y,
                (float)coordinates[3].x, (float)coordinates[3].y,
        };

        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(vertices).position(0);

        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glColor4ub((byte)255, (byte)255, (byte)255, (byte)128);

        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vertices.length/2);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
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
