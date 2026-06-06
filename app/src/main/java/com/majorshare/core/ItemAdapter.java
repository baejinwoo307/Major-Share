package com.majorshare.core;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ItemDetailActivity.class);
            intent.putExtra("itemId", item.getItemId());
            v.getContext().startActivity(intent);
        });
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

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemCategory = itemView.findViewById(R.id.tvItemCategory);
            tvItemType = itemView.findViewById(R.id.tvItemType);
            tvItemPrice = itemView.findViewById(R.id.tvItemPrice);
        }
    }
}