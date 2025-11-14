package com.example.doctorhome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doctorhome.R;
import com.example.doctorhome.model.Hospital;
import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder> {

    private Context context;
    private List<Hospital> hospitalList;
    private OnHospitalClickListener listener;

    public interface OnHospitalClickListener {
        void onHospitalClick(Hospital hospital);
    }

    public HospitalAdapter(Context context, List<Hospital> hospitalList, OnHospitalClickListener listener) {
        this.context = context;
        this.hospitalList = hospitalList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HospitalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hospital, parent, false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HospitalViewHolder holder, int position) {
        Hospital hospital = hospitalList.get(position);

        if (hospital.getType().equals("약국")) {
            holder.tvType.setText("💊");
        } else {
            holder.tvType.setText("🏥");
        }

        holder.tvName.setText(hospital.getName());
        holder.tvAddress.setText(hospital.getAddress());

        String phone = hospital.getPhone();
        if (phone != null && !phone.isEmpty()) {
            holder.tvPhone.setText("📞 " + phone);
            holder.tvPhone.setVisibility(View.VISIBLE);
        } else {
            holder.tvPhone.setVisibility(View.GONE);
        }

        //아이템 클릭 시 지도 중심 이동
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHospitalClick(hospital);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hospitalList.size();
    }

    public void updateData(List<Hospital> newList) {
        this.hospitalList = newList;
        notifyDataSetChanged();
    }

    static class HospitalViewHolder extends RecyclerView.ViewHolder {
        TextView tvType, tvName, tvAddress, tvPhone;

        public HospitalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvType = itemView.findViewById(R.id.tvHospitalType);
            tvName = itemView.findViewById(R.id.tvHospitalName);
            tvAddress = itemView.findViewById(R.id.tvHospitalAddress);
            tvPhone = itemView.findViewById(R.id.tvHospitalPhone);
        }
    }
}
