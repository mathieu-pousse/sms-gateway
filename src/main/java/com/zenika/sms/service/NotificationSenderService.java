package com.zenika.sms.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import com.zenika.sms.bo.LogEntry;


/**
 * TODO: document me.
 *
 * @author Mathieu POUSSE
 */
public class NotificationSenderService {

    public static final String EVENT = "NotificationSenderService.publish";

    public static Uri LOG_ENTRY_URI = Uri.parse("content://sms-server/entries");

    public static String LOG_ENTRY__AT = "at";
    public static String LOG_ENTRY__FROM = "from";
    public static String LOG_ENTRY__TO = "to";
    public static String LOG_ENTRY__MESSAGE = "message";
    public static String LOG_ENTRY__FAKE = "fake";

    private Context context;

    /**
     * Default constructor.
     */
    public NotificationSenderService(Context context) {
        this.context = context;
    }

    public boolean send(String from, String to, String message) {
        boolean fake = PreferenceManager.getDefaultSharedPreferences(this.context).getBoolean("listener.fake", true);
        if (!fake) {
            Log.i("SMS-SERVER", "sending sms...");
            //SmsManager.getDefault().sendTextMessage(to, null, message, null, null);
        } else {
            Log.i("SMS-SERVER", "FAKE ! Not sending sms...");
        }
        publish(from, to, message);
        return true;
    }

    private void publish(final String from, final String to, final String message) {
        LogEntry entry = new LogEntry(from, to, message);
        Log.i("SMS-SERVER", "added with id " + MyDatabaseHelper.getInstance(this.context).addLogEntry(entry));
        this.context.sendBroadcast(new Intent(EVENT));
    }

}
