package ca.dragz.chequemate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    private Switch swMilitaryTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("ChequeMate", MODE_PRIVATE);

        EventHandler eventHandler = new EventHandler();

        swMilitaryTime = findViewById(R.id.swMilitaryTime);
        InitializeSettings();
        swMilitaryTime.setOnCheckedChangeListener(eventHandler);
    }

    private void InitializeSettings() {
        boolean isMilitaryTime = sharedPreferences.getBoolean("military_time", false);

        swMilitaryTime.setChecked(isMilitaryTime);
    }

    private void SaveSwitchField(String field, boolean isChecked) {
        String state = isChecked == true ? "enabled" : "disabled";

        editor = sharedPreferences.edit();
        editor.putBoolean(field, isChecked);

        boolean successful = editor.commit();
        if (successful) {
            Toast.makeText(this, "Military time has been " + state, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sorry, there was a problem updating the time format...", Toast.LENGTH_LONG).show();
        }
    }

    class EventHandler implements Switch.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.swMilitaryTime:
                    SaveSwitchField("military_time", isChecked);
                    break;
            }
        }
    }
}
