package com.airhockey.android.heightmap;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.View;

class GLSurfaceViewOnTouchListener implements View.OnTouchListener {
    private final ParticlesHeightMapRenderer particlesRenderer;
    float previousX, previousY;
    private GLSurfaceView glSurfaceView;

    public GLSurfaceViewOnTouchListener(GLSurfaceView particlesActivity, ParticlesHeightMapRenderer particlesRenderer) {
        this.glSurfaceView = particlesActivity;
        this.particlesRenderer = particlesRenderer;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event != null) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                previousX = event.getX();
                previousY = event.getY();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                final float deltaX = event.getX() - previousX;
                final float deltaY = event.getY() - previousY;

                previousX = event.getX();
                previousY = event.getY();

                glSurfaceView.queueEvent(() -> particlesRenderer.handleTouchDrag(
                        deltaX, deltaY));
            }

            return true;
        } else {
            return false;
        }
    }
}
