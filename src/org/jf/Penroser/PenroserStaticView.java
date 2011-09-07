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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.opengl.GLSurfaceView;
import android.os.*;
import android.os.Process;
import android.util.Log;
import android.view.View;
import org.jf.GLPixelBuffer.GLPixelBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class PenroserStaticView extends View {
    private Bitmap bitmap = null;
    private static final GLPixelBuffer glPixelBuffer;
    private static final PenroserGLRenderer renderer;

    private PenroserPreferences preferences;

    static {
        glPixelBuffer = new GLPixelBuffer();

        glPixelBuffer.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
            public EGLConfig chooseConfig(EGL10 egl10, EGLDisplay eglDisplay) {
                int[] config = new int[]{
                        EGL10.EGL_SAMPLE_BUFFERS, 1,
                        EGL10.EGL_NONE
                };

                EGLConfig[] returnedConfig = new EGLConfig[1];
                int[] returnedConfigCount = new int[1];

                egl10.eglChooseConfig(eglDisplay, config, returnedConfig, 1, returnedConfigCount);

                if (returnedConfigCount[0] == 0) {
                    config = new int[]{
                            EGL10.EGL_NONE
                    };
                    egl10.eglChooseConfig(eglDisplay, config, returnedConfig, 1, returnedConfigCount);
                    if (returnedConfigCount[0] == 0) {
                        throw new RuntimeException("Couldn't choose an opengl config");
                    }
                }

                return returnedConfig[0];
            }
        });

        renderer = new PenroserGLRenderer(null);
        glPixelBuffer.setRenderer(renderer);
    }

    public PenroserStaticView(Context context) {
        super(context);
    }

    public void setPreferences(PenroserPreferences preferences) {
        this.preferences = preferences;
    }

    public PenroserPreferences getPreferences() {
        PenroserPreferences prefs = new PenroserPreferences();
        prefs.setPreferences(this.preferences);
        return prefs;
    }

    private Handler handler = new Handler();

    private class GenerateBitmapTask extends AsyncTask<Integer, Void, Void> {
        protected Void doInBackground(Integer... dimensions) {
            int width = dimensions[0];
            int height = dimensions[1];
            synchronized (glPixelBuffer) {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY);
                if (bitmap == null || bitmap.getWidth() != width || bitmap.getHeight() != height) {
                    renderer.setPreferences(preferences);
                    renderer.reset();

                    long start = System.nanoTime();
                    bitmap = glPixelBuffer.draw(width, height);
                    long end = System.nanoTime();
                    Log.d("PenroserStaticView", "drawing took " + (end-start)/1E6f + "ms");


                    handler.post(new Runnable() {
                        public void run() {
                            invalidate();
                        }
                    });
                }

                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            }
            return null;
        }
     }


    public void prerender(int width, int height) {
        new GenerateBitmapTask().execute(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        Paint p = new Paint();

        if (bitmap == null || bitmap.getWidth() != width || bitmap.getHeight() != height) {
            new GenerateBitmapTask().execute(width, height);
        } else {
            canvas.drawBitmap(bitmap, 0, 0, p);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width=0, height=0;

        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                width = 0;
                break;
            case MeasureSpec.AT_MOST:
                width = (int)(MeasureSpec.getSize(widthMeasureSpec) * .8f);
                break;
            case MeasureSpec.EXACTLY:
                width = MeasureSpec.getSize(widthMeasureSpec);
                break;
        }

        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                height = 0;
                break;
            case MeasureSpec.AT_MOST:
                height = (int)(MeasureSpec.getSize(heightMeasureSpec) * .8f);
                break;
            case MeasureSpec.EXACTLY:
                height = MeasureSpec.getSize(heightMeasureSpec);
                break;
        }

        setMeasuredDimension(width, height);
    }
}
