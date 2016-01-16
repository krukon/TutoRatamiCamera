package com.github.krukon.tutoratamicamera.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.util.Log;

import java.nio.ByteBuffer;

/**
 * Created by jakub on 15.01.16.
 */
public class AbstractFilter {

    private RenderScript rs;

    private ScriptIntrinsicYuvToRGB intrinsicYuvToRGB;

    private Allocation allocationYUV;
    private Allocation allocationOut;


    private final int width, height;

    public AbstractFilter(int width, int height, Context context) {
        this.width = width;
        this.height = height;

        rs = RenderScript.create(context);

        Type.Builder typeYUV = new Type.Builder(rs, Element.createPixel(rs, Element.DataType.UNSIGNED_8, Element.DataKind.PIXEL_YUV));
        typeYUV.setYuvFormat(ImageFormat.NV21);

        allocationYUV = Allocation.createTyped(rs, typeYUV.setX(width).setY(height).create(), Allocation.USAGE_SCRIPT);
        intrinsicYuvToRGB = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));

    }

    public Bitmap execute(byte[] data) {
        Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int i = 1; i < 100; ++i)
            for (int j = 1; j < 100; ++j)
                outputBitmap.setPixel(i, j, Color.argb(1, 255, 255, 0));

        System.out.println("Process: " + outputBitmap.getPixel(1, 1));
//        allocationOut = Allocation.createFromBitmap(rs, outputBitmap);
//
//        allocationYUV.copyFrom(data);
//        intrinsicYuvToRGB.setInput(allocationYUV);
//        intrinsicYuvToRGB.forEach(allocationOut);
//
////        allocationOut.syncAll(Allocation.USAGE_SHARED);
//
//        System.out.println(outputBitmap.getPixel(1, 1));
////        System.out.println(allocationOut.getBytesSize());

        return outputBitmap;
    }
}
