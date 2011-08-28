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

public class MomentumController {
    private static final int NUM_TOUCHES = 10;
    private static final int MAX_TOUCH_AGE_MS = 300;

    private float targetXYVelocity = 75;

    private float xyVFactor = .33f;

    private float xVNorm = 0;
    private float yVNorm = 0;
    private float xyV = 0;

    private long releaseTime = -1;

    private int nextMovement = 0;
    private PointerMovement[] movements;

    private boolean touchActive = false;

    private static class PointerMovement {
        public long eventTime;
        public float xDelta;
        public float yDelta;

        public PointerMovement() {
            eventTime = -1;
        }

        public void set(long eventTime, float xDelta, float yDelta) {
            this.eventTime = eventTime;
            this.xDelta = xDelta;
            this.yDelta = yDelta;
        }

        public void reset() {
            eventTime = -1;
        }
    }

    public MomentumController() {
        movements = new PointerMovement[NUM_TOUCHES];
        for (int i=0; i<NUM_TOUCHES; i++) {
            movements[i] = new PointerMovement();
        }
    }

    public void reset() {
        for (int i=0; i<NUM_TOUCHES; i++) {
            movements[i].reset();
        }
        nextMovement = 0;
        releaseTime = -1;
        touchActive = false;

        xVNorm = PenroserApp.random.nextFloat()-.5f;
        yVNorm = PenroserApp.random.nextFloat()-.5f;
        xyV = (float)Math.sqrt(xVNorm*xVNorm + yVNorm*yVNorm);
        xVNorm = xVNorm/xyV;
        yVNorm = yVNorm/xyV;
        xyV = 50;
    }

    public boolean touchActive() {
        return touchActive;
    }

    public void addValues(long eventTime, float xDelta, float yDelta, float angleDelta, float scaleDelta) {
        touchActive = true;
        PointerMovement currentMovement = movements[nextMovement];
        nextMovement = (nextMovement+1)%NUM_TOUCHES;
        currentMovement.set(eventTime, xDelta, yDelta);
    }

    public void touchReleased() {
        releaseTime = System.nanoTime();
        touchActive = false;

        int oldestIndex = nextMovement;
        long newestTime = -1;
        for (int i=nextMovement-1; i>nextMovement-11; i--) {
            int index = MathUtil.positiveMod(i, NUM_TOUCHES);

            PointerMovement movement = movements[index];
            if (movement.eventTime != -1) {
                if (newestTime == -1) {
                    newestTime = movement.eventTime;
                } else if (newestTime - movement.eventTime > MAX_TOUCH_AGE_MS) {
                    break;
                }
            } else {
                break;
            }
            oldestIndex = index;
        }

        if (movements[oldestIndex].eventTime == -1 || movements[(oldestIndex+1)%NUM_TOUCHES].eventTime == -1) {
            return;
        }

        xVNorm = 0;
        yVNorm = 0;
        xyV = 0;

        float xCumul = 0;
        float yCumul = 0;
        float angleCumul = 0;
        float scaleCumul = 0;

        long oldestTime = movements[oldestIndex].eventTime;
        for (int i=(oldestIndex+1)%NUM_TOUCHES; i!=nextMovement; i = (i+1)%NUM_TOUCHES) {
            PointerMovement movement = movements[i];

            long duration = movement.eventTime - oldestTime;

            if (duration == 0) {
                continue;
            }

            xCumul += movement.xDelta;
            yCumul += movement.yDelta;

            float _xV = (movement.xDelta/duration) * 1000;
            float _yV = (movement.yDelta/duration) * 1000;

            xVNorm = xVNorm==0?_xV:(xVNorm+_xV)/2f;
            yVNorm = yVNorm==0?_yV:(yVNorm+_yV)/2f;
        }

        xyV = (float)Math.sqrt(xVNorm * xVNorm + yVNorm * yVNorm);
        xVNorm = xVNorm / xyV;
        yVNorm = yVNorm / xyV;
    }

    public void getVelocities(long currentTime, float[] velocities) {
        assert velocities.length == 2;
        long delta = currentTime - releaseTime;

        float xyVel = (float)(xyV * Math.pow(xyVFactor, delta/1000000000f));
        if (xyVel < targetXYVelocity)
            xyVel = targetXYVelocity;

        velocities[0] = xVNorm * xyVel;
        velocities[1] = yVNorm * xyVel;
    }
}
