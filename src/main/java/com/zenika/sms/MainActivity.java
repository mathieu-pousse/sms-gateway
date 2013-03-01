package com.zenika.sms;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import com.zenika.sms.service.HttpServerService;
import com.zenika.sms.service.HttpServerServiceImpl;
import com.zenika.sms.service.LocalServiceBinder;
import com.zenika.sms.service.MyDatabaseHelper;
import com.zenika.sms.service.NotificationSenderService;


public class MainActivity extends ListActivity {

    private HttpServerService httpServerService;

    private final static String PREFERENCES_LISTENER_PORT = "listener.port-number";


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SMS-SERVER", "onCreate(" + savedInstanceState + ")");
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this, HttpServerServiceImpl.class);
        bindService(intent, this.mConnection, Context.BIND_AUTO_CREATE);

        Cursor mCursor = MyDatabaseHelper.getInstance(getApplicationContext()).getAllEntries();
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.activity_log_entry_renderer, mCursor, //
                new String[] {MyDatabaseHelper.LOG_ENTRY__ID, MyDatabaseHelper.LOG_ENTRY__AT, MyDatabaseHelper.LOG_ENTRY__FROM, MyDatabaseHelper.LOG_ENTRY__TO, MyDatabaseHelper.LOG_ENTRY__MESSAGE}, // columns
                new int[] {R.id.s_id, R.id.s_at, R.id.s_from, R.id.s_to, R.id.s_message});

        adapter.notifyDataSetChanged();
        setListAdapter(adapter);

        ((Switch) findViewById(R.id.serverSwitch)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                Log.i("SMS-SERVER", "handle user toggle");
                if (MainActivity.this.httpServerService.isServerRunning()) {
                    MainActivity.this.httpServerService.stopHttpServer();
                } else {
                    MainActivity.this.httpServerService.startHttpServer();
                }
            }
        });

        IntentFilter filter = new IntentFilter(NotificationSenderService.EVENT);
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("SMS-SERVER", "from service to activity !");
                adapter.swapCursor(MyDatabaseHelper.getInstance(getApplicationContext()).getAllEntries());
                abortBroadcast();
            }
        }, filter);

    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalServiceBinder<HttpServerService> binder = (LocalServiceBinder<HttpServerService>) service;
            MainActivity.this.httpServerService = binder.getInstance();

            // initialize the component status
            ((Switch) findViewById(R.id.serverSwitch)).setChecked(MainActivity.this.httpServerService.isServerRunning());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            MainActivity.this.httpServerService = null;
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.clear_consle:
                // doClearConsole();
                return true;
            case R.id.menu_settings:
                showSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void showSettings() {
        Log.i("SMS-SERVER", "showing preferences");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void stopServer() {
        this.httpServerService.stopHttpServer();
    }

    public void startServer() {
        this.httpServerService.startHttpServer();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }


}
