/**
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
 *
 ******
 * Various parts of the code are directly taken from/based on portions of
 * the GLSurfaceView class in AOSP, as per the following license:
 *
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******
 * Additional portions of the code are taken from/based on jsemler's
 * PixelBuffer class, taken from http://www.anddev.org/post41662.html#p41662,
 * and used in accordance with the following license:
 *
 * "Feel free to use it."
 */

package org.jf.GLPixelBuffer;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

import java.nio.IntBuffer;

import static javax.microedition.khronos.egl.EGL10.*;
import static javax.microedition.khronos.egl.EGL10.EGL_NONE;
import static javax.microedition.khronos.opengles.GL10.GL_RGBA;
import static javax.microedition.khronos.opengles.GL10.GL_UNSIGNED_BYTE;

/**
 * Uses opengl to render a GLSurfaceView.Renderer to an offscreen buffer
 *
 * This class is a franken-monster-mash of code from AOSP's GLSurfaceView, jsemler's
 * PixelBuffer class (http://www.anddev.org/post41662.html#p41662), and some of my
 * own special sauce
 */
public class GLPixelBuffer {
    private GLSurfaceView.GLWrapper glWrapper;
    private int debugFlags;
    private GLSurfaceView.Renderer renderer;
    private GLSurfaceView.EGLContextFactory eglContextFactory;
    private EGLPbufferSurfaceFactory eglPbufferSurfaceFactory;
    private GLSurfaceView.EGLConfigChooser eglConfigChooser;
    private int eglContextClientVersion;

    public void setGLWrapper(final GLSurfaceView.GLWrapper glWrapper) {
        this.glWrapper = glWrapper;
    }

    public void setDebugFlags(final int debugFlags) {
        this.debugFlags = debugFlags;
    }

    public int getDebugFlags() {
        return debugFlags;
    }

    public void setRenderer(final GLSurfaceView.Renderer renderer) {
        this.renderer = renderer;
    }

    public void setEGLContextFactory(final GLSurfaceView.EGLContextFactory eglContextFactory) {
        this.eglContextFactory = eglContextFactory;
    }

    public void setEGLPbufferSurfaceFactory(final EGLPbufferSurfaceFactory eglPbufferSurfaceFactory) {
        this.eglPbufferSurfaceFactory = eglPbufferSurfaceFactory;
    }

    public void setEGLConfigChooser(final GLSurfaceView.EGLConfigChooser eglConfigChooser) {
        this.eglConfigChooser = eglConfigChooser;
    }

    public void setEGLConfigChooser(final boolean needDepth) {
        setEGLConfigChooser(new SimpleEGLConfigChooser(needDepth));
    }

    public void setEGLConfigChooser(final int redSize, final int greenSize, final int blueSize, final int alphaSize, final int depthSize, final int stencilSize) {
        setEGLConfigChooser(new ComponentSizeChooser(redSize, greenSize, blueSize, alphaSize, depthSize, stencilSize));
    }

    public void setEGLContextClientVersion(final int eglContextClientVersion) {
        this.eglContextClientVersion = eglContextClientVersion;
    }

