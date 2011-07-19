package org.jf.Penroser;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

public class PenroserGLView extends GLSurfaceView implements PenroserGLRenderer.Callbacks {
    private static final String TAG="PenroserGLView";

    private PenroserGLRenderer renderer = new PenroserGLRenderer(this);


    public PenroserGLView(Context context) {
        super(context);
        init();
    }

    public PenroserGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
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

        this.setRenderer(renderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    @Override
	public boolean onTouchEvent(MotionEvent event) {
        return renderer.onTouchEvent(event);
	}
}
