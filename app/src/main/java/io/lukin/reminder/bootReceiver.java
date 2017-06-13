package io.lukin.reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by maciek on 10.06.17.
 */

public class bootReceiver extends BroadcastReceiver {

    AlarmReceiver alarm = new AlarmReceiver();

    RemindersSQLHepler databaseHelper;
    ArrayList<Reminder> reminders;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        reminders = databaseHelper.getAllReminders();

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            for(Reminder r : reminders) {
                if(r.isEnabled()){
                    alarm.setAlarm(context, databaseHelper.getId(r.getName()), r);
                }
            }
            Toast.makeText(context, "All alarms has been set.", Toast.LENGTH_SHORT).show();
        }
    }
}