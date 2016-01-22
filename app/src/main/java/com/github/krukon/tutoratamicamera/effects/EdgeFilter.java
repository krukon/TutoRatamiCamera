package com.github.krukon.tutoratamicamera.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;

import com.github.krukon.tutoratamicamera.ScriptC_edge;

/**
 * Created by jakub on 22.01.16.
 */
public class EdgeFilter extends AbstractFilter {

    private ScriptC_edge script;

    public EdgeFilter(int imageWidth, int imageHeight, Context context) {
        super(imageWidth, imageHeight, context);

        script = new ScriptC_edge(rs);

        int[] row_indices = new int[imageHeight];
        for (int i = 0; i < imageHeight; i++) {
            row_indices[i] = i * imageWidth;
        }
        Allocation row_indices_alloc = Allocation.createSized(rs, Element.I32(rs), imageHeight, Allocation.USAGE_SCRIPT);
        row_indices_alloc.copyFrom(row_indices);

        //TODO - why cant I use imageWidth, imageHeight???
        script.set_imageWidth(1100);
        script.set_imageHeight(1080);

        script.set_in(row_indices_alloc);
        script.set_out(row_indices_alloc);

        script.bind_inPixels(allocationIn);
        script.bind_outPixels(allocationOut);

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
}
