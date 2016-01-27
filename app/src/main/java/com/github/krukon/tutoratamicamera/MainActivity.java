package com.github.krukon.tutoratamicamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.github.krukon.tutoratamicamera.camera.CameraService;
import com.github.krukon.tutoratamicamera.effects.AbstractFilter;
import com.github.krukon.tutoratamicamera.effects.BlurFilter;
import com.github.krukon.tutoratamicamera.effects.BrightnessFilter;
import com.github.krukon.tutoratamicamera.effects.EdgeFilter;
import com.github.krukon.tutoratamicamera.effects.FaceFilter;
import com.github.krukon.tutoratamicamera.effects.FlipFilter;
import com.github.krukon.tutoratamicamera.effects.MonochromeFilter;
import com.github.krukon.tutoratamicamera.effects.NegativeFilter;
import com.github.krukon.tutoratamicamera.effects.NormalFilter;
import com.github.krukon.tutoratamicamera.effects.SepiaFilter;
import com.github.krukon.tutoratamicamera.effects.TresholdFilter;
import com.github.krukon.tutoratamicamera.effects.VignetteFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements Camera.PreviewCallback, Camera.FaceDetectionListener, SurfaceHolder.Callback {

    private ImageView outputImageView;
    private Button mNextFilterButton;

    private List<AbstractFilter> filters;
    private int currentFilterId;
    private Bitmap bitmap;

    private volatile boolean rendering;
    private SeekBar r;
    private SeekBar g;
    private SeekBar b;
    private SeekBar t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int imageWidth = CameraService.getCamera().getParameters().getPreviewSize().width;
        int imageHeight = CameraService.getCamera().getParameters().getPreviewSize().height;

        r = (SeekBar) findViewById(R.id.seekBarRed);
        g = (SeekBar) findViewById(R.id.seekBarGreen);
        b = (SeekBar) findViewById(R.id.seekBarBlue);
        t = (SeekBar) findViewById(R.id.seekBarTreshold);

        bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888);
        addFilters(imageWidth, imageHeight);

        outputImageView = (ImageView) findViewById(R.id.outputImageView);
        SurfaceView surView = (SurfaceView) findViewById(R.id.inputSurfaceView);
        SurfaceHolder surHolder = surView.getHolder();
        surHolder.addCallback(this);

        mNextFilterButton = (Button) findViewById(R.id.nextFilterButton);
        mNextFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextFilter();
                refreshButtonLabel();
            }
        });
        refreshButtonLabel();
        seekBarListeners();
        filters.get(currentFilterId).setRgbVisible(r, g, b);
        filters.get(currentFilterId).setTresholdVisible(t);
    }

    private void addFilters(int imageWidth, int imageHeight) {
        filters = new ArrayList<AbstractFilter>();
        filters.add(new NormalFilter(imageWidth, imageHeight, this, bitmap));
        filters.add(new BlurFilter(imageWidth, imageHeight, this, bitmap));
        filters.add(new MonochromeFilter(imageWidth, imageHeight, this, bitmap));
        filters.add(new SepiaFilter(imageWidth, imageHeight, this, bitmap));
        filters.add(new VignetteFilter(imageWidth, imageHeight, this, bitmap));
        filters.add(new BrightnessFilter(imageWidth, imageHeight, this, bitmap));
        filters.add(new FlipFilter(imageWidth, imageHeight, this, bitmap));
        filters.add(new NegativeFilter(imageWidth, imageHeight, this, bitmap));
        filters.add(new TresholdFilter(imageWidth, imageHeight, this, bitmap));
        filters.add(new FaceFilter(imageWidth, imageHeight, this, bitmap));
        filters.add(new EdgeFilter(imageWidth, imageHeight, this, bitmap));
    }

    private void setRgb(int r, int g, int b) {
        for (AbstractFilter filter : filters) {
            filter.setRGB(r, g, b);
        }
    }

    private void setTreshold(int progress) {
        for (AbstractFilter filter : filters) {
            filter.setTreshold(progress);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        CameraService.shutdownCamera();
    }

    @Override
    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
        if (!rendering && currentFilter() instanceof FaceFilter) {
            FaceFilter faceFilter = (FaceFilter) currentFilter();
            faceFilter.setFaces(faces);
        }
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

        filters.get(currentFilterId).setRgbVisible(r,g,b);
        filters.get(currentFilterId).setTresholdVisible(t);

        if (currentFilter() instanceof FaceFilter) {
            CameraService.getCamera().startFaceDetection();
        } else {
            CameraService.getCamera().stopFaceDetection();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            CameraService.getCamera().setPreviewCallback(this);
            CameraService.getCamera().setPreviewDisplay(holder);
            CameraService.getCamera().setFaceDetectionListener(this);
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

    private void refreshButtonLabel() {
        mNextFilterButton.setText(currentFilter().getName());
    }

    private void seekBarListeners() {

        r.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setRgb(r.getProgress(), g.getProgress(), b.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        g.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setRgb(r.getProgress(), g.getProgress(), b.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        b.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setRgb(r.getProgress(), g.getProgress(), b.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        t.setOnSeekBarChangeListener((new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setTreshold(t.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        }));
    }


}
