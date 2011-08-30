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

import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import org.jf.GLWallpaper.GLWallpaperService;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class PenroserLiveWallpaper extends GLWallpaperService {

    private SharedPreferences preferences;

    public PenroserLiveWallpaper() {
        super();
    }

    @Override
    public Engine onCreateEngine() {
        preferences = getSharedPreferences("penroser_liveWallpaper_prefs", 0);
        return new PenroserGLEngine();
    }

    class PenroserGLEngine extends GLEngine implements PenroserGLRenderer.Callbacks {
        PenroserGLRenderer renderer = new PenroserGLRenderer(this);

        public PenroserGLEngine() {
            super();

            this.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
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

            setRenderer(renderer);
            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            renderer.onTouchEvent(event);
        }

        public int getColor(int rhombusType) {
            return PenroserApp.getColorForRhombusType(preferences, rhombusType);
        }
    }
}
