package com.example.doctorhome.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.doctorhome.MedicineDetailActivity;
import com.example.doctorhome.R;
import com.example.doctorhome.model.Medicine;
import java.util.List;

//약 갤러리 어댑터
public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private Context context;
    private List<Medicine> medicineList;

    public MedicineAdapter(Context context, List<Medicine> medicineList) {
        this.context = context;
        this.medicineList = medicineList;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);

        holder.tvName.setText(medicine.getName());
        holder.tvExpiryDate.setText("유통기한: " + medicine.getExpiryDate());
        holder.tvQuantity.setText("남은 개수: " + medicine.getQuantity() + "개");

        //이미지 로드
        String imageUrl = medicine.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery) //로딩 중 표시
                    .error(android.R.drawable.ic_menu_gallery) //실패 시 기본 이미지
                    .diskCacheStrategy(DiskCacheStrategy.ALL) //캐싱
                    .centerCrop()
                    .into(holder.ivMedicine);
        } else { //이미지 URL이 없으면 기본 이미지
            holder.ivMedicine.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        //카드 클릭 시 상세 화면으로 이동
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MedicineDetailActivity.class);
            intent.putExtra("medicine", medicine);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public void updateData(List<Medicine> newList) {
        this.medicineList = newList;
        notifyDataSetChanged();
    }

    static class MedicineViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMedicine;
        TextView tvName, tvExpiryDate, tvQuantity;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMedicine = itemView.findViewById(R.id.ivMedicine);
            tvName = itemView.findViewById(R.id.tvName);
            tvExpiryDate = itemView.findViewById(R.id.tvExpiryDate);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }
}