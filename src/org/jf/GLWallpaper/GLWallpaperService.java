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
