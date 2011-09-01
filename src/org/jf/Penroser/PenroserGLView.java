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
import android.content.res.TypedArray;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class PenroserGLView extends GLSurfaceView implements PenroserGLRenderer.Callbacks {
    private static final String TAG="PenroserGLView";

    private PenroserGLRenderer renderer;
    public final PenroserContext penroserContext;

    public PenroserGLView(Context context, String sharedPrefName) {
        super(context);
        penroserContext = new PenroserContext(context.getSharedPreferences(sharedPrefName, 0));
        init();
    }

    public PenroserGLView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PenroserGLView);
        String sharedPrefName = typedArray.getString(R.styleable.PenroserGLView_shared_pref_name);

        penroserContext = new PenroserContext(context.getSharedPreferences(sharedPrefName, 0));
        init();
    }

    private void init() {
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
                    config = new int[] {
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

        renderer = new PenroserGLRenderer(penroserContext, this);
        this.setRenderer(renderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
	public boolean onTouchEvent(MotionEvent event) {
        return renderer.onTouchEvent(event);
	}

    public void setColor(HalfRhombusType rhombusType, int color) {
        penroserContext.setRhombusColor(rhombusType, color);
    }

    public void reloadColors() {
        renderer.reloadColors();
    }
}
