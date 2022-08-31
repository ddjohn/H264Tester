package com.avelon.h264tester;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class H264MediaCodec extends MediaCodec.Callback {
    private static final String TAG = H264MediaCodec.class.getSimpleName();
    private final MediaCodec encoder;
    private final FileOutputStream stream;

    public H264MediaCodec(final MediaCodec h264Encoder, String file) throws FileNotFoundException {
        encoder = h264Encoder;
        encoder.setCallback(this);

        stream = new FileOutputStream(file);
    }

    void start() {
        encoder.start();
    }

    @Override
    public void onInputBufferAvailable(MediaCodec codec, int index) {
        Log.e(TAG, "onInputBufferAvailable()");
    }

    @Override
    public void onError(MediaCodec codec, MediaCodec.CodecException e) {
        Log.e(TAG, "onError()");
    }

    @Override
    public void onOutputFormatChanged(MediaCodec codec, MediaFormat format) {
        Log.i(TAG, "onOutputFormatChanged() - " + format);
    }

    @Override
    public void onOutputBufferAvailable(MediaCodec codec, int index, MediaCodec.BufferInfo info) {
        Log.e(TAG, "onOutputBufferAvailable() - " + index);
        ByteBuffer buffer = codec.getOutputBuffer(index);

        try {
            while(buffer.hasRemaining())
                stream.write(buffer.get());
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        buffer.rewind();

        Log.e(TAG, "buffer: " + new H264Printer(buffer));

        /*String tmp = "";
        for(int i = 0; i < 10; i++) {
            tmp += " " + buffer.get();
        }
        Log.e(TAG, "buffer available: " + tmp);
*/

        buffer.clear();
        codec.releaseOutputBuffer(index, false);
    }
}