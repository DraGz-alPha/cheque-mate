package ca.dragz.chequemate;

import androidx.annotation.NonNull;

public class Shift {
    private String jobName;
    private double hourlyWage;
    private Integer year;
    private Integer month;
    private Integer dayOfMonth;
    private Integer startHour;
    private Integer startMinute;
    private Integer endHour;
    private Integer endMinute;

    public Shift(String jobName, double hourlyWage, Integer year, Integer month, Integer dayOfMonth,
                 Integer startHour, Integer startMinute, Integer endHour, Integer endMinute) {
        this.jobName = jobName;
        this.hourlyWage = hourlyWage;
        this.year = year;
        this.month = month + 1;
        this.dayOfMonth = dayOfMonth;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    private String getTimeString(boolean isStartTime) {
        if (isStartTime) {
            return startHour + ":" + startMinute;
        } else {
            return endHour + ":" + endMinute;
        }
    }

    private String getDateString() {
        return month + "/" + dayOfMonth + "/" + year;
    }

    @NonNull
    @Override
    public String toString() {
        String string = "Job: " + this.jobName + "\nHourly Wage: " + this.hourlyWage + "\nDate: " + getDateString() + "\nStart Time: " + getTimeString(true) + "\nEnd Time: " + getTimeString(false);
        return string;
    }
}
