package org.jf.Penroser;

import android.text.method.MovementMethod;

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

        xVNorm = Penroser.random.nextFloat()-.5f;
        yVNorm = Penroser.random.nextFloat()-.5f;
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
        releaseTime= System.currentTimeMillis();
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

        if (movements[oldestIndex].eventTime == -1) {
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

    public void getVelocities(float[] velocities) {
        assert velocities.length == 2;
        long currentTime = System.currentTimeMillis();
        long delta = currentTime - releaseTime;

        float xyVel = xyV * (float)Math.pow(xyVFactor, delta/1000f);
        if (xyVel < targetXYVelocity)
            xyVel = targetXYVelocity;

        velocities[0] = xVNorm * xyVel;
        velocities[1] = yVNorm * xyVel;
    }
}
