package ca.dragz.chequemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int STANDARD_REQUEST_CODE = 123;
    Toolbar toolbar;

    private SharedPreferences sharedPreferences;
    private boolean hapticFeedbackEnabled;
    private boolean isMilitaryTime;

    private DatabaseReference mDatabase;
    private RecyclerView rvShifts;
    private Spinner spnJobs;

    private String jobId;
    private String selectedJobId;
    private String jobName;
    private double hourlyWage;
    private double deductionPercentage;
    private double deductionAmount;
    private Integer year;
    private Integer month;
    private Integer dayOfMonth;
    private Integer startHour;
    private Integer startMinute;
    private Integer endHour;
    private Integer endMinute;

    private boolean jobIsSet = true;
    private boolean wageIsSet = true;
    private boolean dateIsSet = false;
    private boolean startTimeIsSet = false;
    private boolean endTimeIsSet = false;

    private Button btnSetDate;
    private Button btnStartTime;
    private Button btnEndTime;
    private Button btnAddShift;

    private EditText etJobName;

    private Job job;
    private Shift shift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("ChequeMate", MODE_PRIVATE);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        EventHandler eventHandler = new EventHandler();

        rvShifts = findViewById(R.id.rvShifts);
        spnJobs = findViewById(R.id.spnJobs);
        spnJobs.setOnItemSelectedListener(eventHandler);

        new FirebaseDatabaseHelper().readJobs(new FirebaseDatabaseHelper.JobDataStatus() {
            @Override
            public void JobDataIsLoaded(List<Job> jobs, List<String> keys) {
                //fill data in spinner
                ArrayAdapter<Job> spinnerAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, jobs);
                spnJobs.setAdapter(spinnerAdapter);
                spnJobs.setSelection(spinnerAdapter.getPosition(job));//Optional to set the selected item.
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

        btnSetDate = findViewById(R.id.btnSetDate);
        btnStartTime = findViewById(R.id.btnStartTime);
        btnEndTime = findViewById(R.id.btnEndTime);
        btnAddShift = findViewById(R.id.btnAddShift);
        btnSetDate.setOnClickListener(eventHandler);
        btnStartTime.setOnClickListener(eventHandler);
        btnEndTime.setOnClickListener(eventHandler);
        btnAddShift.setOnClickListener(eventHandler);

        etJobName = findViewById(R.id.etJobName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RefreshShifts();
        hapticFeedbackEnabled = sharedPreferences.getBoolean("haptic_feedback", true);
        isMilitaryTime = sharedPreferences.getBoolean("military_time", false);
    }

    //to inflate the xml menu file
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar, menu);
        return true;
    }

    //to handle events
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //return super.onOptionsItemSelected(item);
        boolean returnVal = false;

        switch (item.getItemId()) {
            case R.id.action_refresh:
                Log.d("DGM", "refresh menu item");
                returnVal = true;
                break;
            case R.id.action_settings:
                Log.d("DGM", "settings menu item");
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(i, STANDARD_REQUEST_CODE);
                returnVal = true;
                break;
        }
        return returnVal;
    }

    public class EventHandler implements View.OnClickListener, Spinner.OnItemSelectedListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnSetDate:
                    ShowDatePicker();
                    break;
                case R.id.btnStartTime:
                    ShowTimePicker(true);
                    break;
                case R.id.btnEndTime:
                    ShowTimePicker(false);
                    break;
                case R.id.btnAddShift:
                    if (shiftIsValid()) {
                        shift = new Shift(jobName, hourlyWage, year, month, dayOfMonth, startHour, startMinute, endHour, endMinute, deductionPercentage, deductionAmount, "");
                        fireShift();
                        Toast.makeText(MainActivity.this, "Shift added successfully", Toast.LENGTH_SHORT).show();
                        vibrate(hapticFeedbackEnabled, false);
                        clearInputs();
                    } else {
                        Toast.makeText(MainActivity.this, "Missing required fields", Toast.LENGTH_SHORT).show();
                        vibrate(hapticFeedbackEnabled, true);
                    }
                    break;
            }
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Job job = (Job) spnJobs.getItemAtPosition(position);
            selectedJobId = job.getJobId();
            jobName = job.getJobName();
            hourlyWage = job.getHourlyWage();
            deductionPercentage = job.getDeductionPercentage();
            deductionAmount = job.getDeductionAmount();
            RefreshShifts();
//            Toast.makeText(MainActivity.this, "Job Id: " + selectedJobId, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public void RefreshShifts() {
        new FirebaseDatabaseHelper().readShifts(selectedJobId, new FirebaseDatabaseHelper.ShiftDataStatus() {
            @Override
            public void ShiftDataIsLoaded(List<Shift> shifts, List<String> keys) {
                new RecyclerView_Config().setConfig(rvShifts, MainActivity.this, shifts, keys, sharedPreferences);
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

    public void ShowDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        MainActivity.this.year = year;
                        MainActivity.this.month = month + 1;
                        MainActivity.this.dayOfMonth = day;
                        MainActivity.this.dateIsSet = true;
                        btnSetDate.setText((month + 1) + "/" + day + "/" + year);
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    public void ShowTimePicker(final boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        if (isStartTime) {
                            startHour = hourOfDay;
                            startMinute = minute;
                            startTimeIsSet = true;
                            btnStartTime.setText(getTimeString(true, isMilitaryTime));
                        } else {
                            // MAKE SURE END TIME IS GREATER THAN START TIME
                            endHour = hourOfDay;
                            endMinute = minute;
                            endTimeIsSet = true;
                            btnEndTime.setText(getTimeString(false, isMilitaryTime));
                        }
                    }
                }, hour, minute, isMilitaryTime);
        timePickerDialog.show();
    }

    public String getTimeString(boolean isStartTime, boolean isMilitaryTime) {
        int hour = startHour;
        int minute = startMinute;
        String symbol = "AM";

        if (!isStartTime) {
            hour = endHour;
            minute = endMinute;
        }
        if (!isMilitaryTime) {
            if (hour == 0) {
                hour = 12;
            } else if (hour == 12) {
                symbol = "PM";
            } else if (hour > 12) {
                hour = hour - 12;
                symbol = "PM";
            }
            return (minute < 10) ? String.format("%d:0%d %s", hour, minute , symbol) : String.format("%d:%d %s", hour, minute , symbol);
        } else {
            String militaryHour = (hour < 10) ? String.format("0%d", hour) : String.format("%d", hour);
            String militaryMinute = (minute < 10) ? String.format("0%d", minute) : String.format("%d", minute);
            return String.format("%s:%s", militaryHour, militaryMinute);
        }
    }

    public Boolean shiftIsValid() {
        boolean isValid = false;
        if (jobIsSet && wageIsSet && startTimeIsSet && endTimeIsSet) {
            isValid = true;
        }
        return isValid;
    }

    public void fireShift() {
        DatabaseReference shiftEntries = FirebaseDatabase.getInstance().getReference();
        String key = shiftEntries.push().getKey();
        if (key != null) {
            mDatabase.child("jobs").child(selectedJobId).child("shifts").child(key).setValue(shift);
        }
    }

    public void clearInputs() {
        year = null;
        month = null;
        dayOfMonth = null;
        startHour = null;
        startMinute = null;
        endHour = null;
        endMinute = null;
        btnSetDate.setText("Date");
        btnStartTime.setText("Start Time");
        btnEndTime.setText("End Tme");
        dateIsSet = false;
        startTimeIsSet = false;
        endTimeIsSet = false;
        shift = null;
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
}
