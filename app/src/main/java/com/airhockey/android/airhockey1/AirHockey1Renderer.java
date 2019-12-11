/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
 ***/
package com.airhockey.android.airhockey1;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.airhockey.android.R;
import com.airhockey.android.model.GLProgramInfo;
import com.airhockey.android.common.util.FeatureConfig;
import com.airhockey.android.common.util.LoggerConfig;
import com.airhockey.android.common.util.ShaderHelper;
import com.airhockey.android.common.util.TextResourceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_ELEMENT_ARRAY_BUFFER;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_STATIC_DRAW;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBindBuffer;
import static android.opengl.GLES20.glBufferData;
import static android.opengl.GLES20.glDisableVertexAttribArray;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glDrawElements;
import static android.opengl.GLES20.glGenBuffers;
import static android.opengl.GLES32.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES32.GL_FLOAT;
import static android.opengl.GLES32.GL_POINTS;
import static android.opengl.GLES32.glClear;
import static android.opengl.GLES32.glClearColor;
import static android.opengl.GLES32.glEnableVertexAttribArray;
import static android.opengl.GLES32.glGetAttribLocation;
import static android.opengl.GLES32.glGetUniformLocation;
import static android.opengl.GLES32.glUniform4f;
import static android.opengl.GLES32.glUseProgram;
import static android.opengl.GLES32.glVertexAttribPointer;
import static android.opengl.GLES32.glViewport;

public class AirHockey1Renderer implements Renderer {
    private static final String U_COLOR = "u_Color";
    private static final String A_POSITION = "a_Position";
    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;
    private final Context context;

    private FloatBuffer tableVertexData;
    private ByteBuffer tableIndexArray;
    private ByteBuffer malleteIndexArray;
    private GLProgramInfo tableProgramInfo;
    private FloatBuffer malletsVertexData;

    private GLProgramInfo malletsProgramInfo;
    private int tableByteSize;
    private int tableIndexSize;
    private int malleteByteSize;
    private int malleteIndexSize;
    private int tablePointCount;
    private int linePointCount;

    public AirHockey1Renderer(Context context) {
        this.context = context;

        buildVertexArray();

        if (FeatureConfig.INDEX_DATA) {
            buildIndexArray();
        }
    }

    private void buildIndexArray() {
        tableIndexArray = ByteBuffer.allocateDirect(6 * BYTES_PER_FLOAT);
        byte[] tableIndex = {
                //三角形1
                0, 1, 2,
                //三角形2
                3, 4, 5,
                //横线
                6, 7
        };
        tableIndexSize = tableIndex.length;
        tableIndexArray.put(tableIndex);
        tableIndexArray.position(0);

        malleteIndexArray = ByteBuffer.allocateDirect(2 * BYTES_PER_FLOAT);
        byte[] malleteIndex = {
                0, 1
        };
        malleteIndexSize = malleteIndex.length;
        malleteIndexArray.put(malleteIndex);
        malleteIndexArray.position(0);
    }

    private void buildVertexArray() {
        float[] tableData = new float[]{
                // Triangle 1
                -0.5f, -0.5f,
                0.5f, 0.5f,
                -0.5f, 0.5f,

                // Triangle 2
                -0.5f, -0.5f,
                0.5f, -0.5f,
                0.5f, 0.5f,

                // Line 1
                -0.5f, 0f,
                0.5f, 0f,
        };
        tablePointCount = 6;
        linePointCount = 2;
        tableByteSize = tableData.length * BYTES_PER_FLOAT;
        tableVertexData = ByteBuffer
                .allocateDirect(tableByteSize)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        tableVertexData.put(tableData);
        tableVertexData.position(0);

        float[] malletsData = new float[]{
                // Mallets
                0f, -0.25f,
                0f, 0.25f
        };
        malleteByteSize = malletsData.length * BYTES_PER_FLOAT;
        malletsVertexData = ByteBuffer
                .allocateDirect(tableByteSize)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        malletsVertexData.put(malletsData);
        malletsVertexData.position(0);
    }


    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        // Set the background clear color to red. The first component is red,
        // the second is green, the third is blue, and the last component is
        // alpha, which we don't use in this lesson.
        // glClearColor(1.0f, 0.0f, 0.0f, 0.0f);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        tableProgramInfo = initTableProgram();
        initVbos(tableProgramInfo
                , tableVertexData
                , tableByteSize
                , tableIndexArray
                , tableIndexSize);

