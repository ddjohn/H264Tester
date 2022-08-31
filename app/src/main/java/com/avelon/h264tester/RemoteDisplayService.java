package com.avelon.h264tester;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.util.Random;

public class RemoteDisplayService extends Service {
    private static final String TAG = RemoteDisplayService.class.getSimpleName();
    private static final Rect rect = new Rect(0, 0, 1280, 768);
    private Surface surface;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand()");

        try {
            MediaCodec codec = getCodec();
            Log.i(TAG, "Created codec: " + codec.getName());

            surface = codec.createInputSurface();
            Log.i(TAG, "Create surface:" + surface);
/*
            DisplayManager manager = (DisplayManager) this.getSystemService(Context.DISPLAY_SERVICE);
            VirtualDisplay display = manager.createVirtualDisplay("name", 600, 400, 160, surface, 0);
            Log.e(TAG, "Display: " + display);
*/
            String file = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath() + "/a.h264";
            Log.e(TAG, "using file " + file);
            H264MediaCodec stream = new H264MediaCodec(codec, file);
            stream.start();
        }

        catch(IOException e) {
            Log.e(TAG, "exception", e);
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() - " + intent);
        return new IRemoteDisplay.Stub() {
            @Override
            public Surface getSurface() throws RemoteException {
                Log.e(TAG, "getSurface() - " + surface);
                return surface;
            }
        };
    }

    public MediaCodec getCodec() throws IOException {
        MediaCodec codec = MediaCodec.createByCodecName("OMX.google.h264.encoder");
        //MediaCodec codec = MediaCodec.createByCodecName("OMX.Intel.hw_ve.h264");

        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, rect.right, rect.bottom);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, 350000);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 1);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 0);
        codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

        return codec;
    }
}
