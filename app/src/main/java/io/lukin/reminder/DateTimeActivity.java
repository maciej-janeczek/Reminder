package io.lukin.reminder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class DateTimeActivity extends AppCompatActivity {

    int hour = -1;
    int minute = -1;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time);

        String[] arraySpinner = new String[] {
                "Everyday", "Monday - Friday", "Weekends"};

        Spinner s = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        s.setAdapter(adapter);

        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        ImageButton doneButton = (ImageButton) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
                Spinner s = (Spinner) findViewById(R.id.spinner);
                // put the String to pass back into an Intent and close this activity
                Intent intent = new Intent();
                intent.putExtra("type", s.getSelectedItem().toString());
                intent.putExtra("minute", timePicker.getMinute());
                intent.putExtra("hour", timePicker.getHour());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
