package com.example.doctorhome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doctorhome.R;
import com.example.doctorhome.model.MedicineAlarm;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private Context context;
    private List<MedicineAlarm> alarmList;
    private OnAlarmClickListener listener;

    public interface OnAlarmClickListener {
        void onDeleteClick(MedicineAlarm alarm);
        void onItemClick(MedicineAlarm alarm);
    }

    public AlarmAdapter(Context context, List<MedicineAlarm> alarmList, OnAlarmClickListener listener) {
        this.context = context;
        this.alarmList = alarmList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        MedicineAlarm alarm = alarmList.get(position);

        holder.tvMedicineName.setText(alarm.getMedicineName());
        holder.tvTime.setText(alarm.getTime());
        holder.tvUsage.setText(alarm.getUsage());

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(alarm);
            }
        });

        //아이템 클릭 (수정용)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(alarm);
            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public void updateData(List<MedicineAlarm> newList) {
        this.alarmList = newList;
        notifyDataSetChanged();
    }

    static class AlarmViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicineName, tvTime, tvUsage;
        ImageButton btnDelete;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineName = itemView.findViewById(R.id.tvAlarmMedicineName);
            tvTime = itemView.findViewById(R.id.tvAlarmTime);
            tvUsage = itemView.findViewById(R.id.tvAlarmUsage);
            btnDelete = itemView.findViewById(R.id.btnDeleteAlarm);
        }
    }
}