    public Bitmap draw(int width, int height) {
        if (renderer == null) {
            throw new RuntimeException("No renderer has been set");
        }

        if (eglConfigChooser == null) {
            eglConfigChooser = new SimpleEGLConfigChooser(true);
        }
        if (eglContextFactory == null) {
            eglContextFactory = new DefaultContextFactory();
        }
        if (eglPbufferSurfaceFactory == null) {
            eglPbufferSurfaceFactory = new DefaultPbufferSurfaceFactory();
        }

        EGL10 egl = (EGL10)EGLContext.getEGL();
        EGLDisplay eglDisplay = egl.eglGetDisplay(EGL_DEFAULT_DISPLAY);
        if (eglDisplay == EGL10.EGL_NO_DISPLAY) {
            throw new RuntimeException("eglGetDisplay failed");
        }

        int[] version = new int[2];
        if(!egl.eglInitialize(eglDisplay, version)) {
            throw new RuntimeException("eglInitialize failed");
        }
        EGLConfig eglConfig = eglConfigChooser.chooseConfig(egl, eglDisplay);

        EGLContext eglContext = eglContextFactory.createContext(egl, eglDisplay, eglConfig);
        if (eglContext == null || eglContext == EGL10.EGL_NO_CONTEXT) {
            throw new RuntimeException("eglCreateContext failed (" + egl.eglGetError() + ")");
        }

        EGLSurface eglSurface = eglPbufferSurfaceFactory.createPbufferSurface(egl, eglDisplay, eglConfig, width, height);
        if (eglSurface == null || eglSurface == EGL10.EGL_NO_SURFACE) {
            throw new RuntimeException("eglCreatePbufferSurface failed (" + egl.eglGetError() + ")");
        }

        /*
         * Before we can issue GL commands, we need to make sure
         * the context is current and bound to a surface.
         */
        if (!egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
            throw new RuntimeException("eglMakeCurrent failed (" + egl.eglGetError() + ")");
        }

        GL gl = eglContext.getGL();
        if (glWrapper != null) {
            gl = glWrapper.wrap(gl);
        }

        renderer.onSurfaceCreated((GL10)gl, eglConfig);
        renderer.onSurfaceChanged((GL10)gl, width, height);

        renderer.onDrawFrame((GL10)gl);
        Bitmap bmp = convertToBitmap((GL10)gl, width, height);

        eglPbufferSurfaceFactory.destroySurface(egl, eglDisplay, eglSurface);
        eglContextFactory.destroyContext(egl, eglDisplay, eglContext);
        egl.eglTerminate(eglDisplay);

        return bmp;
    }

    private Bitmap convertToBitmap(final GL10 gl, final int width, final int height) {
        IntBuffer ib = IntBuffer.allocate(width*height);
        IntBuffer ibt = IntBuffer.allocate(width*height);
        gl.glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, ib);

