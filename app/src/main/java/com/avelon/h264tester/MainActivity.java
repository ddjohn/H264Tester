package com.avelon.h264tester;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Log.i(TAG, "Start service...");
        Intent remoteDispayIntent = new Intent(this, RemoteDisplayService.class);
        startService(remoteDispayIntent);

        Intent someServiceIntent = new Intent(this, SomeService.class);
        startService(someServiceIntent);
    }
}
