package com.zenika.sms.service.server;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.zenika.sms.service.NotificationSenderService;
import org.apache.http.HttpException;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TODO: document me.
 *
 * @author Mathieu POUSSE
 */
public class HttpServer extends Thread {

    private static final String SERVER_NAME = "http-server";
    private static final String ALL_PATTERN = "*";
    private static final String ENDPOINT_PATTERN = "/services/send";

    private boolean isRunning = false;
    private Context context = null;
    private int serverPort = 0;

    private BasicHttpContext httpContext = null;
    private HttpService httpService = null;
    private NotificationManager notifyManager = null;

    public HttpServer(Context context, final NotificationSenderService notificationSenderService) {
        super(SERVER_NAME);

        this.setContext(context);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

        this.serverPort = 9999; //Integer.parseInt(pref.getString(Constants.PREF_SERVER_PORT, "" + Constants.DEFAULT_SERVER_PORT));
        BasicHttpProcessor httpProcessor = new BasicHttpProcessor();
        this.httpContext = new BasicHttpContext();

        httpProcessor.addInterceptor(new ResponseDate());
        httpProcessor.addInterceptor(new ResponseServer());
        httpProcessor.addInterceptor(new ResponseContent());
        httpProcessor.addInterceptor(new ResponseConnControl());

        HttpRequestHandlerRegistry registry = new HttpRequestHandlerRegistry();

        this.httpService = new HttpService(httpProcessor, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());
        this.httpService.setHandlerResolver(registry);

        registry.register(ALL_PATTERN, new HomePageHandler(context));
        registry.register(ENDPOINT_PATTERN, new EndpointHandler(context, notificationSenderService));
        Log.i("SMS-SERVER", "Http server is ready to start");
    }


    @Override
    public void run() {
        try {
            Log.i("SMS-SERVER", "starting http server");
            ServerSocket serverSocket = new ServerSocket(this.serverPort);

            serverSocket.setReuseAddress(true);

            while (this.isRunning) {
                try {
                    final Socket socket = serverSocket.accept();
                    Log.i("SMS-SERVER", "got a request ! let's delegate");
                    DefaultHttpServerConnection serverConnection = new DefaultHttpServerConnection();

                    serverConnection.bind(socket, new BasicHttpParams());

                    this.httpService.handleRequest(serverConnection, this.httpContext);

                    serverConnection.shutdown();
                } catch (IOException e) {
                    Log.e("SMS-SERVER", "Oops", e);
                } catch (HttpException e) {
                    Log.e("SMS-SERVER", "Oops", e);
                }
            }
            serverSocket.close();
        } catch (IOException e) {
            Log.e("SMS-SERVER", "Oops", e);
        }
    }

    public synchronized void startThread() {
        if (this.isRunning) {
            return;
        }
        try {
            super.start();
            this.isRunning = true;
        } catch (Exception e) {
            // oops !
            Log.e("SMS-SERVER", "oops", e);
        }
    }

    public synchronized void stopThread() {
        if (!this.isRunning) {
            return;
        }
        try {
            super.interrupt();
            this.isRunning = false;
        } catch (Exception e) {
            // oops !
            Log.e("SMS-SERVER", "oops", e);
        }
    }

    public synchronized boolean isRunning() {
        Log.d("SMS-SERVER", "isRunning = " + this.isRunning);
        return this.isRunning;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return this.context;
    }

}
