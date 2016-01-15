package com.github.krukon.tutoratamicamera;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.github.krukon.tutoratamicamera.camera.CameraPreview;
import com.github.krukon.tutoratamicamera.camera.CameraService;

public class MainActivity extends Activity {
    private Camera camera;
    private CameraPreview preview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        camera = CameraService.getCamera();
        preview = new CameraPreview(camera, this);

        FrameLayout previewLayout = (FrameLayout) findViewById(R.id.camera_preview);
        previewLayout.addView(preview);
    }

    @Override
    public void onPause() {
        super.onPause();
        CameraService.shutdownCamera();
    }
}
