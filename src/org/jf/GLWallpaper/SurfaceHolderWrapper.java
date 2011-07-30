package org.jf.GLWallpaper;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.ArrayList;

public class SurfaceHolderWrapper implements SurfaceHolder {
    private final ArrayList<Callback> mCallbacks = new ArrayList<Callback>();

    private SurfaceHolder mSurfaceHolder = null;

    private Callback callback = new Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            for (Callback callback: mCallbacks) {
                callback.surfaceCreated(holder);
            }
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            for (Callback callback: mCallbacks) {
                callback.surfaceChanged(holder, format, width, height);
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            for (Callback callback: mCallbacks) {
                callback.surfaceDestroyed(holder);
            }
        }
    };

    public SurfaceHolderWrapper() {
    }

    private SurfaceHolder getSurfaceHolder() {
        if (mSurfaceHolder == null) {
            throw new RuntimeException("No SurfaceHolder yet");
        }
        return mSurfaceHolder;
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
        if (surfaceHolder != null) {
            surfaceHolder.addCallback(callback);
        }
    }

    public void addCallback(Callback callback) {
        mCallbacks.add(callback);
    }

    public void removeCallback(Callback callback) {
        mCallbacks.remove(callback);
    }

    public boolean isCreating() {
        return getSurfaceHolder().isCreating();
    }

    public void setType(int type) {
        getSurfaceHolder().setType(type);
    }

    public void setFixedSize(int width, int height) {
        getSurfaceHolder().setFixedSize(width, height);
    }

    public void setSizeFromLayout() {
        getSurfaceHolder().setSizeFromLayout();
    }

    public void setFormat(int format) {
        getSurfaceHolder().setFormat(format);
    }

    public void setKeepScreenOn(boolean screenOn) {
        getSurfaceHolder().setKeepScreenOn(screenOn);
    }

    public Canvas lockCanvas() {
        return getSurfaceHolder().lockCanvas();
    }

    public Canvas lockCanvas(Rect dirty) {
        return getSurfaceHolder().lockCanvas(dirty);
    }

    public void unlockCanvasAndPost(Canvas canvas) {
        getSurfaceHolder().unlockCanvasAndPost(canvas);
    }

    public Rect getSurfaceFrame() {
        return getSurfaceHolder().getSurfaceFrame();
    }

    public Surface getSurface() {
        return getSurfaceHolder().getSurface();
    }
}
