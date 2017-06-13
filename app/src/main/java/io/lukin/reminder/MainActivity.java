package io.lukin.reminder;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int TIME_ACTIVITY_RESULT_CODE = 2;
    private static final int EDIT_ACTIVITY_RESULT_CODE = 3;

    ArrayList<Reminder> reminders;
    Reminder newReminder;
    ReminderAdapter reminderAdapter;
    RemindersSQLHepler databaseHelper;

    AlarmReceiver alarm = new AlarmReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reminders = new ArrayList<>();

        databaseHelper = new RemindersSQLHepler(this);

        reminders = databaseHelper.getAllReminders();


        final ListView listView = (ListView) findViewById(R.id.mainGridView);
        if (savedInstanceState != null) {
            newReminder = savedInstanceState.getParcelable("newReminder");
        }else{
            newReminder = new Reminder();
        }
        reminderAdapter =  new ReminderAdapter(this, reminders, alarm, databaseHelper);

        listView.setAdapter(reminderAdapter);

        addButtonDescription(reminderAdapter);
        locationButtonDescription();
        dateButtonDescription();

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
            {
                Intent intent = new Intent(MainActivity.this, EditReminderActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("reminder", reminders.get(position));
                intent.putExtras(bundle);
                intent.putExtra("position", position);
                startActivityForResult(intent, EDIT_ACTIVITY_RESULT_CODE);
            }
        });

        ComponentName receiver = new ComponentName(getApplication(), bootReceiver.class);
        PackageManager pm = getBaseContext().getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void addButtonDescription(final ReminderAdapter reminderAdapter){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.buttonAdd);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                AutoCompleteTextView textInput = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
                String text = textInput.getText().toString();
                String trimmedName = text.trim();
                if(trimmedName.length() > 0 && newReminder.getPlace() != null && newReminder.timeIsSet()){

                    boolean found = false;
                    for(Reminder r : reminders)
                        if (r.getName().contentEquals(trimmedName)) found = true;

                    if(found){
                        Toast.makeText(MainActivity.this.getApplicationContext(), "Already exist reminder with such name, try harder :)", Toast.LENGTH_LONG).show();
                        textInput.setText("");
                    }else{
                        newReminder.setName(trimmedName);

                        newReminder.setState(true);

                        reminders.add(newReminder);
                        databaseHelper.insertReminder(newReminder);

                        reminderAdapter.notifyDataSetChanged();

                        textInput.setText("");

                        CheckBox locationCheckBox = (CheckBox) findViewById(R.id.locationCheckBox);
                        locationCheckBox.setChecked(false);

                        CheckBox timeCheckBox = (CheckBox) findViewById(R.id.dateCheckBox);
                        timeCheckBox.setChecked(false);

                        alarm.setAlarm(getApplicationContext(), databaseHelper.getId(newReminder.getName()), newReminder);

                        newReminder = new Reminder();
                    }
                }else{
                    Toast.makeText(MainActivity.this.getApplicationContext(), "You have to fill all the parameters.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void locationButtonDescription(){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.buttonMap);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = builder.build(MainActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putParcelable("newReminder", newReminder);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(this, data);
                    newReminder.setPlace(place.getLatLng());
                    newReminder.setAddress(place.getAddress().toString());

                    CheckBox locationCheckBox = (CheckBox) findViewById(R.id.locationCheckBox);
                    locationCheckBox.setChecked(true);
                    return;
                case TIME_ACTIVITY_RESULT_CODE:
                    newReminder.setType(data.getStringExtra("type"));
                    newReminder.setMinute(data.getIntExtra("minute", -1));
                    newReminder.setHour(data.getIntExtra("hour", -1));
                    CheckBox timeCheckBox = (CheckBox) findViewById(R.id.dateCheckBox);
                    timeCheckBox.setChecked(true);
                    return;
                case EDIT_ACTIVITY_RESULT_CODE:
                    boolean delete = data.getBooleanExtra("delete", false);
                    if(delete){
                        Toast.makeText(MainActivity.this, "Delete",  Toast.LENGTH_SHORT).show();
                        int position = data.getIntExtra("position", 0);
                        databaseHelper.deleteReminder(reminders.get(position).getName());
                        reminders.remove(position);
                        reminderAdapter.notifyDataSetChanged();
                    }else{
                        Bundle b = data.getExtras();
                        if (b != null) {
                            Reminder reminder = b.getParcelable("reminder");
                            int position = data.getIntExtra("position", 0);
                            reminders.get(position).setPlace(reminder.getPlace());
                            reminders.get(position).setAddress(reminder.getAddress());
                            reminders.get(position).setHour(reminder.getHour());
                            reminders.get(position).setMinute(reminder.getMinute());
                            reminders.get(position).setType(reminder.getType());
                            reminderAdapter.notifyDataSetChanged();
                            databaseHelper.updateReminder(reminder);

                            if(reminders.get(position).isEnabled()){
                                alarm.cancelAlarm(getApplicationContext(), databaseHelper.getId(reminders.get(position).getName()));
                                alarm.setAlarm(getApplicationContext(),
                                        databaseHelper.getId(reminders.get(position).getName()), reminders.get(position));

                                Toast.makeText(MainActivity.this, "Reminder has been updated. Alarm is on.",  Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(MainActivity.this, "Reminder has been updated. Alarm is off.",  Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
            }
        }
    }

    private void dateButtonDescription(){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.buttonDate);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this.getApplicationContext(), DateTimeActivity.class);
                startActivityForResult(intent, TIME_ACTIVITY_RESULT_CODE);
            }
        });
    }

}
