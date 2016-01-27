package com.github.krukon.tutoratamicamera.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;

/**
 * Created by jakub on 17.01.16.
 */
public class NormalFilter extends AbstractFilter {

    public NormalFilter(int imageWidth, int imageHeight, Context mainActivity, Bitmap sharedBitmap) {
        super(imageWidth, imageHeight, mainActivity, sharedBitmap);
    }

    @Override
    public Bitmap execute(byte[] data) {
        allocationYUV.copyFrom(data);
        intrinsicYuvToRGB.setInput(allocationYUV);
        intrinsicYuvToRGB.forEach(allocationOut);

        allocationOut.syncAll(Allocation.USAGE_SHARED);

        return outputBitmap;
    }

    @Override
    public void setRGB(int red, int green, int blue) {
    }

    @Override
    public String getName() {
        return "Normal";
    }

}
