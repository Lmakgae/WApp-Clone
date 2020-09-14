package com.hlogi.wappclone.contacts.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.contacts.data.model.Contact;
import com.hlogi.wappclone.databinding.ContactsListItemBinding;

import static com.hlogi.wappclone.contacts.data.model.Contact.DIFF_CALLBACK;

public class ContactsAdapter extends PagedListAdapter<Contact, ContactsAdapter.ViewHolder> {

    private ContactsAdapter.ItemActionListener mItemActionListener;
    private final String side;

    public ContactsAdapter(String side) {
        super(DIFF_CALLBACK);
        this.side = side;
    }

    public interface ItemActionListener {
        void onClickContact(String number);
        void onClickCall(String number);
        void onClickVideoCall(String number);
        void onClickImage(Contact contact);
    }

    public void setItemActionListener(ContactsAdapter.ItemActionListener mItemActionListener) {
        this.mItemActionListener = mItemActionListener;
    }

    @NonNull
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContactsListItemBinding binding = ContactsListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false );
        return new ContactsAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapter.ViewHolder holder, int position) {
        holder.bindTo(getItem(position), side);
        holder.setItemActionListener(mItemActionListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ContactsListItemBinding binding;
        private Contact contact;
        private ContactsAdapter.ItemActionListener itemActionListener;

        public ViewHolder(@NonNull ContactsListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(Contact contact, String side) {
            this.contact = contact;
            binding.setSide(side);
            binding.setContact(contact);

            binding.getRoot().setOnClickListener(v -> {
                if (side.equals(binding.getRoot().getResources().getString(R.string.chats_t))) {
                    itemActionListener.onClickContact(this.contact.getNumber());
                }
            });
            binding.contactImageIv.setOnClickListener(v -> {
                itemActionListener.onClickImage(this.contact);
            });
            binding.contactName.setOnClickListener(v -> {
                if (side.equals(binding.getRoot().getResources().getString(R.string.chats_t))) {
                    itemActionListener.onClickContact(this.contact.getNumber());
                }
            });
            binding.contactStatus.setOnClickListener(v -> {
                if (side.equals(binding.getRoot().getResources().getString(R.string.chats_t))) {
                    itemActionListener.onClickContact(this.contact.getNumber());
                }
            });
            binding.contactCallIc.setOnClickListener(v -> {
                itemActionListener.onClickCall(this.contact.getNumber());
            });
            binding.contactVideoCallIc.setOnClickListener(v -> {
                itemActionListener.onClickVideoCall(this.contact.getNumber());
            });
        }

        public void setItemActionListener(ContactsAdapter.ItemActionListener listener) {
            this.itemActionListener = listener;
        }
    }


}
