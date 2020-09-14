package com.hlogi.wappclone.settings.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hlogi.wappclone.databinding.AboutListItemBinding;

import java.util.ArrayList;

public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.ViewHolder> {

    private ArrayList<String> list = new ArrayList<>();
    private String selected_about;
    private AboutAdapter.ItemActionListener mItemActionListener;

    public AboutAdapter(String selected_about) {
        this.selected_about = selected_about;
        list.add("Available");
        list.add("Busy");
        list.add("At school");
        list.add("At the movies");
        list.add("At work");
        list.add("Battery about to die");
        list.add("Can't talk, WApp only");
        list.add("In a meeting");
        list.add("At the gym");
        list.add("Sleeping");
        list.add("Urgent calls only");
    }

    public void setSelected(String about) {
        this.selected_about = about;
    }

    public interface ItemActionListener {
        void onClickView(String title);
    }

    public void setItemActionListener(AboutAdapter.ItemActionListener mItemActionListener) {
        this.mItemActionListener = mItemActionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AboutListItemBinding binding = AboutListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AboutAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setItemActionListener(mItemActionListener);
        holder.bindTo(list.get(position), selected_about);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private AboutListItemBinding binding;
        private AboutAdapter.ItemActionListener itemActionListener;

        public ViewHolder(@NonNull AboutListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(String about, String selected_about) {
            binding.text.setText(about);
            if (about.equals(selected_about)) {
                binding.check.setVisibility(View.VISIBLE);
            } else {
                binding.check.setVisibility(View.INVISIBLE);
            }
            binding.getRoot().setOnClickListener(v -> {
                itemActionListener.onClickView(about);
            });
        }

        public void setItemActionListener(AboutAdapter.ItemActionListener listener) {
            this.itemActionListener = listener;
        }
    }

}
