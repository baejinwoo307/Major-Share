package com.majorshare.core;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.majorshare.core.domain.Item;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private List<Item> itemList;

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    // ★ 검색 필터링을 위해 리스트 데이터를 교체하고 화면을 새로고침하는 메서드 추가
    public void updateList(List<Item> newList) {
        this.itemList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.tvItemTitle.setText(item.getTitle());
        holder.tvItemCategory.setText(item.getCategory());
        holder.tvItemType.setText(item.getTransactionType());
        holder.tvItemPrice.setText(item.getPrice() + "원");

        if (item.getImageBase64() != null && !item.getImageBase64().trim().isEmpty()) {
            try {
                byte[] decodedBytes = Base64.decode(item.getImageBase64(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                if (bitmap != null) {
                    holder.ivItemThumbnail.setImageBitmap(bitmap);
                    holder.ivItemThumbnail.setVisibility(View.VISIBLE);
                } else {
                    holder.ivItemThumbnail.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                holder.ivItemThumbnail.setVisibility(View.GONE);
            }
        } else {
            holder.ivItemThumbnail.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ItemDetailActivity.class);
            intent.putExtra("itemId", item.getItemId());
            v.getContext().startActivity(intent);
        });

        // 소유자 본인인 경우 예약자 명단 버튼 표시
        com.majorshare.core.domain.User currentUser = com.majorshare.core.controller.AuthManager.getInstance().getCurrentUser();
        if (currentUser != null && item.getOwner() != null && currentUser.getUserId().trim().equalsIgnoreCase(item.getOwner().getUserId().trim())) {
            holder.btnViewItemReservations.setVisibility(View.VISIBLE);
            holder.btnViewItemReservations.setOnClickListener(v -> {
                com.majorshare.core.db.DatabaseHelper db = new com.majorshare.core.db.DatabaseHelper(v.getContext());
                java.util.List<com.majorshare.core.domain.Reservation> allRes = db.getAllReservations();
                java.util.List<String> displayList = new java.util.ArrayList<>();
                for (com.majorshare.core.domain.Reservation r : allRes) {
                    if (r.getTargetItem().getItemId().equals(item.getItemId()) && "대기".equals(r.getStatus())) {
                        displayList.add("대기자: " + r.getBorrower().getName() + " (" + r.getBorrower().getUserId() + ")");
                    }
                }
                
                String message = displayList.isEmpty() ? "현재 대기 중인 예약자가 없습니다." : String.join("\n", displayList);
                new android.app.AlertDialog.Builder(v.getContext())
                    .setTitle("예약자 명단")
                    .setMessage(message)
                    .setPositiveButton("확인", null)
                    .show();
            });
        } else {
            holder.btnViewItemReservations.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemTitle;
        TextView tvItemCategory;
        TextView tvItemType;
        TextView tvItemPrice;
        ImageView ivItemThumbnail;
        android.widget.Button btnViewItemReservations;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemCategory = itemView.findViewById(R.id.tvItemCategory);
            tvItemType = itemView.findViewById(R.id.tvItemType);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
            ivItemThumbnail = itemView.findViewById(R.id.ivItemThumbnail);
            btnViewItemReservations = itemView.findViewById(R.id.btnViewItemReservations);
        }
    }
}