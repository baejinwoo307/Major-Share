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

        holder.btnBlind.setText("게시글 삭제");
        holder.btnBlind.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(v.getContext())
                .setTitle("게시글 삭제")
                .setMessage("부적절한 게시글로 판단되어 삭제하시겠습니까?\n이 작업은 되돌릴 수 없습니다.")
                .setPositiveButton("삭제", (dialog, which) -> {
                    ItemRepository.getInstance().deleteItem(v.getContext(), item.getItemId());
                    itemList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, itemList.size());
                    Toast.makeText(v.getContext(), "게시글이 영구 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("취소", null)
                .show();
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
