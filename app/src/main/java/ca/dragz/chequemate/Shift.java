package ca.dragz.chequemate;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Shift {
    private String jobName;
    private double hourlyWage;
    private int year;
    private int month;
    private int dayOfMonth;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;

    public Shift() {
    }

    public Shift(String jobName, double hourlyWage, int year, int month, int dayOfMonth, int startHour, int startMinute, int endHour, int endMinute) {
        this.jobName = jobName;
        this.hourlyWage = hourlyWage;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    public String getJobName() {
        return jobName;
    }

    public double getHourlyWage() {
        return hourlyWage;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public int getStartHour() {
        return startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public String getTimeString(boolean isStartTime, boolean isAmPm) {
        int hour = startHour;
        int minute = startMinute;
        String symbol = "AM";
        String returnVal = "";

        if (!isStartTime) {
            hour = endHour;
            minute = endMinute;
        }
        if (isAmPm) {
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
            return String.format("%d:%d", hour, minute);
        }
    }

//    public String getShiftGrossPay() {
//        SimpleDateFormat format = new SimpleDateFormat("hhmm");
//        Date startDate = format.parse("0900");
//        Date endDate = format.parse("1730");
//        DateTime jdStartDate = new DateTime(startDate);
//        DateTime jdEndDate = new DateTime(endDate);
//        int hours = Hours.hoursBetween(jdStartDate, jdEndDate).getHours();
//        int minutes = Minutes.minutesBetween(jdStartDate, jdEndDate).getMinutes();
//        minutes = minutes % 60;
//
//        System.out.println(hours + " hours " + minutes + " minutes");
//
//        return
//    }

    public String getDateString() {
        return month + "/" + dayOfMonth + "/" + year;
    }

//    @NonNull
//    @Override
//    public String toString() {
//        String string = "Job: " + this.jobName + "\nHourly Wage: " + this.hourlyWage + "\nDate: " + getDateString() + "\nStart Time: " + getTimeString(true) + "\nEnd Time: " + getTimeString(false);
//        return string;
//    }
}
