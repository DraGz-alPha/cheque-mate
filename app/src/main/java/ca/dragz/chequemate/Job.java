package ca.dragz.chequemate;

import androidx.annotation.NonNull;

public class Job {
    private String jobId;
    private String jobName;
    private double hourlyWage;

    public Job() {
    }

    public Job(String jobName, double hourlyWage) {
        this.jobName = jobName;
        this.hourlyWage = hourlyWage;
    }

    public String getJobName() {
        return jobName;
    }

    public double getHourlyWage() {
        return hourlyWage;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    @NonNull
    @Override
    public String toString() {
        return jobName;
    }
}
