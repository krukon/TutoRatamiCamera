package com.github.krukon.tutoratamicamera.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.view.View;
import android.widget.SeekBar;

import com.github.krukon.tutoratamicamera.ScriptC_sepia;

/**
 * Created by jakub on 17.01.16.
 */
public class SepiaFilter extends AbstractFilter {

    private ScriptC_sepia script;

    public SepiaFilter(int imageWidth, int imageHeight, Context mainActivity, Bitmap sharedBitmap) {
        super(imageWidth, imageHeight, mainActivity, sharedBitmap);

        script = new ScriptC_sepia(rs);
        script.set_imageWidth(imageWidth);
        script.set_imageHeight(imageHeight);
        script.set_script(script);
        script.set_in(allocationIn);
        script.set_out(allocationOut);
    }

    @Override
    public void setRGB(int red, int green, int blue) {
        script.set_GS_BLUE((float) blue / 100);
        script.set_GS_GREEN((float) green / 100);
        script.set_GS_RED((float) red / 100);
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

    @Override
    public void setRgbVisible(SeekBar r, SeekBar g, SeekBar b) {
        r.setVisibility(View.VISIBLE);
        g.setVisibility(View.VISIBLE);
        b.setVisibility(View.VISIBLE);
    }

}
