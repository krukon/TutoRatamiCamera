package com.github.krukon.tutoratamicamera.camera;

import android.hardware.Camera;

/**
 * Created by jakub on 15.01.16.
 */
public class CameraService {
    private static Camera camera;

    public static Camera getCamera() {
        if (camera == null) {
            camera = Camera.open();
        }
        return camera;
    }

    public static void shutdownCamera() {
        if (camera != null) {
            camera.setPreviewCallbackWithBuffer(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}
