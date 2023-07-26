/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package nitritmodel;

import data.PipeData;
import java.io.Serializable;

public class ColorBinder extends PipeData<ColorBinder> implements Serializable
{
    private String mImportance;
    private int mColor;

    public ColorBinder(String mImportance, int mColor) {
        this.mImportance = mImportance;
        this.mColor = mColor;
    }

    public ColorBinder() {
    }

    public String getmImportance() {
        return mImportance;
    }

    public void setmImportance(String mImportance) {
        this.mImportance = mImportance;
    }

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }
}
