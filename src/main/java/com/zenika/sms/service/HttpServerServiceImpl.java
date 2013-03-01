package com.zenika.sms.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.zenika.sms.service.server.HttpServer;

/**
 * TODO: document me.
 *
 * @author Mathieu POUSSE
 */
public class HttpServerServiceImpl extends Service implements HttpServerService {

    private final IBinder binder = new LocalServiceBinder<HttpServerService>(this);

    private HttpServer server = null;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
        NotificationSenderService notificationSenderService = new NotificationSenderService(getApplicationContext());
        this.server = new HttpServer(this, notificationSenderService);
        toast("server created");
        } catch (Throwable e) {
            Log.e("SMS-SERVER", "oops", e);
            toast("error while creating...");
        }
    }

    @Override
    public void onDestroy() {
        toast("stopping server");
        stopHttpServer();
        super.onDestroy();
    }

    @Override
    public void startHttpServer() {
        this.server.startThread();
    }

    @Override
    public void stopHttpServer() {
        this.server.stopThread();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        toast("starting server");
        startHttpServer();
        return START_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @Override
    public boolean isServerRunning() {
        return this.server != null && this.server.isRunning();
    }

    private void toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
    }

}

