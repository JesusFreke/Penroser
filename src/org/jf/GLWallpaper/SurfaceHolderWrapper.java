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
