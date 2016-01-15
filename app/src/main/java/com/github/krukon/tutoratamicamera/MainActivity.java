package com.github.krukon.tutoratamicamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.krukon.tutoratamicamera.camera.CameraPreview;
import com.github.krukon.tutoratamicamera.camera.CameraService;
import com.github.krukon.tutoratamicamera.effects.AbstractFilter;

public class MainActivity extends Activity implements Camera.PreviewCallback {
    private Camera camera;
    private CameraPreview hiddenPreview;
    private ImageView renderedPreview;

    private volatile boolean rendering;

    private AbstractFilter[] filters;
    private int currentFilterId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        camera = CameraService.getCamera();
        camera.setPreviewCallback(this);

        filters = new AbstractFilter[] {new AbstractFilter(camera.getParameters().getPreviewSize().width, camera.getParameters().getPreviewSize().height, this)};

        hiddenPreview = new CameraPreview(camera, this);
        FrameLayout previewLayout = (FrameLayout) findViewById(R.id.camera_preview);
        previewLayout.addView(hiddenPreview);

        renderedPreview = (ImageView)findViewById(R.id.outputImageView);
    }

    @Override
    public void onPause() {
        super.onPause();
        CameraService.shutdownCamera();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (rendering) return;

        rendering = true;
        Bitmap bm = getCurrentFilter().execute(data); //TODO - should be async
        renderedPreview.setImageBitmap(bm);
        renderedPreview.invalidate();
        rendering = false;
    }

    public AbstractFilter getCurrentFilter() {
        return filters[currentFilterId];
    }
}
