package com.zenika.sms.service.server;

import android.content.Context;
import android.util.Log;
import com.zenika.sms.service.NotificationSenderService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

/**
 * TODO: document me.
 *
 * @author Mathieu POUSSE
 */
public class EndpointHandler implements HttpRequestHandler {

    private Context context = null;

    private NotificationSenderService notificationSenderService;

    public EndpointHandler(Context context, final NotificationSenderService notificationSenderService) {
        this.context = context;
        this.notificationSenderService = notificationSenderService;
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response, HttpContext httpContext) throws HttpException, IOException {
        Log.i("SMS-SERVER", "got a message to send !");
        //        this.notificationSenderService.send("+33 6 28 89 08 43", "+33 6 28 89 08 43", "<to be implemented>");

        if (request instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest) request).getEntity();
            if (entity != null) {
                String content = EntityUtils.toString(entity, "UTF-8");
                Log.v("RequestBody", content);
                entity.consumeContent();


                try {
                    JSONObject jsonObject = new JSONObject(content);
                    if (!jsonObject.has("from")) {
                        response.setStatusLine(request.getProtocolVersion(), 400, "Missing 'from' attribute");
                        return;
                    }
                    if (!jsonObject.has("to")) {
                        response.setStatusLine(request.getProtocolVersion(), 400, "Missing 'to' attribute");
                        return;
                    }
                    if (!jsonObject.has("message")) {
                        response.setStatusLine(request.getProtocolVersion(), 400, "Missing 'message' attribute");
                        return;
                    }

                    String from = jsonObject.getString("from");
                    String to = jsonObject.getString("to");
                    String message = jsonObject.getString("message");

                    Log.i("SMS-SERVER", from + " > " + to + " = " + message);

                    if (!this.notificationSenderService.send(from, to, message)) {
                        response.setStatusCode(500);
                    } else {
                        response.setStatusCode(200);
                    }
                } catch (Exception e) {
                    Log.e("SMS-SERVER", "oops", e);
                }
            }
        }
    }

}
