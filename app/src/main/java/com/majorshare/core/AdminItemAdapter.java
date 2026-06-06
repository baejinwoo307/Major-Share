package com.majorshare.core;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.majorshare.core.controller.ItemRepository;
import com.majorshare.core.domain.Item;

import java.util.List;

public class AdminItemAdapter extends RecyclerView.Adapter<AdminItemAdapter.AdminViewHolder> {

    private List<Item> itemList;

    public AdminItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_list, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        Item item = itemList.get(position);
        
        holder.tvAdminItemTitle.setText("ID: " + item.getItemId() + " - " + item.getTitle());
        holder.tvAdminItemOwner.setText("작성자: " + item.getOwner().getUserId());
        holder.tvAdminItemStatus.setText("상태: " + item.getStatus());

        holder.btnBlind.setOnClickListener(v -> {
            ItemRepository.getInstance().updateItemStatus(v.getContext(), item.getItemId(), "블라인드(관리자)");
            item.changeStatus("블라인드(관리자)");
            notifyDataSetChanged();
            Toast.makeText(v.getContext(), "해당 게시글이 블라인드 처리되었습니다.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvAdminItemTitle, tvAdminItemOwner, tvAdminItemStatus;
        Button btnBlind;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAdminItemTitle = itemView.findViewById(R.id.tvAdminItemTitle);
            tvAdminItemOwner = itemView.findViewById(R.id.tvAdminItemOwner);
            tvAdminItemStatus = itemView.findViewById(R.id.tvAdminItemStatus);
            btnBlind = itemView.findViewById(R.id.btnBlind);
        }
    }
}
