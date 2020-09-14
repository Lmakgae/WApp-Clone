package com.hlogi.wappclone.chats.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.hlogi.wappclone.contacts.data.model.Contact;
import com.hlogi.wappclone.databinding.OtherPhonesListItemBinding;

import static com.hlogi.wappclone.contacts.data.model.Contact.DIFF_CALLBACK;

public class OtherNumbersAdapter extends ListAdapter<Contact, OtherNumbersAdapter.ViewHolder> {

    private ItemAction mItemAction;

    public OtherNumbersAdapter() {
        super(DIFF_CALLBACK);
    }

    public interface ItemAction {
        void onClickMessage(String number);
        void onClickVoiceCall(String number);
        void onClickVideoCall(String number);
    }

    public void setOnItemAction( ItemAction itemAction) { this.mItemAction = itemAction; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        OtherPhonesListItemBinding binding = OtherPhonesListItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindTo(getItem(position));
        holder.setOnItemClickListener(mItemAction);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private Contact contact;
        private OtherPhonesListItemBinding binding;
        private ItemAction itemAction;

        ViewHolder(@NonNull OtherPhonesListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(Contact contact) {
            this.contact = contact;
            binding.setNumber(this.contact.getNumber());
            binding.setNumberType(this.contact.getNumber_type());
            binding.phoneNumberTextview.setOnClickListener(v -> {
                itemAction.onClickMessage(this.contact.getNumber());
            });
            binding.phoneNumberTypeTextview.setOnClickListener(v -> {
                itemAction.onClickMessage(this.contact.getNumber());
            });
            binding.phoneNoMsgImgView.setOnClickListener(v -> {
                itemAction.onClickMessage(this.contact.getNumber());
            });
            binding.phoneNoCallImgView.setOnClickListener(v -> {
                itemAction.onClickVoiceCall(this.contact.getNumber());
            });
            binding.phoneNoVideoCallImgView.setOnClickListener(v -> {
                itemAction.onClickVideoCall(this.contact.getNumber());
            });
        }

        void setOnItemClickListener(ItemAction itemAction) {
            this.itemAction = itemAction;
        }
    }
}
