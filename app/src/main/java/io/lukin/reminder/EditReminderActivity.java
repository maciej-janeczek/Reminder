package io.lukin.reminder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditReminderActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int TIME_ACTIVITY_RESULT_CODE = 2;

    Reminder reminder;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_reminder);

        if (savedInstanceState != null) {
            reminder = savedInstanceState.getParcelable("newReminder");
        }else{
            Bundle b = this.getIntent().getExtras();
            if (b != null) {
                reminder = b.getParcelable("reminder");
            }
        }

        position = this.getIntent().getIntExtra("position", -1);

        loadText();

        locationButtonDescription();
        dateButtonDescription();


    }

    private void loadText(){
        AutoCompleteTextView label = (AutoCompleteTextView) findViewById(R.id.editNameTextView);
        label.setText(reminder.getName());

        TextView time = (TextView) findViewById(R.id.editTimeEditText);

        String dateStr = reminder.getHour() +":"+ reminder.getMinute();
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

        TextView type = (TextView) findViewById(R.id.editTypeEditText);
        type.setText(reminder.getType());

        TextView location = (TextView) findViewById(R.id.editLocationTextView);
        location.setText(reminder.getAddress());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_accept:
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                intent.putExtra("delete", false);
                intent.putExtra("position", position);
                Bundle bundle = new Bundle();
                bundle.putParcelable("reminder", reminder);
                intent.putExtras(bundle);
                finish();
                return true;
            case R.id.action_delete:
                intent = new Intent();
                setResult(RESULT_OK, intent);
                intent.putExtra("delete", true);
                intent.putExtra("position", position);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putParcelable("newReminder", reminder);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(this, data);
                    reminder.setPlace(place.getLatLng());
                    reminder.setAddress(place.getAddress().toString());
                    loadText();
                    return;

                case TIME_ACTIVITY_RESULT_CODE:
                    reminder.setType(data.getStringExtra("type"));
                    reminder.setMinute(data.getIntExtra("minute", -1));
                    reminder.setHour(data.getIntExtra("hour", -1));
                    loadText();
                    return;

            }
        }
    }

    private void locationButtonDescription(){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.editLocationButton);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = builder.build(EditReminderActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void dateButtonDescription(){
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.editTimeButton);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditReminderActivity.this.getApplicationContext(), DateTimeActivity.class);
                startActivityForResult(intent, TIME_ACTIVITY_RESULT_CODE);
            }
        });
    }


}
