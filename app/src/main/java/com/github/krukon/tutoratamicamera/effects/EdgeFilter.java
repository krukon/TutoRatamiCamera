package com.github.krukon.tutoratamicamera.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;

import com.github.krukon.tutoratamicamera.ScriptC_edge;

/**
 * Created by jakub on 22.01.16.
 */
public class EdgeFilter extends AbstractFilter {

    private ScriptC_edge script;

    public EdgeFilter(int imageWidth, int imageHeight, Context context) {
        super(imageWidth, imageHeight, context);

        script = new ScriptC_edge(rs);
        script.set_imageWidth(imageWidth);
        script.set_imageHeight(imageHeight);
        script.set_in(allocationIn);
        script.set_out(allocationOut);
        script.set_script(script);
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
        return "Edge";
    }
}
