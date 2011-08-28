package org.jf.GLWallpaper;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

public class GLWallpaperSurfaceView extends GLSurfaceView {
    private SurfaceHolderWrapper mSurfaceHolderWrapper = null;

    public GLWallpaperSurfaceView(Context context) {
        super(context);
    }

    public GLWallpaperSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        ((SurfaceHolderWrapper)getHolder()).setSurfaceHolder(holder);
        super.surfaceCreated(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        ((SurfaceHolderWrapper)getHolder()).setSurfaceHolder(null);
        super.surfaceDestroyed(holder);
    }

    @Override
    public SurfaceHolder getHolder() {
        /**
         * This is called by the GLSurfaceView in the constructor, before mSurfaceHolderWrapper
         * can be otherwise initialized, so we initialize it here
         */
        if (mSurfaceHolderWrapper == null) {
            mSurfaceHolderWrapper = new SurfaceHolderWrapper();
        }
        return mSurfaceHolderWrapper;
    }
}
