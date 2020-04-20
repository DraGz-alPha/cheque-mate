package ca.dragz.chequemate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private DatabaseReference mDatabase;

    private Job job;
    private String jobId;

    private Switch swMilitaryTime;

    private Spinner spnSettingsJobs;

    private EditText etJobName;
    private EditText etHourlyWage;

    private Button btnUpdateJob;
    private Button btnClearJobInputs;
    private Button btnAddJob;

    private String selectedJobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("ChequeMate", MODE_PRIVATE);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        EventHandler eventHandler = new EventHandler();

        swMilitaryTime = findViewById(R.id.swMilitaryTime);
        spnSettingsJobs = findViewById(R.id.spnSettingsJobs);
        etJobName = findViewById(R.id.etJobName);
        etHourlyWage = findViewById(R.id.etHourlyWage);
        btnUpdateJob = findViewById(R.id.btnUpdateJob);
        btnClearJobInputs = findViewById(R.id.btnClearJobInputs);
        btnAddJob = findViewById(R.id.btnAddJob);
        InitializeSettings();
        swMilitaryTime.setOnCheckedChangeListener(eventHandler);
        spnSettingsJobs.setOnItemSelectedListener(eventHandler);
        btnUpdateJob.setOnClickListener(eventHandler);
        btnClearJobInputs.setOnClickListener(eventHandler);
        btnAddJob.setOnClickListener(eventHandler);

        new FirebaseDatabaseHelper().readJobs(new FirebaseDatabaseHelper.JobDataStatus() {
            @Override
            public void JobDataIsLoaded(List<Job> jobs, List<String> keys) {
                //fill data in spinner
                ArrayAdapter<Job> spinnerAdapter = new ArrayAdapter<>(SettingsActivity.this, android.R.layout.simple_spinner_dropdown_item, jobs);
                spnSettingsJobs.setAdapter(spinnerAdapter);
//                spnSettingsJobs.setSelection(spinnerAdapter.getPosition(job));//Optional to set the selected item.
            }

            @Override
            public void DataIsInserted() {

            }

            @Override
            public void DataIsUpdated() {

            }

            @Override
            public void DataIsDeleted() {

            }
        });
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

    private void UpdateJobEditTexts(Job job) {
        etJobName.setText(job.getJobName());
        etHourlyWage.setText("" + job.getHourlyWage());
    }

    public void fireJob() {
        DatabaseReference jobEntries = FirebaseDatabase.getInstance().getReference().child("jobs");
        jobId = jobEntries.push().getKey();
        jobEntries.child(jobId).setValue(job);
    }

    public void vibrate(boolean isError) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (!isError) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert vibrator != null;
                vibrator.vibrate(VibrationEffect.createOneShot(10, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vibrator.vibrate(10);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                assert vibrator != null;
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vibrator.vibrate(100);
            }
        }
    }

    class EventHandler implements Switch.OnCheckedChangeListener, Button.OnClickListener, Spinner.OnItemSelectedListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.swMilitaryTime:
                    SaveSwitchField("military_time", isChecked);
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnUpdateJob:
                    if (etJobName.getText().toString().trim().length() > 0 && etHourlyWage.getText().toString().trim().length() > 0) {
                        String jobName = etJobName.getText().toString().trim();
                        double hourlyWage = Double.parseDouble(etHourlyWage.getText().toString().trim());
                        mDatabase.child("jobs").child(selectedJobId).child("jobName").setValue(jobName);
                        mDatabase.child("jobs").child(selectedJobId).child("hourlyWage").setValue(hourlyWage);
                        Toast.makeText(SettingsActivity.this, "Job '" + jobName + "' has been updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Missing required fields", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btnClearJobInputs:
                    etJobName.setText("");
                    etHourlyWage.setText("");
                    break;
                case R.id.btnAddJob:
                    if (etJobName.getText().toString().trim().length() > 0 && etHourlyWage.getText().toString().trim().length() > 0) {
                        String jobName = etJobName.getText().toString();
                        double hourlyWage = Double.parseDouble(etHourlyWage.getText().toString());
                        job = new Job(jobName, hourlyWage);
                        fireJob();
                        etJobName.setText("");
                        etHourlyWage.setText("");
                        Toast.makeText(SettingsActivity.this, "'" + jobName + "' has been created", Toast.LENGTH_SHORT).show();
                        vibrate(false);
                    } else {
                        Toast.makeText(SettingsActivity.this, "Missing required fields", Toast.LENGTH_SHORT).show();
                        vibrate(true);
                    }
                    break;
            }
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Job job = (Job) spnSettingsJobs.getItemAtPosition(position);
            selectedJobId = job.getJobId();
            UpdateJobEditTexts(job);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
