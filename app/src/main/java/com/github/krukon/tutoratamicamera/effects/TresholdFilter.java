package com.github.krukon.tutoratamicamera.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.view.View;
import android.widget.SeekBar;

import com.github.krukon.tutoratamicamera.ScriptC_treshold;

/**
 * Created by wiktortendera on 25/01/16.
 */
public class TresholdFilter extends AbstractFilter {


    private ScriptC_treshold script;

    public TresholdFilter(int imageWidth, int imageHeight, Context mainActivity, Bitmap sharedBitmap) {
        super(imageWidth, imageHeight, mainActivity, sharedBitmap);

        script = new ScriptC_treshold(rs);
        script.set_imageWidth(imageWidth);
        script.set_imageHeight(imageHeight);
        script.set_script(script);
        script.set_in(allocationIn);
        script.set_out(allocationOut);
    }
    @Override
    public void setRGB(int red, int green, int blue) {}

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
        return "Treshold";
    }

    @Override
    public void setTreshold(int treshold) {
        script.set_threshold((float) treshold/100);
    }

    @Override
    public void setTresholdVisible(SeekBar t) {
        t.setVisibility(View.VISIBLE);
    }
}
