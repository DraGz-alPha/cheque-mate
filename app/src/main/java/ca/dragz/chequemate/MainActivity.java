package ca.dragz.chequemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;

    private DatabaseReference mDatabase;
    private RecyclerView rvShifts;

    private String jobName = "Nyhof Farms";
    private double hourlyWage = 18.50;
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

    private TextView tvDate;
    private TextView tvStartTime;
    private TextView tvEndTime;

    private Shift shift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        EventHandler eventHandler = new EventHandler();

        rvShifts = findViewById(R.id.rvShifts);
        new FirebaseDatabaseHelper().readShifts(new FirebaseDatabaseHelper.DataStatus() {
            @Override
            public void DataIsLoaded(List<Shift> shifts, List<String> keys) {
                new RecyclerView_Config().setConfig(rvShifts, MainActivity.this, shifts, keys);
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

        tvDate = findViewById(R.id.tvDate);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);

        btnSetDate = findViewById(R.id.btnSetDate);
        btnStartTime = findViewById(R.id.btnStartTime);
        btnEndTime = findViewById(R.id.btnEndTime);
        btnAddShift = findViewById(R.id.btnAddShift);
        btnSetDate.setOnClickListener(eventHandler);
        btnStartTime.setOnClickListener(eventHandler);
        btnEndTime.setOnClickListener(eventHandler);
        btnAddShift.setOnClickListener(eventHandler);
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
//                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
//                startActivityForResult(i, STANDARD_REQUEST_CODE);
                returnVal = true;
                break;
        }
        return returnVal;
    }

    public class EventHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnSetDate:
//                    mDatabase.child("shift_entries").child("shift").setValue("Hello, Universe!");
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
                        shift = new Shift(jobName, hourlyWage, year, month, dayOfMonth, startHour, startMinute, endHour, endMinute);
                        fireShift();
                        Toast.makeText(MainActivity.this, "Shift added successfully!", Toast.LENGTH_SHORT).show();
                        vibrate(false);
                        clearInputs();
                    } else {
                        Toast.makeText(MainActivity.this, "Missing required fields!", Toast.LENGTH_SHORT).show();
                        vibrate(true);
                    }
                    break;
            }
        }
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
                        tvDate.setText((month + 1) + "/" + day + "/" + year);
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
                            tvStartTime.setText(hourOfDay + ":" + minute);
                        } else {
                            // MAKE SURE END TIME IS GREATER THAN START TIME
                            endHour = hourOfDay;
                            endMinute = minute;
                            endTimeIsSet = true;
                            tvEndTime.setText(hourOfDay + ":" + minute);
                        }
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    public Boolean shiftIsValid() {
        boolean isValid = false;
        if (jobIsSet && wageIsSet && startTimeIsSet && endTimeIsSet) {
            isValid = true;
        }
        return isValid;
    }

    public void fireShift() {
        DatabaseReference shiftEntries = FirebaseDatabase.getInstance().getReference("shifts");
        String key = shiftEntries.push().getKey();
        if (key != null) {
            mDatabase.child("shifts").child(key).child("jobName").setValue(jobName);
            mDatabase.child("shifts").child(key).child("hourlyWage").setValue(hourlyWage);
            mDatabase.child("shifts").child(key).child("year").setValue(year);
            mDatabase.child("shifts").child(key).child("month").setValue(month);
            mDatabase.child("shifts").child(key).child("dayOfMonth").setValue(dayOfMonth);
            mDatabase.child("shifts").child(key).child("startHour").setValue(startHour);
            mDatabase.child("shifts").child(key).child("startMinute").setValue(startMinute);
            mDatabase.child("shifts").child(key).child("endHour").setValue(endHour);
            mDatabase.child("shifts").child(key).child("endMinute").setValue(endMinute);
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
        tvDate.setText("--");
        tvStartTime.setText("--");
        tvEndTime.setText("--");
        dateIsSet = false;
        startTimeIsSet = false;
        endTimeIsSet = false;
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
}
