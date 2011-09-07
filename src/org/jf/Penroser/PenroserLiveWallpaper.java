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

import android.content.*;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;
import org.jf.GLWallpaper.GLWallpaperService;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;
import java.lang.ref.WeakReference;

public class PenroserLiveWallpaper extends GLWallpaperService {
    private static final String TAG = "PenroserLiveWallpaper";
    public static final String PREFERENCE_NAME = "current_pref_wallpaper";

    public static WeakReference<PenroserLiveWallpaper> theService = null;
    private static WeakReference<PenroserGLEngine> theEngine = null;

    private SharedPreferences sharedPreferences;
    private PenroserPreferences preferences;

    public PenroserLiveWallpaper() {
        super();
    }

    public PenroserPreferences getPreferences() {
        PenroserGLEngine engine = null;
        if (theEngine != null) {
            engine = theEngine.get();
        }
        if (engine == null) {
            return new PenroserPreferences(sharedPreferences, PenroserLiveWallpaper.PREFERENCE_NAME);
        }
        return engine.getPreferences();
    }

    @Override
    public void onCreate() {
        sharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE);
        theService = new WeakReference<PenroserLiveWallpaper>(this);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (theService != null) {
            theService.clear();
            theService = null;
        }
        if (theEngine != null) {
            theEngine.clear();
            theEngine = null;
        }
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
        preferences = new PenroserPreferences(sharedPreferences, PREFERENCE_NAME);
        PenroserGLEngine engine = new PenroserGLEngine();
        theEngine = new WeakReference<PenroserGLEngine>(engine);
        return engine;
    }


    class PenroserGLEngine extends GLEngine implements PenroserGLRenderer.Callbacks {
        private PenroserGLRenderer renderer = new PenroserGLRenderer(this);

        public PenroserGLEngine() {
            super();

            renderer.setPreferences(preferences);

            this.setTouchEventsEnabled(true);

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

        public PenroserPreferences getPreferences() {
            return renderer.getPreferences();
        }

        @Override
        public void onDestroy() {
            if (theEngine != null) {
                theEngine.clear();
                theEngine = null;
            }
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (!visible) {
                preferences.setScale(renderer.getScale());
                preferences.saveTo(sharedPreferences, PenroserLiveWallpaper.PREFERENCE_NAME);
            } else {
                preferences.setPreferences(new PenroserPreferences(sharedPreferences, PenroserLiveWallpaper.PREFERENCE_NAME));
                renderer.setPreferences(preferences);
            }
            super.onVisibilityChanged(visible);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            renderer.onTouchEvent(event);
        }
    }
}
