package com.hlogi.wappclone.starred;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.hlogi.wappclone.chats.data.model.Message;
import com.hlogi.wappclone.databinding.StarredMessageListItemBinding;

public class StarredMessagesAdapter extends PagedListAdapter<Message, StarredMessagesAdapter.ViewHolder> {

    private ItemAction itemAction;

    public StarredMessagesAdapter() {
        super(Message.DIFF_CALLBACK);
    }

    public interface ItemAction {
        void onClick();
    }

    public void setItemAction(ItemAction itemAction) {
        this.itemAction = itemAction;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StarredMessageListItemBinding binding = StarredMessageListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setItemAction(this.itemAction);
        holder.bindTo(getItem(position));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemAction itemAction;

        public ViewHolder(@NonNull StarredMessageListItemBinding binding) {
            super(binding.getRoot());
        }

        void bindTo(Message message){

        }

        public void setItemAction(ItemAction itemAction) {
            this.itemAction = itemAction;
        }
    }

}
