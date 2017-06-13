package io.lukin.reminder;

import android.widget.ArrayAdapter;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.Switch;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReminderAdapter extends ArrayAdapter<Reminder> {

    private Context context;
    private final ArrayList<Reminder> reminders;
    private AlarmReceiver alarm;
    RemindersSQLHepler databaseHelper;

    public ReminderAdapter(Context context, ArrayList<Reminder> reminders, AlarmReceiver alarmReceiver,
                           RemindersSQLHepler databaseHelper) {
        super(context, 0, reminders);
        this.context = context;
        this.reminders = reminders;
        this.alarm = alarmReceiver;
        this.databaseHelper = databaseHelper;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Reminder item = reminders.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.main_menu_reminder, parent, false);
        }
        TextView tvName = (TextView) convertView.findViewById(R.id.reminderTextView);
        final Switch sw = (Switch) convertView.findViewById(R.id.reminderItemSw);

        tvName.setText(item.getName());

        sw.setChecked(item.isEnabled());

        sw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sw.isChecked()){
                    alarm.setAlarm(context, databaseHelper.getId(item.getName()), item);
                    reminders.get(position).setState(true);
                    databaseHelper.updateReminder(item);
                    //TODO: Update database
                } else {
                    alarm.cancelAlarm(context, databaseHelper.getId(item.getName()));
                    reminders.get(position).setState(false);
                    databaseHelper.updateReminder(item);
                    //TODO: Update database
                }
            }
        });

        TextView address = (TextView) convertView.findViewById(R.id.localisationTextView);
        address.setText(item.getAddress());

        TextView time = (TextView) convertView.findViewById(R.id.reminderTimeEeditText);

        String dateStr = item.getHour() +":"+ item.getMinute();
        SimpleDateFormat curFormater = new SimpleDateFormat("H:m");
        Date dateObj = null;
        try {
            dateObj = curFormater.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat postFormater = new SimpleDateFormat("HH:mm");
        String newDateStr = postFormater.format(dateObj);

        time.setText(newDateStr);

        TextView type = (TextView) convertView.findViewById(R.id.frequencyTimeTextView);
        type.setText(item.getType());

        return convertView;
    }

    public ArrayList<Reminder> getValues(){
        return reminders;
    }

    @Override
    public int getCount() {
        return reminders.size();
    }

    @Override
    public Reminder getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

}