        // Convert upside down mirror-reversed image to right-side up normal image.
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                ibt.put((height-i-1)*width + j, ib.get(i*width + j));
            }
        }

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.copyPixelsFromBuffer(ibt);
        return bmp;
    }

    private abstract class BaseConfigChooser implements GLSurfaceView.EGLConfigChooser {
        public BaseConfigChooser(int[] configSpec) {
            mConfigSpec = filterConfigSpec(configSpec);
        }

        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
            int[] num_config = new int[1];
            if (!egl.eglChooseConfig(display, mConfigSpec, null, 0,
                    num_config)) {
                throw new IllegalArgumentException("eglChooseConfig failed");
            }

            int numConfigs = num_config[0];

            if (numConfigs <= 0) {
                throw new IllegalArgumentException(
                        "No configs match configSpec");
            }

            EGLConfig[] configs = new EGLConfig[numConfigs];
            if (!egl.eglChooseConfig(display, mConfigSpec, configs, numConfigs,
                    num_config)) {
                throw new IllegalArgumentException("eglChooseConfig#2 failed");
            }
            EGLConfig config = chooseConfig(egl, display, configs);
            if (config == null) {
                throw new IllegalArgumentException("No config chosen");
            }
            return config;
        }

        abstract EGLConfig chooseConfig(EGL10 egl, EGLDisplay display,
                EGLConfig[] configs);

        protected int[] mConfigSpec;

        private int[] filterConfigSpec(int[] configSpec) {
            if (eglContextClientVersion != 2) {
                return configSpec;
            }
            /* We know none of the subclasses define EGL_RENDERABLE_TYPE.
             * And we know the configSpec is well formed.
             */
            int len = configSpec.length;
            int[] newConfigSpec = new int[len + 2];
            System.arraycopy(configSpec, 0, newConfigSpec, 0, len-1);
            newConfigSpec[len-1] = EGL10.EGL_RENDERABLE_TYPE;
            newConfigSpec[len] = 4; /* EGL_OPENGL_ES2_BIT */
            newConfigSpec[len+1] = EGL10.EGL_NONE;
            return newConfigSpec;
        }
    }

    /**
     * Choose a configuration with exactly the specified r,g,b,a sizes,
     * and at least the specified depth and stencil sizes.
     */
    private class ComponentSizeChooser extends BaseConfigChooser {
        public ComponentSizeChooser(int redSize, int greenSize, int blueSize,
                int alphaSize, int depthSize, int stencilSize) {
            super(new int[] {
                    EGL10.EGL_RED_SIZE, redSize,
                    EGL10.EGL_GREEN_SIZE, greenSize,
                    EGL10.EGL_BLUE_SIZE, blueSize,
                    EGL10.EGL_ALPHA_SIZE, alphaSize,
                    EGL10.EGL_DEPTH_SIZE, depthSize,
                    EGL10.EGL_STENCIL_SIZE, stencilSize,
                    EGL10.EGL_NONE});
            mValue = new int[1];
            mRedSize = redSize;
            mGreenSize = greenSize;
            mBlueSize = blueSize;
            mAlphaSize = alphaSize;
            mDepthSize = depthSize;
            mStencilSize = stencilSize;
       }

        @Override
        public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display,
                EGLConfig[] configs) {
            for (EGLConfig config : configs) {
                int d = findConfigAttrib(egl, display, config,
                        EGL10.EGL_DEPTH_SIZE, 0);
                int s = findConfigAttrib(egl, display, config,
                        EGL10.EGL_STENCIL_SIZE, 0);
                if ((d >= mDepthSize) && (s >= mStencilSize)) {
                    int r = findConfigAttrib(egl, display, config,
                            EGL10.EGL_RED_SIZE, 0);
                    int g = findConfigAttrib(egl, display, config,
                             EGL10.EGL_GREEN_SIZE, 0);
                    int b = findConfigAttrib(egl, display, config,
                              EGL10.EGL_BLUE_SIZE, 0);
                    int a = findConfigAttrib(egl, display, config,
                            EGL10.EGL_ALPHA_SIZE, 0);
                    if ((r == mRedSize) && (g == mGreenSize)
                            && (b == mBlueSize) && (a == mAlphaSize)) {
                        return config;
                    }
                }
            }
            return null;
        }

        private int findConfigAttrib(EGL10 egl, EGLDisplay display,
                EGLConfig config, int attribute, int defaultValue) {

            if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
                return mValue[0];
            }
            return defaultValue;
        }

        private int[] mValue;
        // Subclasses can adjust these values:
        protected int mRedSize;
        protected int mGreenSize;
        protected int mBlueSize;
        protected int mAlphaSize;
        protected int mDepthSize;
        protected int mStencilSize;
    }

    /**
     * This class will choose a RGB_565 surface with
     * or without a depth buffer.
     *
     */
    private class SimpleEGLConfigChooser extends ComponentSizeChooser {
        public SimpleEGLConfigChooser(boolean withDepthBuffer) {
            super(5, 6, 5, 0, withDepthBuffer ? 16 : 0, 0);
        }
    }

    private class DefaultContextFactory implements GLSurfaceView.EGLContextFactory {
        private int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

        public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig config) {
            int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, eglContextClientVersion, EGL10.EGL_NONE };
            return egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, eglContextClientVersion != 0 ? attrib_list : null);
        }

        public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
            if (!egl.eglDestroyContext(display, context)) {
                Log.e("DefaultContextFactory", "display:" + display + " context: " + context);
                throw new RuntimeException("eglDestroyContext failed: " + egl.eglGetError());
            }
        }
    }

    public interface EGLPbufferSurfaceFactory {
        public EGLSurface createPbufferSurface(EGL10 egl, EGLDisplay display, EGLConfig config, int width, int height);
        void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface);
    }

    private static class DefaultPbufferSurfaceFactory implements EGLPbufferSurfaceFactory {
        public EGLSurface createPbufferSurface(EGL10 egl, EGLDisplay display, EGLConfig config, int width, int height) {
            return egl.eglCreatePbufferSurface(display, config, new int[] {
                    EGL_WIDTH, width,
                    EGL_HEIGHT, height,
                    EGL_NONE
                });
        }

        public void destroySurface(EGL10 egl, EGLDisplay display,
                EGLSurface surface) {
            egl.eglDestroySurface(display, surface);
        }
    }
}