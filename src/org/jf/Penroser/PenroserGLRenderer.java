package org.jf.Penroser;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;
import android.view.MotionEvent;
import net.rbgrn.android.glwallpaperservice.GLWallpaperService;
import org.metalev.multitouch.controller.MultiTouchController;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class PenroserGLRenderer implements GLSurfaceView.Renderer, GLWallpaperService.Renderer, MultiTouchController.MultiTouchObjectCanvas<Object> {
    private static final String TAG = "PenroserGLRenderer";

    /**
     * Setting this to true causes the drawing logic to change, so that the tiles are kept in the same
     * position and a white box denoting the viewport is drawn and moved around instead
     */
    private static final boolean DRAW_VIEWPORT = false;
    private static final boolean LOG_DRAWTIMES = false;

    private static final float INITIAL_SCALE = 2118;

    private final Callbacks callbacks;

    private int level = 0;
    private HalfRhombus halfRhombus;

    private MultiTouchController<Object> multiTouchController = new MultiTouchController<Object>(this);
    private MomentumController momentumController = new MomentumController();

    private Matrix currentTransform = new Matrix();

    private long lastDraw = 0;

    private float[] viewport = new float[8];
    private RectF viewportEnvelope = new RectF();

    private int width, height;

    public PenroserGLRenderer(Callbacks callbacks) {
        this.callbacks = callbacks;

        Penroser.halfRhombusPool.initToLevels(0, 0);
        reset();
    }

    private void reset() {
        currentTransform.reset();
        currentTransform.postScale(INITIAL_SCALE, INITIAL_SCALE);

        int rhombusType = Penroser.random.nextInt(2);
        int rhombusSide = Penroser.random.nextInt(2);
        if (rhombusType == 0) {
            halfRhombus = new FatHalfRhombus(0, rhombusSide, 0, 0, 1, 0);
        } else {
            halfRhombus = new SkinnyHalfRhombus(0, rhombusSide, 0, 0, 1, 0);
        }

        momentumController.reset();
    }

    private Matrix invertedMatrix = new Matrix();
    private void calculateViewport() {
        if (!currentTransform.invert(invertedMatrix)) {
            throw new RuntimeException("Could not invert transformation matrix");
        }

        float halfWidth = width/2;
        float halfHeight = height/2;
        viewport[0] = -halfWidth;       viewport[1] = halfHeight;
        viewport[2] = halfWidth;        viewport[3] = halfHeight;
        viewport[4] = halfWidth;        viewport[5] = -halfHeight;
        viewport[6] = -halfWidth;       viewport[7] = -halfHeight;
        invertedMatrix.mapPoints(viewport);
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
        this.width = width;
        this.height = height;

        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluOrtho2D(gl, -width / 2, width / 2, height / 2, -height / 2);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
    }

    private float[] androidMatrixValues = new float[9];
    private float[] glMatrixValues = new float[16];
    private float[] velocities = new float[2];
    public void onDrawFrame(GL10 gl) {
        long start = System.nanoTime();
        int num=0;
        if (gl instanceof GL11) {
            GL11 gl11 = (GL11)gl;

            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

            gl.glPushMatrix();

            if (lastDraw != 0 && !momentumController.touchActive()) {
                float seconds = (start-lastDraw)/1E9f;
                momentumController.getVelocities(start, velocities);
                currentTransform.postTranslate(seconds*velocities[0], seconds*velocities[1]);
            }
            lastDraw = start;

            if (DRAW_VIEWPORT) {
                gl.glScalef(100, 100, 0);
            } else {
                currentTransform.getValues(androidMatrixValues);
                MatrixUtil.convertMatrix(androidMatrixValues, glMatrixValues);
                gl.glMultMatrixf(glMatrixValues, 0);
            }

            calculateViewport();

            int intersectingEdges = halfRhombus.getIntersectingEdges(viewport);
            while (intersectingEdges != 0) {
                if ((intersectingEdges & 1) != 0) {
                    int parentType = halfRhombus.getRandomParentType(0);
                    halfRhombus = halfRhombus.getParent(parentType);
                } else if ((intersectingEdges & 2) != 0) {
                    int parentType = halfRhombus.getRandomParentType(1);
                    halfRhombus = halfRhombus.getParent(parentType);
                } else {
                    int parentType = halfRhombus.getRandomParentType(2);
                    halfRhombus = halfRhombus.getParent(parentType);
                }

                intersectingEdges = halfRhombus.getIntersectingEdges(viewport);
                Log.v(TAG, "Generated parent: level = " + halfRhombus.level);
            }

            Penroser.halfRhombusPool.initToLevels(halfRhombus.level, 0);


            viewportEnvelope.left = MathUtil.min(viewport[0], viewport[2], viewport[4], viewport[6]);
            viewportEnvelope.top = MathUtil.min(viewport[1], viewport[3], viewport[5], viewport[7]);
            viewportEnvelope.right = MathUtil.max(viewport[0], viewport[2], viewport[4], viewport[6]);
            viewportEnvelope.bottom = MathUtil.max(viewport[1], viewport[3], viewport[5], viewport[7]);
            num += halfRhombus.draw(gl11, viewportEnvelope, level);
            if (num == 0) {
                Log.e(TAG, "Oops, the viewport somehow got out of the drawn region. Resetting viewport and tiling");
                reset();
            }

            if (DRAW_VIEWPORT) {
                drawViewport(gl11, viewport);
            }

            gl.glPopMatrix();
        }
        if (LOG_DRAWTIMES) {
            long end = System.nanoTime();
            Log.v("PenroserGLView", "Drawing took " + (end-start)/1E6d + " ms, with " + num + " leaf tiles drawn");
        }
    }

    private void drawViewport(GL11 gl, float[] viewport) {
        FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(viewport.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertexBuffer.put(viewport).position(0);

        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, vertexBuffer);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glColor4ub((byte)255, (byte)255, (byte)255, (byte)128);

        gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, viewport.length/2);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    public Object getDraggableObjectAtPoint(MultiTouchController.PointInfo touchPoint) {
        return "";
    }

    public void getPositionAndScale(Object obj, MultiTouchController.PositionAndScale objPosAndScaleOut) {
        //return the "default" values, so we get back the correct relative transformation values
        objPosAndScaleOut.set(0, 0, true, 1, false, 0, 0, true, 0);
    }

    public boolean setPositionAndScale(Object obj, MultiTouchController.PositionAndScale newObjPosAndScale, MultiTouchController.PointInfo touchPoint) {
        currentTransform.postTranslate(newObjPosAndScale.getXOff(), newObjPosAndScale.getYOff());
        currentTransform.postScale(newObjPosAndScale.getScale(), newObjPosAndScale.getScale());
        currentTransform.postRotate((float)(newObjPosAndScale.getAngle() * 180 / Math.PI));

        momentumController.addValues(touchPoint.getEventTime(), newObjPosAndScale.getXOff(),
                newObjPosAndScale.getYOff(), newObjPosAndScale.getAngle(), newObjPosAndScale.getScale());

        //reanchor the multitouch controller, so we always get relative transformation values
        multiTouchController.reanchor();

        callbacks.requestRender();
        return true;
    }

    public void selectObject(Object obj, MultiTouchController.PointInfo touchPoint) {
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction()==MotionEvent.ACTION_UP) {
            momentumController.touchReleased();
        } else if (event.getAction()==MotionEvent.ACTION_DOWN) {
            momentumController.reset();
        }
        return multiTouchController.onTouchEvent(event);
	}

    public interface Callbacks {
        void requestRender();
    }
}
