package ca.dragz.chequemate;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private final int STANDARD_REQUEST_CODE = 123;
    Toolbar toolbar;

    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private DatabaseReference mDatabase;

    private Job job;
    private String jobId;

    private Switch swMilitaryTime;
    private Switch swHapticFeedback;
    private boolean hapticFeedbackEnabled;

    private Spinner spnSettingsJobs;

    private EditText etJobName;
    private EditText etHourlyWage;
    private EditText etDeductionPercentage;
    private EditText etDeductionAmount;

    private Button btnAddJob;
    private Button btnUpdateJob;
    private Button btnDeleteJob;
    private Button btnClearJobInputs;

    private String selectedJobId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.toolbarSettings);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedPreferences = getSharedPreferences("ChequeMate", MODE_PRIVATE);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        EventHandler eventHandler = new EventHandler();

        swMilitaryTime = findViewById(R.id.swMilitaryTime);
        swHapticFeedback = findViewById(R.id.swHapticFeedback);
        hapticFeedbackEnabled = sharedPreferences.getBoolean("haptic_feedback", true);
        spnSettingsJobs = findViewById(R.id.spnSettingsJobs);

        etJobName = findViewById(R.id.etJobName);
        etHourlyWage = findViewById(R.id.etHourlyWage);
        etDeductionPercentage = findViewById(R.id.etDeductionPercentage);
        etDeductionAmount = findViewById(R.id.etDeductionAmount);

        btnAddJob = findViewById(R.id.btnAddJob);
        btnUpdateJob = findViewById(R.id.btnUpdateJob);
        btnDeleteJob = findViewById(R.id.btnDeleteJob);
        btnClearJobInputs = findViewById(R.id.btnClearJobInputs);

        InitializeSettings();

        swMilitaryTime.setOnCheckedChangeListener(eventHandler);
        swHapticFeedback.setOnCheckedChangeListener(eventHandler);

        spnSettingsJobs.setOnItemSelectedListener(eventHandler);

        btnAddJob.setOnClickListener(eventHandler);
        btnUpdateJob.setOnClickListener(eventHandler);
        btnClearJobInputs.setOnClickListener(eventHandler);
        btnDeleteJob.setOnClickListener(eventHandler);

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);
        boolean returnVal = false;

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void InitializeSettings() {
        boolean isMilitaryTime = sharedPreferences.getBoolean("military_time", false);
        boolean isHapticFeedback = sharedPreferences.getBoolean("haptic_feedback", true);
        swMilitaryTime.setChecked(isMilitaryTime);
        swHapticFeedback.setChecked(isHapticFeedback);
    }

    private void SaveSwitchField(String displayName, String field, boolean isChecked) {
        String state = isChecked == true ? "enabled" : "disabled";

        editor = sharedPreferences.edit();
        editor.putBoolean(field, isChecked);

        boolean successful = editor.commit();
        if (successful) {
            Toast.makeText(this, displayName + " has been " + state, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sorry, there was a problem updating " + displayName + "...", Toast.LENGTH_LONG).show();
        }
    }

    private void UpdateJobEditTexts(Job job) {
        etJobName.setText(job.getJobName());
        etHourlyWage.setText("" + job.getHourlyWage());
        etDeductionPercentage.setText("" + job.getDeductionPercentage() * 100);
        etDeductionAmount.setText("" + job.getDeductionAmount());
    }

    private void fireJob() {
        DatabaseReference jobEntries = FirebaseDatabase.getInstance().getReference().child("jobs");
        jobId = jobEntries.push().getKey();
        jobEntries.child(jobId).setValue(job);
    }

    private boolean jobIsValid() {
        if (etJobName.getText().toString().trim().length() > 0 &&
            etHourlyWage.getText().toString().trim().length() > 0 &&
            etDeductionPercentage.getText().toString().trim().length() > 0 &&
            etDeductionAmount.getText().toString().trim().length() > 0) {

            return true;
        } else {
            return false;
        }
    }

    public void vibrate(boolean isEnabled, boolean isError) {
        if (isEnabled) {
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
    }

    private void ClearJobInputs() {
        etJobName.setText("");
        etHourlyWage.setText("");
        etDeductionPercentage.setText("");
        etDeductionAmount.setText("");
    }

    class EventHandler implements Switch.OnCheckedChangeListener, Button.OnClickListener, Spinner.OnItemSelectedListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.swMilitaryTime:
                    SaveSwitchField("Military time", "military_time", isChecked);
                    break;
                case R.id.swHapticFeedback:
                    SaveSwitchField("Haptic feedback", "haptic_feedback", isChecked);
                    hapticFeedbackEnabled = isChecked;
                    break;
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnAddJob:
                    if (jobIsValid()) {
                        String jobName = etJobName.getText().toString().trim();
                        double hourlyWage = Double.parseDouble(etHourlyWage.getText().toString().trim());
                        double deductionPercentage = Double.parseDouble(etDeductionPercentage.getText().toString().trim());
                        double deductionAmount = Double.parseDouble(etDeductionAmount.getText().toString().trim());
                        job = new Job(jobName, hourlyWage, deductionAmount, deductionPercentage);
                        fireJob();
                        ClearJobInputs();
                        Toast.makeText(SettingsActivity.this, "'" + jobName + "' has been created", Toast.LENGTH_SHORT).show();
                        vibrate(hapticFeedbackEnabled, false);
                    } else {
                        Toast.makeText(SettingsActivity.this, "Missing required fields", Toast.LENGTH_SHORT).show();
                        vibrate(hapticFeedbackEnabled, true);
                    }
                    break;
                case R.id.btnUpdateJob:
                    if (jobIsValid()) {
                        String jobName = etJobName.getText().toString().trim();
                        double hourlyWage = Double.parseDouble(etHourlyWage.getText().toString().trim());
                        double deductionPercentage = Double.parseDouble(etDeductionPercentage.getText().toString().trim());
                        double deductionAmount = Double.parseDouble(etDeductionAmount.getText().toString().trim());
                        Map<String, Object> updatedJob = new HashMap<>();
                        updatedJob.put("jobName", jobName);
                        updatedJob.put("hourlyWage", hourlyWage);
                        updatedJob.put("deductionPercentage", deductionPercentage);
                        updatedJob.put("deductionAmount", deductionAmount);
                        mDatabase.child("jobs").child(selectedJobId).updateChildren(updatedJob);
                        Toast.makeText(SettingsActivity.this, "Job '" + jobName + "' has been updated", Toast.LENGTH_SHORT).show();
                        vibrate(hapticFeedbackEnabled, false);
                    } else {
                        Toast.makeText(SettingsActivity.this, "Missing required fields", Toast.LENGTH_SHORT).show();
                        vibrate(hapticFeedbackEnabled, true);
                    }
                    break;
                case R.id.btnDeleteJob:
                    if (selectedJobId != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                        builder.setMessage("Are you sure you want to delete this job? All associated shifts will be gone forever!")
                            .setPositiveButton("Yes, delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mDatabase.child("jobs").child(selectedJobId).removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                        if (databaseError == null) {
                                            ClearJobInputs();
                                            Toast.makeText(SettingsActivity.this, "Job has been deleted", Toast.LENGTH_SHORT).show();
                                            vibrate(hapticFeedbackEnabled, false);
                                        } else {
                                            Toast.makeText(SettingsActivity.this, "Unable to delete job", Toast.LENGTH_SHORT).show();
                                            vibrate(hapticFeedbackEnabled, true);
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    } else {
                        Toast.makeText(SettingsActivity.this, "No job is selected", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btnClearJobInputs:
                    ClearJobInputs();
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
