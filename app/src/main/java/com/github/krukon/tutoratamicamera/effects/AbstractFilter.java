package com.github.krukon.tutoratamicamera.effects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

/**
 * Created by jakub on 15.01.16.
 */
public abstract class AbstractFilter {

    protected final int imageWidth;
    protected final int imageHeight;

    protected RenderScript rs;

    protected ScriptIntrinsicYuvToRGB intrinsicYuvToRGB;

    protected Allocation allocationYUV;
    protected Allocation allocationOut;
    protected Allocation allocationIn;
    protected Bitmap outputBitmap;

    public AbstractFilter(int imageWidth, int imageHeight, Context context, Bitmap sharedBitmap) {
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;

        rs = RenderScript.create(context);

        Type.Builder typeYUV = new Type.Builder(rs, Element.createPixel(rs, Element.DataType.UNSIGNED_8, Element.DataKind.PIXEL_YUV));
        typeYUV.setYuvFormat(ImageFormat.NV21);
        allocationYUV = Allocation.createTyped(rs, typeYUV.setX(imageWidth).setY(imageHeight).create(), Allocation.USAGE_SCRIPT);

        intrinsicYuvToRGB = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
        outputBitmap = sharedBitmap;
        allocationOut = Allocation.createFromBitmap(rs, outputBitmap);
        allocationIn =  Allocation.createTyped(rs, allocationOut.getType(), Allocation.USAGE_SCRIPT);
    }

    public abstract void setRGB(int red, int green, int blue);

    public abstract Bitmap execute(byte[] data);

    public abstract  String getName();

}
