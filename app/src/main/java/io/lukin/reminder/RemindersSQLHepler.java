package io.lukin.reminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Parcel;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.internal.PlaceEntity;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by maciek on 09.06.17.
 */



public class RemindersSQLHepler extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "RemindersSQLite.db";
    private static final int DATABASE_VERSION = 1;
    public static final String REMINDER_TABLE = "REMINDER";
    public static final String REMINDER_COLUMN_ID = "_ID";
    public static final String REMINDER_COLUMN_LABEL = "LABEL";
    public static final String REMINDER_COLUMN_HOUR = "HOUR";
    public static final String REMINDER_COLUMN_MINUTE = "MINUTE";
    public static final String REMINDER_COLUMN_TYPE = "TYPE";
    public static final String REMINDER_COLUMN_ADDRESS = "ADDRESS";
    public static final String REMINDER_COLUMN_LAT = "LAT";
    public static final String REMINDER_COLUMN_LON = "LON";
    public static final String REMINDER_COLUMN_ENABLED = "ENABLED";

    public RemindersSQLHepler(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + REMINDER_TABLE + "(" +
                REMINDER_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                REMINDER_COLUMN_LABEL + " TEXT, " +
                REMINDER_COLUMN_HOUR + " INTEGER, " +
                REMINDER_COLUMN_MINUTE + " INTEGER, " +
                REMINDER_COLUMN_TYPE + " TEXT, " +
                REMINDER_COLUMN_ADDRESS + " TEXT, " +
                REMINDER_COLUMN_LAT + " DOUBLE, " +
                REMINDER_COLUMN_LON + " DOUBLE, " +
                REMINDER_COLUMN_ENABLED + " BIT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + REMINDER_TABLE);
        onCreate(db);
    }

    public boolean insertReminder(Reminder reminder) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(REMINDER_COLUMN_LABEL, reminder.getName());
        contentValues.put(REMINDER_COLUMN_HOUR, reminder.getHour());
        contentValues.put(REMINDER_COLUMN_MINUTE, reminder.getMinute());
        contentValues.put(REMINDER_COLUMN_TYPE, reminder.getType());
        contentValues.put(REMINDER_COLUMN_ADDRESS, reminder.getAddress());
        contentValues.put(REMINDER_COLUMN_LAT, reminder.getPlace().latitude);
        contentValues.put(REMINDER_COLUMN_LON, reminder.getPlace().longitude);
        contentValues.put(REMINDER_COLUMN_ENABLED, reminder.isEnabled());
        db.insert(REMINDER_TABLE, null, contentValues);
        return true;
    }

    public boolean updateReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(REMINDER_COLUMN_LABEL, reminder.getName());
        contentValues.put(REMINDER_COLUMN_HOUR, reminder.getHour());
        contentValues.put(REMINDER_COLUMN_MINUTE, reminder.getMinute());
        contentValues.put(REMINDER_COLUMN_TYPE, reminder.getType());
        contentValues.put(REMINDER_COLUMN_ADDRESS, reminder.getAddress());
        contentValues.put(REMINDER_COLUMN_LAT, reminder.getPlace().latitude);
        contentValues.put(REMINDER_COLUMN_LON, reminder.getPlace().longitude);
        contentValues.put(REMINDER_COLUMN_ENABLED, reminder.isEnabled());
        db.update(REMINDER_TABLE, contentValues, REMINDER_COLUMN_LABEL + " = ? ", new String[] { reminder.getName() } );
        return true;
    }

    public ArrayList<Reminder> getAllReminders() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + REMINDER_TABLE, null );

        ArrayList<Reminder> reminders = new ArrayList<>();

        for(res.moveToFirst(); !res.isAfterLast(); res.moveToNext()) {
            String label = res.getString(res.getColumnIndex(REMINDER_COLUMN_LABEL));
            int hour = res.getInt(res.getColumnIndex(REMINDER_COLUMN_HOUR));
            int minute = res.getInt(res.getColumnIndex(REMINDER_COLUMN_MINUTE));
            String type = res.getString(res.getColumnIndex(REMINDER_COLUMN_TYPE));
            String address = res.getString(res.getColumnIndex(REMINDER_COLUMN_ADDRESS));
            double lat = res.getDouble(res.getColumnIndex(REMINDER_COLUMN_LAT));
            double lon = res.getDouble(res.getColumnIndex(REMINDER_COLUMN_LON));
            boolean enabled = res.getInt(res.getColumnIndex(REMINDER_COLUMN_ENABLED)) > 0;

            Reminder reminder = new Reminder();
            reminder.setName(label);
            reminder.setHour(hour);
            reminder.setMinute(minute);
            reminder.setState(enabled);
            reminder.setType(type);
            reminder.setPlace(new LatLng(lat, lon));
            reminder.setAddress(address);
            reminders.add(reminder);
        }
        return reminders;
    }

    public Integer deleteReminder(String label) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(REMINDER_TABLE,
                REMINDER_COLUMN_LABEL + " = ? ",
                new String[] { label });
    }

    public Integer getId(String label){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + REMINDER_TABLE +
                " WHERE " + REMINDER_COLUMN_LABEL + " = '" + label + "'", null );
        if (res.moveToFirst()) {
            return res.getInt(res.getColumnIndex(REMINDER_COLUMN_ID));
        }
        return 0;
    }




}
