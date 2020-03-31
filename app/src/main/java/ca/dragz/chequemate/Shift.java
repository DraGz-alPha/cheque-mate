package ca.dragz.chequemate;

import androidx.annotation.NonNull;

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

    public String getTimeString(boolean isStartTime) {
        if (isStartTime) {
            return startHour + ":" + startMinute;
        } else {
            return endHour + ":" + endMinute;
        }
    }

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
