package com.github.krukon.tutoratamicamera;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import android.view.View;
import android.widget.*;
import android.app.Activity;
import android.graphics.Bitmap;

import com.github.krukon.tutoratamicamera.camera.CameraService;
import com.github.krukon.tutoratamicamera.effects.AbstractFilter;
import com.github.krukon.tutoratamicamera.effects.BlurFilter;
import com.github.krukon.tutoratamicamera.effects.NormalFilter;


@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements Camera.PreviewCallback, SurfaceHolder.Callback {

    private ImageView outputImageView;

    private List<AbstractFilter> filters;
    private int currentFilterId;

    private volatile boolean rendering;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int imageWidth = CameraService.getCamera().getParameters().getPreviewSize().width;
        int imageHeight = CameraService.getCamera().getParameters().getPreviewSize().height;

        filters = new ArrayList<>();
        filters.add(new NormalFilter(imageWidth, imageHeight, this));
        filters.add(new BlurFilter(imageWidth, imageHeight, this));

        outputImageView = (ImageView) findViewById(R.id.outputImageView);
        SurfaceView surView = (SurfaceView) findViewById(R.id.inputSurfaceView);
        SurfaceHolder surHolder = surView.getHolder();
        surHolder.addCallback(this);

        Button nextFilterButton = (Button) findViewById(R.id.nextFilterButton);
        nextFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextFilter();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        CameraService.shutdownCamera();
    }

    private class ProcessData extends AsyncTask<byte[], Void, Boolean> {
        private Bitmap outputBitmap;

        @Override
        protected Boolean doInBackground(byte[]... args) {
            outputBitmap = currentFilter().execute(args[0]);
            return true;
        }
        protected void onPostExecute(Boolean result) {
            outputImageView.setImageBitmap(outputBitmap);
            outputImageView.invalidate();
            rendering = false;
        }
    }
    @Override
    public void onPreviewFrame(byte[] data, Camera arg1) {
        if (rendering) {
            return;
        }

        rendering = true;
        new ProcessData().execute(data);
    }

    private AbstractFilter currentFilter() {
        return filters.get(currentFilterId);
    }

    private void nextFilter() {
        ++currentFilterId;
        if (filters.size() == currentFilterId) currentFilterId = 0;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            CameraService.getCamera().setPreviewCallback(this);
            CameraService.getCamera().setPreviewDisplay(holder);
            CameraService.getCamera().startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraService.shutdownCamera();
    }

}
