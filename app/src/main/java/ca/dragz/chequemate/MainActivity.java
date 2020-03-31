package ca.dragz.chequemate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;

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
    private TextView tvShiftDetails;

    private DatabaseReference mDatabase;

    private Shift shift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        EventHandler eventHandler = new EventHandler();

        tvDate = findViewById(R.id.tvDate);
        tvStartTime = findViewById(R.id.tvStartTime);
        tvEndTime = findViewById(R.id.tvEndTime);
        tvShiftDetails = findViewById(R.id.tvShiftDetails);

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
                        tvShiftDetails.setText(shift.toString());
                        Toast.makeText(MainActivity.this, "Shift added successfully!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Missing required fields!", Toast.LENGTH_LONG).show();
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
                        MainActivity.this.month = month;
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
}
