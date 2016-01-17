package com.github.krukon.tutoratamicamera.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;

/**
 * Created by jakub on 17.01.16.
 */
public class NormalFilter extends AbstractFilter {

    public NormalFilter(int imageWidth, int imageHeight, Context context) {
        super(imageWidth, imageHeight, context);
    }

    @Override
    public Bitmap execute(byte[] data) {
        Bitmap outputBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        Allocation allocationOut = Allocation.createFromBitmap(rs, outputBitmap);

        allocationYUV.copyFrom(data);
        intrinsicYuvToRGB.setInput(allocationYUV);
        intrinsicYuvToRGB.forEach(allocationOut);

        allocationOut.syncAll(Allocation.USAGE_SHARED);

        return outputBitmap;
    }

}
