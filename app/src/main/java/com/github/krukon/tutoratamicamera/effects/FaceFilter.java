package com.github.krukon.tutoratamicamera.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.renderscript.Allocation;

import com.github.krukon.tutoratamicamera.ScriptC_weleven;

/**
 * Created by jakub on 27.01.16.
 */
public class FaceFilter extends AbstractFilter {

    private Camera.Face[] faces;

    private ScriptC_weleven script;

    private Camera.Face fakeFace = new Camera.Face();
    {fakeFace.rect = new Rect(0, 0, 0, 0);}

    public FaceFilter(int imageWidth, int imageHeight, Context context, Bitmap sharedBitmap) {
        super(imageWidth, imageHeight, context, sharedBitmap);

        script = new ScriptC_weleven(rs);
        script.set_imageWidth(imageWidth);
        script.set_imageHeight(imageHeight);
        script.set_in(allocationIn);
        script.set_out(allocationOut);
        script.set_script(script);
    }

    @Override
    public void setRGB(int red, int green, int blue) {
    }

    @Override
    public Bitmap execute(byte[] data) {
        allocationYUV.copyFrom(data);
        intrinsicYuvToRGB.setInput(allocationYUV);
        intrinsicYuvToRGB.forEach(allocationIn);

        script.set_copyInput(1);
        script.invoke_filter();
        allocationOut.syncAll(Allocation.USAGE_SHARED);
        script.set_copyInput(0);

        if (faces != null && faces.length > 0) {
            for (Camera.Face face : faces) {
                if (face.score > 50) {
                    computeBlur(face);
                    allocationOut.syncAll(Allocation.USAGE_SHARED);
                }
            }
        }


        return outputBitmap;
    }

    private void computeBlur(Camera.Face face) {
        Rect scaledFaceRect = new Rect((face.rect.left + 1000) * imageWidth / 2000,
                (face.rect.top + 1000) * imageHeight / 2000,
                (face.rect.right + 1000) * imageWidth / 2000,
                (face.rect.bottom + 1000) * imageHeight / 2000);

        script.set_left(scaledFaceRect.left);
        script.set_top(scaledFaceRect.top);
        script.set_right(scaledFaceRect.right);
        script.set_bottom(scaledFaceRect.bottom);

        script.invoke_filter();

    }

    @Override
    public String getName() {
        return "W11";
    }

    public void setFaces(Camera.Face[] faces) {
        this.faces = faces;
    }
}
