package com.github.krukon.tutoratamicamera.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;

import com.github.krukon.tutoratamicamera.ScriptC_sepia;

/**
 * Created by jakub on 17.01.16.
 */
public class SepiaFilter extends AbstractFilter {

    private ScriptC_sepia script;

    public SepiaFilter(int imageWidth, int imageHeight, Context context, int red, int green, int blue) {
        super(imageWidth, imageHeight, context, red, green, blue);

        script = new ScriptC_sepia(rs);
        script.set_imageWidth(imageWidth);
        script.set_imageHeight(imageHeight);
        script.set_script(script);
        script.set_in(allocationIn);
        script.set_GS_BLUE((float)blue/100);
        script.set_GS_GREEN((float)green/100);
        script.set_GS_RED((float)red/100);
        script.set_out(allocationOut);
    }

    @Override
    public Bitmap execute(byte[] data) {
        allocationYUV.copyFrom(data);
        intrinsicYuvToRGB.setInput(allocationYUV);
        intrinsicYuvToRGB.forEach(allocationIn);

        script.invoke_filter();

        allocationOut.syncAll(Allocation.USAGE_SHARED);

        return outputBitmap;
    }

    @Override
    public String getName() {
        return "Sepia";
    }

}
