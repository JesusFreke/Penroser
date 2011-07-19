package org.jf.Penroser;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import net.rbgrn.android.glwallpaperservice.GLWallpaperService;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class PenroserLiveWallpaper extends GLWallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new PenroserGLEngine();
    }

    class PenroserGLEngine extends GLEngine implements PenroserGLRenderer.Callbacks {
        PenroserGLRenderer renderer = new PenroserGLRenderer(this);

        public PenroserGLEngine() {
            super();

            this.setEGLConfigChooser(new EGLConfigChooser() {
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
            setRenderMode(RENDERMODE_CONTINUOUSLY);
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            renderer.onTouchEvent(event);
        }
    }
}
