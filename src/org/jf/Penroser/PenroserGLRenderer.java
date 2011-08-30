/*
 * [The "BSD licence"]
 * Copyright (c) 2011 Ben Gruver
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jf.Penroser;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;
import android.view.MotionEvent;
import org.metalev.multitouch.controller.MultiTouchController;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class PenroserGLRenderer implements GLSurfaceView.Renderer, MultiTouchController.MultiTouchObjectCanvas<Object> {
    private static final String TAG = "PenroserGLRenderer";

    /**
     * Setting this to true causes the drawing logic to change, so that the tiles are kept in the same
     * position and a white box denoting the viewport is drawn and moved around instead
     */
    private static final boolean DRAW_VIEWPORT = false;
    private static final boolean LOG_DRAWTIMES = false;

    private static final float INITIAL_SCALE = (float)(500 * Math.pow((Math.sqrt(5)+1)/2, GLContext.VBO_LEVEL-5));

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

    private GLContext glContext;

    public PenroserGLRenderer(Callbacks callbacks) {
        this.callbacks = callbacks;

        int[] rhombusColors = new int[] {
            ColorUtil.swapOrder(callbacks.getColor(HalfRhombusType.LEFT_SKINNY)),
            ColorUtil.swapOrder(callbacks.getColor(HalfRhombusType.RIGHT_SKINNY)),
            ColorUtil.swapOrder(callbacks.getColor(HalfRhombusType.LEFT_FAT)),
            ColorUtil.swapOrder(callbacks.getColor(HalfRhombusType.RIGHT_FAT))
        };

        glContext = new GLContext(rhombusColors);

        PenroserApp.halfRhombusPool.initToLevels(0, 0);
        reset();
    }

    private void reset() {
        currentTransform.reset();
        currentTransform.postScale(INITIAL_SCALE, INITIAL_SCALE);

        int rhombusType = PenroserApp.random.nextInt(2);
        int rhombusSide = PenroserApp.random.nextInt(2);
        if (rhombusType == 0) {
            halfRhombus = new FatHalfRhombus(glContext, 0, rhombusSide, 0, 0, 1, 0);
        } else {
            halfRhombus = new SkinnyHalfRhombus(glContext, 0, rhombusSide, 0, 0, 1, 0);
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

        if (gl instanceof GL11) {
            gl.glEnable(GL11.GL_VERTEX_ARRAY);

            glContext.onSurfaceCreated((GL11)gl);
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

            PenroserApp.halfRhombusPool.initToLevels(halfRhombus.level, 0);


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
        float scale = newObjPosAndScale.getScale();

        currentTransform.postTranslate(newObjPosAndScale.getXOff(), newObjPosAndScale.getYOff());
        if (scale != 1) {
            float newScale = (MatrixUtil.getMatrixScale(currentTransform) * scale) / INITIAL_SCALE;
            if (newScale >= .1 && newScale <= 25) {
                currentTransform.postScale(scale, scale);
            }
        }
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
        int getColor(HalfRhombusType rhombusType);
    }
}
