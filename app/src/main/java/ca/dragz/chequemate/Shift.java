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
    private double deductionPercentage;
    private double deductionAmount;
    private String notes;

    public Shift() {
    }

    public Shift(String jobName, double hourlyWage, int year, int month, int dayOfMonth, int startHour, int startMinute, int endHour, int endMinute, double deductionPercentage, double deductionAmount, String notes) {
        this.jobName = jobName;
        this.hourlyWage = hourlyWage;
        this.year = year;
        this.month = month;
        this.dayOfMonth = dayOfMonth;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.deductionPercentage = deductionPercentage;
        this.deductionAmount = deductionAmount;
        this.notes = notes;
    }

    public String getJobName() {
        return jobName;
    }

    public double getHourlyWage() {
        return hourlyWage;
    }

    public String getNotes() {
        return notes;
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

    public double getDeductionPercentage() { return deductionPercentage; }

    public double getDeductionAmount() { return deductionAmount; }

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

    public double getGrossPay() {
        int startHour = getStartHour();
        int endHour = getEndHour();
        int hours;
        int minutes = Math.abs(getStartMinute() - getEndMinute());
        if (startHour > endHour) {
            hours = 24 - getStartHour() + getEndHour();
        } else {
            hours = Math.abs(startHour - endHour);
        }
        double hourlyWage = getHourlyWage();

        return (hourlyWage * hours) + (hourlyWage * minutes / 60);
    }

    public double getNetPay() {
        return getGrossPay() * (1 - getDeductionPercentage()) - getDeductionAmount();
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
