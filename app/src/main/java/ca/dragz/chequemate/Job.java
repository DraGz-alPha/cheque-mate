package ca.dragz.chequemate;

import androidx.annotation.NonNull;

public class Job {
    private String jobId;
    private String jobName;
    private double hourlyWage;
    private double deductionAmount;
    private double deductionPercentage;

    public Job() {
    }

    public Job(String jobName, double hourlyWage, double deductionAmount, double deductionPercentage) {
        this.jobName = jobName;
        this.hourlyWage = hourlyWage;
        this.deductionAmount = deductionAmount;
        this.deductionPercentage = deductionPercentage;
    }

    public Job(String jobId, String jobName, double hourlyWage, double deductionAmount, double deductionPercentage) {
        this.jobId = jobId;
        this.jobName = jobName;
        this.hourlyWage = hourlyWage;
        this.deductionAmount = deductionAmount;
        this.deductionPercentage = deductionPercentage;
    }

    public String getJobName() {
        return jobName;
    }

    public double getHourlyWage() {
        return hourlyWage;
    }

    public String getJobId() { return jobId; }

    public double getDeductionAmount() { return deductionAmount; }

    public void setDeductionAmount(double deductionAmount) { this.deductionAmount = deductionAmount; }

    public double getDeductionPercentage() { return deductionPercentage; }

    public void setDeductionPercentage(double deductionPercentage) { this.deductionPercentage = deductionPercentage; }

    public void setJobId(String jobId) { this.jobId = jobId; }

    @NonNull
    @Override
    public String toString() {
        return jobName;
    }
}
