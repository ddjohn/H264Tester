package com.avelon.h264tester;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Surface;

import java.util.Random;

public class SomeService extends Service {
    private static final String TAG = SomeService.class.getSimpleName();
    private static final Rect rect = new Rect(0, 0, 1280, 768);

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");
        super.onCreate();

        Intent intent = new Intent(this, RemoteDisplayService.class);
        intent.setAction(IRemoteDisplay.class.getName());
        bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected() - " + name);

                IRemoteDisplay remoteDisplay = IRemoteDisplay.Stub.asInterface(service);
                try {
                    startH264(remoteDisplay.getSurface());
                }
                catch (RemoteException e) {
                    Log.e(TAG, "exception", e);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected()");
            }
        }, Context.BIND_AUTO_CREATE);
    }

    private void startH264(Surface surface) {
        Log.e(TAG, "startH264() - " + surface);

        new Thread(() -> {
            while(true) {
                draw(surface);

                try {
                    Thread.sleep(100);
                }
                catch(InterruptedException e) {
                    Log.e(TAG, "exception", e);
                }
            }
        }).start();
    }
    private synchronized void draw(Surface surface) {
        Log.i(TAG, "draw()");
        Canvas canvas = surface.lockCanvas(rect);

        Paint paint = new Paint();
        Random random = new Random();
        paint.setColor(random.nextInt(65536)*65536 + random.nextInt(65536));
        canvas.drawCircle(random.nextInt(rect.right), random.nextInt(rect.bottom), random.nextInt(100), paint);

        surface.unlockCanvasAndPost(canvas);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}