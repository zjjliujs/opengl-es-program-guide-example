/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
package com.airhockey.android.immallete;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.airhockey.android.R;
import com.airhockey.android.common.objects.Mallet;
import com.airhockey.android.common.objects.Puck;
import com.airhockey.android.common.objects.Table;
import com.airhockey.android.common.programs.UColorShaderProgram;
import com.airhockey.android.common.programs.TextureShaderProgram;
import com.airhockey.android.common.util.MatrixHelper;
import com.airhockey.android.common.util.TextureHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.scaleM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

public class HockeyImMalletRenderer implements Renderer {
    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] viewProjectionMatrix = new float[16];
    private final float[] modelViewProjectionMatrix = new float[16];

    private Table table;
    private Mallet mallet;
    private Puck puck;

    private TextureShaderProgram textureProgram;
    private UColorShaderProgram colorProgram;

    private int texture;
    private double theta = 0;
    private float[] scaleMatrix = new float[16];
    private float[] rotateMatrix = new float[16];
    private float[] transMatrix = new float[16];


    public HockeyImMalletRenderer(Context context) {
        this.context = context;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        table = new Table();
        mallet = new Mallet(0.08f, 0.15f, 32);
        puck = new Puck(0.06f, 0.02f, 32);

        textureProgram = new TextureShaderProgram(context);
        colorProgram = new UColorShaderProgram(context);

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);
        MatrixHelper.perspectiveM(projectionMatrix
                , 45
                , (float) width / (float) height
                , 1f
                , 10f);
        //setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        //double theta = Math.PI * 2 / (System.currentTimeMillis() % 10000);
        //Log.d(LOG_TAG, "theta:" + theta + ",currentTimeMillis:" + System.currentTimeMillis());
        theta += Math.PI * 2 / 1000f;
        float z = (float) (2.2f * Math.cos(theta));
        float x = (float) (2.2f * Math.sin(theta));
        setLookAtM(viewMatrix, 0, x, 1.2f, z, 0f, 0f, 0f, 0f, 1f, 0f);

        // Multiply the view and projection matrices together.
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        // Draw the table.
        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        // Draw the mallets.
        positionObjectOnTable(0f, -0.4f, mallet.height / 2f, 0.5f);
        colorProgram.useProgram();
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorProgram);
        mallet.draw();

        positionObjectOnTable(0f, 0.4f, mallet.height / 2f, 0.5f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        // Note that we don't have to define the object data twice -- we just
        // draw the same mallet again but in a different position and with a
        // different color.
        mallet.draw();

        // Draw the puck.
        positionObjectOnTable(0f, 0f, puck.height / 2f, 1f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();
    }

    private void positionTableInScene() {
        // The table is defined in terms of X & Y coordinates, so we rotate it
        // 90 degrees to lie flat on the XZ plane.
        setIdentityM(modelMatrix, 0);
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f);
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix,
                0, modelMatrix, 0);
    }

    // The mallets and the puck are positioned on the same plane as the table.
    //放大 -> 自我旋转 -> 移动 -> 对照桌面旋转
    private void positionObjectOnTable(float x, float y, float z, float scale) {
        //物体缩放
        setIdentityM(scaleMatrix, 0);
        scaleM(scaleMatrix, 0, scale, scale, scale);

        //自我旋转
        setIdentityM(rotateMatrix, 0);
        rotateM(rotateMatrix, 0, 90, 1, 0, 0);
        multiplyMM(modelMatrix, 0
                , rotateMatrix, 0
                , scaleMatrix, 0);

        //位移
        setIdentityM(transMatrix, 0);
        translateM(transMatrix, 0, x, y, z * scale);
        multiplyMM(modelMatrix, 0
                , transMatrix, 0
                , modelMatrix, 0);

        //桌面旋转
        setIdentityM(rotateMatrix, 0);
        rotateM(rotateMatrix, 0, -90, 1, 0, 0);
        multiplyMM(modelMatrix, 0, rotateMatrix, 0, modelMatrix, 0);

        //build MPV
        multiplyMM(modelViewProjectionMatrix, 0
                , viewProjectionMatrix, 0
                , modelMatrix, 0);
    }
}