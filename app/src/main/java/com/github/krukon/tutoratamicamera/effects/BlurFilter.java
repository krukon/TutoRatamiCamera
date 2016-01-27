package com.github.krukon.tutoratamicamera.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.ScriptIntrinsicBlur;

/**
 * Created by jakub on 17.01.16.
 */
public class BlurFilter extends AbstractFilter {

    private ScriptIntrinsicBlur intrinsicBlur;
    private float radius = 15f;

    public BlurFilter(int imageWidth, int imageHeight, Context mainActivity, Bitmap sharedBitmap) {
        super(imageWidth, imageHeight, mainActivity, sharedBitmap);
        intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        intrinsicBlur.setRadius(radius);
    }

    @Override
    public void setRGB(int red, int green, int blue) {
    }

    public BlurFilter withRadius(float radius) {
        this.radius = radius;
        intrinsicBlur.setRadius(radius);
        return this;
    }

    @Override
    public Bitmap execute(byte[] data) {
        allocationYUV.copyFrom(data);
        intrinsicYuvToRGB.setInput(allocationYUV);
        intrinsicYuvToRGB.forEach(allocationIn);

        intrinsicBlur.setInput(allocationIn);
        intrinsicBlur.forEach(allocationOut);

        allocationOut.syncAll(Allocation.USAGE_SHARED);

        return outputBitmap;
    }

    @Override
    public String getName() {
        return "Blur";
    }

}
