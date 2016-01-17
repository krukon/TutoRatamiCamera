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

    private float radius = 15f;

    public BlurFilter(int imageWidth, int imageHeight, Context context) {
        super(imageWidth, imageHeight, context);
    }

    public BlurFilter withRadius(float radius) {
        this.radius = radius;
        return this;
    }

    @Override
    public Bitmap execute(byte[] data) {
        Bitmap outputBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        Allocation allocationOut = Allocation.createFromBitmap(rs, outputBitmap);

        Allocation allocationBlur =  Allocation.createTyped(rs, allocationOut.getType(), Allocation.USAGE_SCRIPT);

        ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        intrinsicBlur.setRadius(radius);

        allocationYUV.copyFrom(data);
        intrinsicYuvToRGB.setInput(allocationYUV);
        intrinsicYuvToRGB.forEach(allocationBlur);

        intrinsicBlur.setInput(allocationBlur);
        intrinsicBlur.forEach(allocationOut);

        allocationOut.syncAll(Allocation.USAGE_SHARED);

        return outputBitmap;
    }

}
