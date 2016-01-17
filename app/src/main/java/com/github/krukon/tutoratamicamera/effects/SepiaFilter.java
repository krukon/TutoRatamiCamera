package com.github.krukon.tutoratamicamera.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.ScriptIntrinsicBlur;

import com.github.krukon.tutoratamicamera.ScriptC_sepia;

/**
 * Created by jakub on 17.01.16.
 */
public class SepiaFilter extends AbstractFilter {

    public SepiaFilter(int imageWidth, int imageHeight, Context context) {
        super(imageWidth, imageHeight, context);
    }

    @Override
    public Bitmap execute(byte[] data) {
        Bitmap outputBitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        Allocation allocationOut = Allocation.createFromBitmap(rs, outputBitmap);
        Allocation allocationIn =  Allocation.createTyped(rs, allocationOut.getType(), Allocation.USAGE_SCRIPT);

        ScriptC_sepia script = new ScriptC_sepia(rs);
        script.set_imageWidth(imageWidth);
        script.set_imageHeight(imageHeight);
        script.set_in(allocationIn);
        script.set_out(allocationOut);
        script.set_script(script);

        allocationYUV.copyFrom(data);
        intrinsicYuvToRGB.setInput(allocationYUV);
        intrinsicYuvToRGB.forEach(allocationIn);

        script.invoke_filter();

        allocationOut.syncAll(Allocation.USAGE_SHARED);

        return outputBitmap;
    }

}
