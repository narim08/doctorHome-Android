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
import com.example.doctorhome.model.MedicineSchedule;
import java.util.List;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    private Context context;
    private List<MedicineSchedule> scheduleList;
    private OnScheduleDeleteListener deleteListener;

    public interface OnScheduleDeleteListener {
        void onDelete(MedicineSchedule schedule);
    }

    public ScheduleAdapter(Context context, List<MedicineSchedule> scheduleList, OnScheduleDeleteListener deleteListener) {
        this.context = context;
        this.scheduleList = scheduleList;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        MedicineSchedule schedule = scheduleList.get(position);

        holder.tvMedicineName.setText(schedule.getMedicineName());
        holder.tvDaysOfWeek.setText(schedule.getDaysOfWeek());
        holder.tvPeriod.setText(schedule.getStartDate() + " ~ " + schedule.getEndDate());

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(schedule);
            }
        });
    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public void updateData(List<MedicineSchedule> newScheduleList) {
        this.scheduleList = newScheduleList;
        notifyDataSetChanged();
    }

    static class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicineName, tvDaysOfWeek, tvPeriod;
        ImageButton btnDelete;

        public ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineName = itemView.findViewById(R.id.tvMedicineName);
            tvDaysOfWeek = itemView.findViewById(R.id.tvDaysOfWeek);
            tvPeriod = itemView.findViewById(R.id.tvPeriod);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}