        malletsProgramInfo = initTableProgram();
        initVbos(malletsProgramInfo
                , malletsVertexData
                , malleteByteSize
                , malleteIndexArray
                , malleteIndexSize);
    }

    private GLProgramInfo initTableProgram() {
        int program = buildProgram();
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }
        glUseProgram(program);

        int uColorLocation = glGetUniformLocation(program, U_COLOR);
        int aPositionLocation = glGetAttribLocation(program, A_POSITION);

        GLProgramInfo programInfo = new GLProgramInfo();
        programInfo.setAttributePosLoc(aPositionLocation);
        programInfo.setUniformColorLoc(uColorLocation);
        programInfo.setProgram(program);
        return programInfo;
    }

    private int buildProgram() {
        String vertexShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_vertex_shader_es3);
        String fragmentShaderSource = TextResourceReader
                .readTextFileFromResource(context, R.raw.simple_fragment_shader_es3);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        return ShaderHelper.linkProgram(vertexShader, fragmentShader);
    }

    /**
     * onSurfaceChanged is called whenever the surface has changed. This is
     * called at least once when the surface is initialized. Keep in mind that
     * Android normally restarts an Activity on rotation, and in that case, the
     * renderer will be destroyed and a new one created.
     *
     * @param width  The new width, in pixels.
     * @param height The new height, in pixels.
     */
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);
    }

    /**
     * OnDrawFrame is called whenever a new frame needs to be drawn. Normally,
     * this is done at the refresh rate of the screen.
     */
    @Override
    public void onDrawFrame(GL10 glUnused) {
        //logDebug("onDrawFrame entry!");
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        drawTable();

        drawMallets();
    }

    private void drawTable() {
        glUseProgram(tableProgramInfo.getProgram());
        bindVbo(tableProgramInfo);
        // Draw the table.
        glUniform4f(tableProgramInfo.getUniformColorLoc(), 1.0f, 1.0f, 1.0f, 1.0f);
        if (FeatureConfig.INDEX_DATA) {
            glDrawElements(GL_TRIANGLES, tablePointCount, GL_UNSIGNED_BYTE, 0);
        } else {
            glDrawArrays(GL_TRIANGLES, 0, tablePointCount);
        }

        // Draw the center dividing line.
        glUniform4f(tableProgramInfo.getUniformColorLoc(), 1.0f, 0.0f, 0.0f, 1.0f);
        if (FeatureConfig.INDEX_DATA) {
            glDrawElements(GL_LINES, linePointCount, GL_UNSIGNED_BYTE, 6);
        } else {
            glDrawArrays(GL_LINES, tablePointCount, linePointCount);
        }

        ubBindVbo(tableProgramInfo);
    }

    private void initVbos(GLProgramInfo programInfo
            , FloatBuffer vertexData
            , int vertexByteSize
            , ByteBuffer indexData
            , int indexSize) {
        logDebug("initVbos entry: program id:" + programInfo.getProgram() + ", byte tableByteSize:" + vertexByteSize);
        int[] vboIds;
        if (FeatureConfig.INDEX_DATA) {
            vboIds = new int[2];
            glGenBuffers(2, vboIds, 0);
        } else {
            vboIds = new int[1];
            glGenBuffers(1, vboIds, 0);
        }

        glBindBuffer(GL_ARRAY_BUFFER, vboIds[0]);
        glBufferData(GL_ARRAY_BUFFER, vertexByteSize, vertexData, GL_STATIC_DRAW);
        programInfo.setArrayVboID(vboIds[0]);
        logDebug("initVbos array data vbo id:" + vboIds[0]);

        if (FeatureConfig.INDEX_DATA) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboIds[1]);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexSize, indexData, GL_STATIC_DRAW);
            programInfo.setIndexVboID(vboIds[1]);
            logDebug("initVbos index data vbo id:" + vboIds[1]);
        }
    }

    private void bindVbo(GLProgramInfo programInfo) {
        glBindBuffer(GL_ARRAY_BUFFER, programInfo.getArrayVboID());
        if (FeatureConfig.INDEX_DATA) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, programInfo.getIndexVboID());
        }

        glEnableVertexAttribArray(programInfo.getAttributePosLoc());
        glVertexAttribPointer(programInfo.getAttributePosLoc()
                , POSITION_COMPONENT_COUNT
                , GL_FLOAT
                , false
                , POSITION_COMPONENT_COUNT * BYTES_PER_FLOAT
                , 0);

    }

    private void ubBindVbo(GLProgramInfo programInfo) {
        glDisableVertexAttribArray(programInfo.getProgram());
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        if (FeatureConfig.INDEX_DATA) {
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }

    private void
     drawMallets() {
        glUseProgram(malletsProgramInfo.getProgram());
        bindVbo(malletsProgramInfo);
        glUniform4f(tableProgramInfo.getUniformColorLoc(), 0.0f, 0.0f, 1.0f, 1.0f);
        if (FeatureConfig.INDEX_DATA) {
            glDrawElements(GL_POINTS, 1, GL_UNSIGNED_BYTE, 0);
        } else {
            glDrawArrays(GL_POINTS, 0, 1);
        }

        glUniform4f(tableProgramInfo.getUniformColorLoc(), 1.0f, 0.0f, 0.0f, 1.0f);
        if (FeatureConfig.INDEX_DATA) {
            glDrawElements(GL_POINTS, 1, GL_UNSIGNED_BYTE, 1);
        } else {
            glDrawArrays(GL_POINTS, 1, 1);
        }
    }

    private void logDebug(String msg) {
        Log.d(this.getClass().getSimpleName(), msg);
    }
}
