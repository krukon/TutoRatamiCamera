package com.github.krukon.tutoratamicamera.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;

import com.github.krukon.tutoratamicamera.ScriptC_monochrome;

/**
 * Created by krukon on 17.01.2016.
 */
public class MonochromeFilter extends AbstractFilter {

    private ScriptC_monochrome script;

    public MonochromeFilter(int imageWidth, int imageHeight, Context mainActivity, Bitmap sharedBitmap) {
        super(imageWidth, imageHeight, mainActivity, sharedBitmap);

        script = new ScriptC_monochrome(rs);

        script.set_imageWidth(imageWidth);
        script.set_imageHeight(imageHeight);
        script.set_script(script);
        script.set_in(allocationIn);
        script.set_out(allocationOut);
    }

    @Override
    public void setRGB(int red, int green, int blue) {
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
        return "Monochrome";
    }
}
