package com.github.krukon.tutoratamicamera.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.renderscript.Allocation;

/**
 * Created by jakub on 27.01.16.
 */
public class FaceFilter extends AbstractFilter {

    private Camera.Face[] faces;

    public FaceFilter(int imageWidth, int imageHeight, Context context, Bitmap sharedBitmap) {
        super(imageWidth, imageHeight, context, sharedBitmap);
    }

    @Override
    public void setRGB(int red, int green, int blue) {

    }

    @Override
    public Bitmap execute(byte[] data) {
        allocationYUV.copyFrom(data);
        intrinsicYuvToRGB.setInput(allocationYUV);
        intrinsicYuvToRGB.forEach(allocationOut);

        allocationOut.syncAll(Allocation.USAGE_SHARED);

        if (faces != null && faces.length > 0)
            for (Camera.Face face : faces) {
                if (face.score > 50) {
                    computeBlur(face);
                }
            }

        return outputBitmap;
    }

    private void computeBlur(Camera.Face face) {
        System.out.println(face);
        Rect scaledFaceRect = new Rect((face.rect.left + 1000) * imageWidth / 2000,
                (face.rect.top + 1000) * imageHeight / 2000,
                (face.rect.right + 1000) * imageWidth / 2000,
                (face.rect.bottom + 1000) * imageHeight / 2000);

        for (int i = scaledFaceRect.left; i < scaledFaceRect.right; ++i) {
            for (int j = scaledFaceRect.top; j < scaledFaceRect.bottom; ++j) {
                outputBitmap.setPixel(i, j, Color.BLACK);
            }
        }
    }

    @Override
    public String getName() {
        return "Face detection";
    }

    public void setFaces(Camera.Face[] faces) {
        this.faces = faces;
    }
}
