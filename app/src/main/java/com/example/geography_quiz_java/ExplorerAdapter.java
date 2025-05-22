package com.example.geography_quiz_java;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExplorerAdapter extends RecyclerView.Adapter<ExplorerAdapter.ExplorerViewHolder> {

    private final List<ExplorerItem> items;
    private final OnItemClickListener onItemClick;

    public interface OnItemClickListener {
        void onItemClick(ExplorerItem item);
    }

    public ExplorerAdapter(List<ExplorerItem> items, OnItemClickListener onItemClick) {
        this.items = items;
        this.onItemClick = onItemClick;
    }

    public static class ExplorerViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final ImageView icon;

        public ExplorerViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.explorerTitle);
            icon = itemView.findViewById(R.id.explorerIcon);
        }
    }

    @NonNull
    @Override
    public ExplorerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_explorer, parent, false);
        return new ExplorerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExplorerViewHolder holder, int position) {
        ExplorerItem item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.icon.setImageResource(item.getIconRes());

        holder.itemView.setOnClickListener(v -> {
            onItemClick.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}