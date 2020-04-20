package ca.dragz.chequemate;

import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mReferenceJobs;
    private DatabaseReference mReferenceShiftEntries;
    private List<Job> jobs = new ArrayList<>();
    private List<Shift> shifts = new ArrayList<>();

    public interface JobDataStatus {
        void JobDataIsLoaded(List<Job> jobs, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public interface ShiftDataStatus {
        void ShiftDataIsLoaded(List<Shift> shifts, List<String> keys);
        void DataIsInserted();
        void DataIsUpdated();
        void DataIsDeleted();
    }

    public FirebaseDatabaseHelper() {
        mDatabase = FirebaseDatabase.getInstance();
    }

    public void readJobs(final JobDataStatus jobDataStatus) {
        mReferenceJobs = mDatabase.getReference().child("jobs");
        mReferenceJobs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jobs.clear();
                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    Job job = keyNode.getValue(Job.class);
                    job.setJobId(keyNode.getKey());
                    jobs.add(job);
                }
                jobDataStatus.JobDataIsLoaded(jobs, keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void readShifts(String jobId, final ShiftDataStatus shiftDataStatus) {
        if (jobId != null) {
            mReferenceShiftEntries = mDatabase.getReference().child("jobs").child(jobId).child("shifts");
            mReferenceShiftEntries.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    shifts.clear();
                    List<String> keys = new ArrayList<>();
                    for (DataSnapshot keyNode : dataSnapshot.getChildren()) {
                        keys.add(keyNode.getKey());
                        Shift shift = keyNode.getValue(Shift.class);
                        shifts.add(shift);
                    }
                    shiftDataStatus.ShiftDataIsLoaded(shifts, keys);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
