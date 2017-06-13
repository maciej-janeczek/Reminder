package io.lukin.reminder;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class AlarmActivity extends AppCompatActivity {
    Ringtone ringtone;
    int ringing = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        if (savedInstanceState != null) {
            ringing = savedInstanceState.getInt("ringing");
        }
        Bundle b = this.getIntent().getExtras();
        String label = "";
        String address = "";
        if (b != null) {
            label = b.getString("label");
            address = b.getString("address");
        }

        EditText mainLabel = (EditText) findViewById(R.id.alarmNameText);
        mainLabel.setText(label);

        EditText addressLabel = (EditText) findViewById(R.id.alarmLocationText);
        addressLabel.setText(address);


        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);

        ImageButton button = (ImageButton) findViewById(R.id.stopRingtoneButton) ;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ringing != 0) ringtone.stop();
                finish();
            }
        });

        if(ringing == 0) ringtone.play();
        ringing = 1;
    }
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        savedState.putInt("ringing", ringing);
    }
}

