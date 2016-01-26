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
public class MainActivity extends Activity implements Camera.PreviewCallback, SurfaceHolder.Callback {

    private ImageView outputImageView;
    private Button mNextFilterButton;

    private List<AbstractFilter> filters;
    private int currentFilterId;

    private volatile boolean rendering;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int imageWidth = CameraService.getCamera().getParameters().getPreviewSize().width;
        int imageHeight = CameraService.getCamera().getParameters().getPreviewSize().height;

        addFilters(imageWidth, imageHeight, 30, 59, 11);



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
    }

    private void addFilters(int imageWidth, int imageHeight, int red, int green, int blue) {
        filters = new ArrayList<AbstractFilter>();
        filters.add(new NormalFilter(imageWidth, imageHeight, this, red, green, blue));
        filters.add(new MonochromeFilter(imageWidth, imageHeight, this, red, green, blue));
        filters.add(new SepiaFilter(imageWidth, imageHeight, this, red, green, blue));
        filters.add(new VignetteFilter(imageWidth, imageHeight, this, red, green, blue));
        filters.add(new BrightnessFilter(imageWidth, imageHeight, this, red, green, blue));
        filters.add(new FlipFilter(imageWidth, imageHeight, this, red, green, blue));
        filters.add(new NegativeFilter(imageWidth, imageHeight, this, red, green, blue));
        filters.add(new TresholdFilter(imageWidth, imageHeight, this, red, green, blue));
        filters.add(new EdgeFilter(imageWidth, imageHeight, this, red, green, blue));
        filters.add(new BlurFilter(imageWidth, imageHeight, this, red, green, blue));
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

    private void refreshButtonLabel() {
        mNextFilterButton.setText(currentFilter().getName());
    }

    private void seekBarListeners() {
        final SeekBar seekBarRed=(SeekBar) findViewById(R.id.seekBarRed);
        final SeekBar seekBarGreen=(SeekBar) findViewById(R.id.seekBarGreen);
        final SeekBar seekBarBlue=(SeekBar) findViewById(R.id.seekBarBlue);

        seekBarRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int imageWidth = CameraService.getCamera().getParameters().getPreviewSize().width;
                int imageHeight = CameraService.getCamera().getParameters().getPreviewSize().height;

                addFilters(imageWidth, imageHeight, seekBarRed.getProgress(), seekBarGreen.getProgress(), seekBarBlue.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int imageWidth = CameraService.getCamera().getParameters().getPreviewSize().width;
                int imageHeight = CameraService.getCamera().getParameters().getPreviewSize().height;

                addFilters(imageWidth, imageHeight, seekBarRed.getProgress(), seekBarGreen.getProgress(), seekBarBlue.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int imageWidth = CameraService.getCamera().getParameters().getPreviewSize().width;
                int imageHeight = CameraService.getCamera().getParameters().getPreviewSize().height;

                addFilters(imageWidth, imageHeight, seekBarRed.getProgress(), seekBarGreen.getProgress(), seekBarBlue.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
}
