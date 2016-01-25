package com.github.krukon.tutoratamicamera.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;

import com.github.krukon.tutoratamicamera.ScriptC_brightness;
import com.github.krukon.tutoratamicamera.effects.AbstractFilter;

/**
 * Created by wiktortendera on 25/01/16.
 */
public class BrightnessFilter extends AbstractFilter {

    private ScriptC_brightness script;

    public BrightnessFilter(int imageWidth, int imageHeight, Context context) {
        super(imageWidth, imageHeight, context);

        script = new ScriptC_brightness(rs);
        script.set_imageWidth(imageWidth);
        script.set_imageHeight(imageHeight);
        script.set_script(script);
        script.set_in(allocationIn);
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
        return "Brightness";
    }
}
