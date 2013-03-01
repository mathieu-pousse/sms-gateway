package com.zenika.sms.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * TODO: document me.
 *
 * @author Mathieu POUSSE
 */
public class HttpServerServiceVoid extends Service implements HttpServerService {

    private final IBinder binder = new LocalServiceBinder<HttpServerService>(this);

    @Override
    public void onCreate() {
        super.onCreate();
        toast("service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        toast("service destroyed");
    }

    @Override
    public void startHttpServer() {
        toast("http server started");
    }

    @Override
    public void stopHttpServer() {
        toast("http server stomped");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        toast("service started");
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        toast("unbind service");
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        toast("bind service");
        return this.binder;
    }

    @Override
    public boolean isServerRunning() {
        toast("is running");
        return false;
    }

    private void toast(String message) {
        Log.i("SMS-SERVER", message);
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
    }
}
