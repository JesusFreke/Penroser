package org.jf.GLWallpaper;

import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

public abstract class GLWallpaperService extends WallpaperService {
    public class GLEngine extends WallpaperService.Engine {
        private GLSurfaceView mGLSurfaceView;

        public GLEngine() {
            mGLSurfaceView = new GLWallpaperSurfaceView(GLWallpaperService.this);
        }

        public void setGLWrapper(GLSurfaceView.GLWrapper glWrapper) {
            mGLSurfaceView.setGLWrapper(glWrapper);
        }

        public void setDebugFlags(int debugFlags) {
            mGLSurfaceView.setDebugFlags(debugFlags);
        }

        public int getDebugFlags() {
            return mGLSurfaceView.getDebugFlags();
        }

        public void setRenderer(GLSurfaceView.Renderer renderer) {
            mGLSurfaceView.setRenderer(renderer);
            if (!isVisible()) {
                mGLSurfaceView.onPause();
            }
        }

        public void setEGLContextFactory(GLSurfaceView.EGLContextFactory factory) {
            mGLSurfaceView.setEGLContextFactory(factory);
        }

        public void setEGLWindowSurfaceFactory(GLSurfaceView.EGLWindowSurfaceFactory factory) {
            mGLSurfaceView.setEGLWindowSurfaceFactory(factory);
        }

        public void setEGLConfigChooser(GLSurfaceView.EGLConfigChooser configChooser) {
            mGLSurfaceView.setEGLConfigChooser(configChooser);
        }

        public void setEGLConfigChooser(boolean needDepth) {
            mGLSurfaceView.setEGLConfigChooser(needDepth);
        }

        public void setEGLConfigChooser(int redSize, int greenSize, int blueSize,
            int alphaSize, int depthSize, int stencilSize) {
            mGLSurfaceView.setEGLConfigChooser(redSize, greenSize, blueSize,
                    alphaSize, depthSize, stencilSize);
        }

        public void setEGLContextClientVersion(int version) {
            mGLSurfaceView.setEGLContextClientVersion(version);
        }

        public void setRenderMode(int renderMode) {
            mGLSurfaceView.setRenderMode(renderMode);
        }

        public int getRenderMode() {
            return mGLSurfaceView.getRenderMode();
        }

        public void requestRender() {
            mGLSurfaceView.requestRender();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                mGLSurfaceView.onResume();
            } else {
                mGLSurfaceView.onPause();
            }
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mGLSurfaceView.surfaceChanged(holder, format, width, height);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            mGLSurfaceView.surfaceCreated(holder);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            mGLSurfaceView.surfaceDestroyed(holder);
        }
    }
}
