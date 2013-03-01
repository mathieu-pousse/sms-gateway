package com.zenika.sms.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import com.zenika.sms.bo.LogEntry;

/**
 * TODO: document me.
 *
 * @author Mathieu POUSSE
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static MyDatabaseHelper instance;

    public static MyDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new MyDatabaseHelper(context);
        }
        return instance;
    }


    private static final String DATABASE_NAME = "sms_server.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "log_entry";

    public static Uri LOG_ENTRY_URI = Uri.parse("content://sms-server/entries");

    public static String LOG_ENTRY__ID = BaseColumns._ID;
    public static String LOG_ENTRY__AT = "i_at";
    public static String LOG_ENTRY__FROM = "s_from";
    public static String LOG_ENTRY__TO = "s_to";
    public static String LOG_ENTRY__MESSAGE = "s_message";

    public static final int SHUTTER_NAME_INDEX = 1;
    public static final int ID_SERVER_INDEX = 2;
    public static final int STATE_INDEX = 3;

    public static final String[] PROJECTION = new String[] {LOG_ENTRY__ID, LOG_ENTRY__AT, LOG_ENTRY__FROM, LOG_ENTRY__TO, LOG_ENTRY__MESSAGE};

    private static final String ID_SELECTION = LOG_ENTRY__ID + " = ?";

    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.i("SMS-SERVER", "installing database");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL("create table " + TABLE_NAME + " (" + LOG_ENTRY__ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LOG_ENTRY__AT + " INTEGER, " + LOG_ENTRY__FROM + " TEXT, " + LOG_ENTRY__TO + " TEXT, " + LOG_ENTRY__MESSAGE + " TEXT);");
        } catch (Throwable t) {
            Log.e("SMS-SERVER", "oops", t);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addLogEntry(ContentValues values) {
        Log.d("SMS-SERVER", "adding entry");
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_NAME, null, values);
    }

    public long addLogEntry(LogEntry entry) {
        return addLogEntry(toCV(entry));
    }

    private static ContentValues toCV(LogEntry entry) {
        ContentValues values = new ContentValues(4);

        values.put(LOG_ENTRY__AT, entry.getAt().getTime());
        values.put(LOG_ENTRY__FROM, entry.getFrom());
        values.put(LOG_ENTRY__TO, entry.getTo());
        values.put(LOG_ENTRY__MESSAGE, entry.getMessage());

        return values;
    }

    public Cursor getAllEntries() {
        Log.d("SMS-SERVER", "select all");
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_NAME, PROJECTION, null, null, null, null, null);
    }

    public int deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_NAME, null, null);
    }

}