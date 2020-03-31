package ca.dragz.chequemate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerView_Config {
    private Context mContext;
    private ShiftsAdapter mShiftsAdapter;
    public void setConfig(RecyclerView rvShifts, Context context, List<Shift> shifts, List<String> keys) {
        mContext = context;
        mShiftsAdapter = new ShiftsAdapter(shifts, keys);
        rvShifts.setLayoutManager(new LinearLayoutManager(context));
        rvShifts.setAdapter(mShiftsAdapter);
    }

    class ShiftItemView extends RecyclerView.ViewHolder {
        private TextView txtJobName;
        private TextView txtHourlyWage;
        private TextView txtStartTime;
        private TextView txtEndTime;

        private String key;

        public ShiftItemView(ViewGroup parent) {
            super(LayoutInflater.from(mContext).
            inflate(R.layout.shift_list_item, parent,false));

            txtJobName = itemView.findViewById(R.id.txtJobName);
            txtHourlyWage = itemView.findViewById(R.id.txtHourlyWage);
            txtStartTime = itemView.findViewById(R.id.txtStartTime);
            txtEndTime = itemView.findViewById(R.id.txtEndTime);
        }
        public void bind(Shift shift, String key) {
            txtJobName.setText(shift.getJobName());
            txtHourlyWage.setText("Hourly: $" + Double.toString(shift.getHourlyWage()));
            txtStartTime.setText("Start Time: " + shift.getTimeString(true));
            txtEndTime.setText("End Time: " + shift.getTimeString(false));
            this.key = key;
        }
    }
    class ShiftsAdapter extends RecyclerView.Adapter<ShiftItemView> {
        private List<Shift> mShiftList;
        private List<String> mKeys;

        public ShiftsAdapter(List<Shift> mShiftList, List<String> mKeys) {
            this.mShiftList = mShiftList;
            this.mKeys = mKeys;
        }

        @NonNull
        @Override
        public ShiftItemView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ShiftItemView(parent);
        }

        @Override
        public void onBindViewHolder(@NonNull ShiftItemView holder, int position) {
            holder.bind(mShiftList.get(position), mKeys.get(position));
        }

        @Override
        public int getItemCount() {
            return mShiftList.size();
        }
    }
}
