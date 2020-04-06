package ca.dragz.chequemate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Duration;
import java.time.LocalTime;
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
        private TextView txtDate;
        private TextView txtGrossPay;
        private TextView txtNetPay;

        private String key;

        public ShiftItemView(ViewGroup parent) {
            super(LayoutInflater.from(mContext).
            inflate(R.layout.shift_list_item, parent,false));

            txtJobName = itemView.findViewById(R.id.txtJobName);
            txtHourlyWage = itemView.findViewById(R.id.txtHourlyWage);
            txtStartTime = itemView.findViewById(R.id.txtStartTime);
            txtEndTime = itemView.findViewById(R.id.txtEndTime);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtGrossPay = itemView.findViewById(R.id.txtGrossPay);
            txtNetPay = itemView.findViewById(R.id.txtNetPay);
        }
        public void bind(Shift shift, String key) {
            txtJobName.setText(shift.getJobName());
            txtHourlyWage.setText("$" + Double.toString(shift.getHourlyWage()) + "0");
            txtStartTime.setText(shift.getTimeString(true, true));
            txtEndTime.setText(shift.getTimeString(false, true));
            txtDate.setText(shift.getDateString());
            txtGrossPay.setText(String.format("$%.2f", shift.getGrossPay()));
            txtNetPay.setText(String.format("$%.2f", shift.getNetPay()));

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
