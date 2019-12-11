package com.airhockey.android.model;

public class GLProgramInfo {
    private int program;
    private int uniformColorLoc;
    private int attributePosLoc;
    private int arrayVboID = 0;
    private int indexVboID;

    public int getProgram() {
        return program;
    }

    public void setProgram(int program) {
        this.program = program;
    }

    public int getUniformColorLoc() {
        return uniformColorLoc;
    }

    public void setUniformColorLoc(int uniformColorLoc) {
        this.uniformColorLoc = uniformColorLoc;
    }

    public int getAttributePosLoc() {
        return attributePosLoc;
    }

    public void setAttributePosLoc(int attributePosLoc) {
        this.attributePosLoc = attributePosLoc;
    }

    public int getArrayVboID() {
        return arrayVboID;
    }

    public void setArrayVboID(int arrayVboID) {
        this.arrayVboID = arrayVboID;
    }

    public void setIndexVboID(int indexVboID) {
        this.indexVboID = indexVboID;
    }

    public int getIndexVboID() {
        return indexVboID;
    }
}
