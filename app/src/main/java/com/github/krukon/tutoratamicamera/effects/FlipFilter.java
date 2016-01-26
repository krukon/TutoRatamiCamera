package com.github.krukon.tutoratamicamera.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;

import com.github.krukon.tutoratamicamera.ScriptC_flip;

/**
 * Created by krukon on 25.01.2016.
 */
public class FlipFilter extends AbstractFilter {

    private ScriptC_flip script;

    public FlipFilter(int imageWidth, int imageHeight, Context context, int red, int green, int blue) {
        super(imageWidth, imageHeight, context, red, green, blue);

        script = new ScriptC_flip(rs);

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
        return "Flip";
    }
}
