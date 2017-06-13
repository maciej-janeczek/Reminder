package io.lukin.reminder;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Thread.sleep;


/**
 * Created by maciek on 10.06.17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    Location current = new Location("");
    LocationManager locationManager;

    @Override
    public void onReceive(final Context context, Intent intent) {

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wl.acquire();
        String label = intent.getStringExtra("label");
        String address = intent.getStringExtra("address");
        double lat = intent.getDoubleExtra("lat", 0);
        double lon = intent.getDoubleExtra("lon", 0);

        Location target = new Location("");
        target.setLatitude(lat);
        target.setLongitude(lon);
        double distance = -1;

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "No permissions.", Toast.LENGTH_SHORT).show();
                // TODO: Consider calling
                return;
            }

            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        current = bestLocation;
        if(current == null){
            Toast.makeText(context, "Couldn't obtain localisation.", Toast.LENGTH_SHORT).show();
            return;
        }

        distance = target.distanceTo(current);

        if(distance < 200 + current.getAccuracy() ) {
            Intent i = new Intent(context, AlarmActivity.class);
            i.putExtra("label", label);
            i.putExtra("address", address);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Toast.makeText(context, "Distance is " + Integer.toString((int)distance) + "m", Toast.LENGTH_SHORT).show();
            context.startActivity(i);
        }else {
            Toast.makeText(context, "Location too far. Distance is " + Integer.toString((int)distance) + "m", Toast.LENGTH_SHORT).show();

        }
        RemindersSQLHepler databaseHelper = new RemindersSQLHepler(context);
        ArrayList<Reminder> reminders = databaseHelper.getAllReminders();
        for (Reminder r : reminders) {
            if (r.getName().contentEquals(label)) {
                setAlarm(context, databaseHelper.getId(label), r);
            }
        }
        wl.release();
    }

    public void setAlarm(final Context context, int id, Reminder reminder)
    {

        // Set the alarm to start at specific hour
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(System.currentTimeMillis());
        time.set(Calendar.HOUR_OF_DAY, reminder.getHour());
        time.set(Calendar.MINUTE, reminder.getMinute());
        time.set(Calendar.SECOND, 0);

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        i.setAction("actionstring" + System.currentTimeMillis());
        i.putExtra("label", reminder.getName());
        i.putExtra("address", reminder.getAddress());
        i.putExtra("lat", reminder.getPlace().latitude);
        i.putExtra("lon", reminder.getPlace().longitude);
        PendingIntent pi = PendingIntent.getBroadcast(context, id, i, 0);
        if(time.before(Calendar.getInstance()))
            time.add(Calendar.DATE, 1);
        int day = time.get(Calendar.DAY_OF_WEEK);
        if(reminder.getType().contentEquals("Weekends")){
            if(day == Calendar.MONDAY)
                time.add(Calendar.DATE, 5);
            if(day == Calendar.TUESDAY)
                time.add(Calendar.DATE, 4);
            if(day == Calendar.WEDNESDAY)
                time.add(Calendar.DATE, 3);
            if(day == Calendar.THURSDAY)
                time.add(Calendar.DATE, 2);
            if(day == Calendar.FRIDAY)
                time.add(Calendar.DATE, 1);
        }else if(reminder.getType().contentEquals("Monday - Friday")){
            if(day == Calendar.SATURDAY)
                time.add(Calendar.DATE, 2);
            if(day == Calendar.SUNDAY)
                time.add(Calendar.DATE, 1);
        }
        am.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), pi);
        long diff = time.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60))/ (1000 * 60);

        Toast.makeText(context, "Days: " + Long.toString(days) + " , Hours: " + Long.toString(hours) + " , Minutes: " + Long.toString(minutes) + " left.", Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarm(Context context, int id)
    {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
