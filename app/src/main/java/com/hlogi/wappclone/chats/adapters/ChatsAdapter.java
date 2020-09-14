package com.hlogi.wappclone.chats.adapters;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hlogi.wappclone.R;
import com.hlogi.wappclone.chats.data.model.ChatConversation;
import com.hlogi.wappclone.chats.data.model.Message;
import com.hlogi.wappclone.chats.data.model.OnlineStatus;
import com.hlogi.wappclone.chats.viewmodel.ChatsViewModel;
import com.hlogi.wappclone.contacts.data.model.Contact;
import com.hlogi.wappclone.databinding.ChatsListItemBinding;
import com.hlogi.wappclone.firebase.DatabaseDocumentLiveData;
import com.hlogi.wappclone.util.BindingAdapterUtil;

import java.util.List;
import java.util.Objects;

import static com.hlogi.wappclone.chats.data.model.ChatConversation.DIFF_CALLBACK;

public class ChatsAdapter extends PagedListAdapter<ChatConversation, ChatsAdapter.ViewHolder> {

    private ItemActionListener mItemActionListener;
    private ChatsViewModel chatsViewModel;
    private FirebaseUser userProfile;
    private LifecycleOwner lifecycleOwner;

    public ChatsAdapter(ChatsViewModel chatsViewModel, LifecycleOwner lifecycleOwner) {
        super(DIFF_CALLBACK);
        this.chatsViewModel = chatsViewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.userProfile = FirebaseAuth.getInstance().getCurrentUser();
    }

    public interface ItemActionListener {
        void onClickProfilePicture(Contact contact);
        void onClickChat(String chat_convo_id, String number, Integer unread, String unread_id);
    }

    public void setItemActionListener(ItemActionListener mItemActionListener) {
        this.mItemActionListener = mItemActionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChatsListItemBinding binding = ChatsListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setItemActionListener(mItemActionListener);
        holder.setLifecycleOwner(lifecycleOwner);

        ChatConversation conversation = getItem(position);
        String number = "";

        if (conversation != null) {
            for (int i = 0; i < conversation.getUsers().size(); i++) {
                if (!conversation.getUsers().get(i).equals(userProfile.getPhoneNumber()))
                    number = conversation.getUsers().get(i);
            }
        }
        holder.setNumber(number);
        holder.setContact(chatsViewModel.getContact(number));
        holder.setOnlineStatus(chatsViewModel.getContactOnlineStatus(number));
        assert conversation != null;
        holder.setUnread_messages(chatsViewModel.getUnreadMessages(conversation.getId()));
        holder.setLast_message(chatsViewModel.getChatLastMessage(conversation.getId()));
        holder.bindTo(conversation);
    }

    @Override
    public long getItemId(int position) {
        return Objects.hash(getItem(position).getId());
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ChatsListItemBinding binding;
        private ChatConversation chatConversation;
        private String number;
        private Integer unread;
        private String unread_id;
        private LiveData<Contact> contact;
        private LiveData<List<Message>> unread_messages;
        private LiveData<Message> last_message;
        private DatabaseDocumentLiveData<OnlineStatus> onlineStatus;
        private ItemActionListener itemActionListener;
        private LifecycleOwner lifecycleOwner;

        public ViewHolder(@NonNull ChatsListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(ChatConversation chatConversation) {
            this.chatConversation = chatConversation;
            binding.setNumber(this.number);
            binding.setConversation(this.chatConversation);

            contact.observe(lifecycleOwner, contactLiveData -> {
                if (contactLiveData != null) {
                    binding.setContact(contactLiveData);
                    if (contactLiveData.getDisplay_name().equals("null")) {
                        binding.displayNameTv.setText(contactLiveData.getNumber());
                    } else {
                        binding.displayNameTv.setText(contactLiveData.getDisplay_name());
                    }

                }

            });

            last_message.observe(lifecycleOwner, message -> {

                if (message != null) {
                    binding.setLastMessage(message);
                    if (message.getSender().equals(this.number) && message.getType().equals(Message.TYPE_TEXT)) {
                        binding.textOnlyGroup.setVisibility(View.VISIBLE);
                        binding.firstMsgTv.setText(message.getMessage());
                    } else if ( (message.getReceiver().equals(number) && message.getType().equals(Message.TYPE_TEXT)) ||
                            (message.getSender().equals(number) && message.getType().equals(Message.TYPE_MEDIA)) ) {
                        binding.icTextGroup.setVisibility(View.VISIBLE);
                        if (message.getReceiver().equals(number) && message.getType().equals(Message.TYPE_TEXT)) {
                            binding.secondMsgTv.setText(message.getMessage());
                            BindingAdapterUtil.setSentMessageIcon(
                                    binding.icTextIcIv,
                                    message.getMessageMetadata().getHasPendingWrites(),
                                    message.getDelivered(),
                                    message.getRead()
                            );
                        } else if (message.getSender().equals(number) && message.getType().equals(Message.TYPE_MEDIA)) {
                            BindingAdapterUtil.setChatMediaIcons(
                                    binding.icTextIcIv,
                                    message.getMedia_type(),
                                    message.getMedia_played()
                            );
                            BindingAdapterUtil.setChatMediaMessage(
                                    binding.secondMsgTv,
                                    message.getMedia_type(),
                                    message.getMedia_caption(),
                                    message.getMedia_metadata().getDuration()
                            );
                        }
                    } else if (message.getReceiver().equals(number) && message.getType().equals(Message.TYPE_MEDIA)) {
                        binding.icIcTextGroup.setVisibility(View.VISIBLE);
                        BindingAdapterUtil.setSentMessageIcon(
                                binding.firstIcIv,
                                message.getMessageMetadata().getHasPendingWrites(),
                                message.getDelivered(),
                                message.getRead()
                        );
                        BindingAdapterUtil.setChatMediaIcons(
                                binding.secondIcIv,
                                message.getMedia_type(),
                                message.getMedia_played()
                        );
                        BindingAdapterUtil.setChatMediaMessage(
                                binding.thirdMsgTv,
                                message.getMedia_type(),
                                message.getMedia_caption(),
                                message.getMedia_metadata().getDuration()
                        );
                    }
                }

            });

            onlineStatus.observe(lifecycleOwner, onlineStatus -> {
                if (onlineStatus != null) {
                    if (onlineStatus.isSuccessful()) {
                        binding.setTyping(onlineStatus.data().getTyping());
                        binding.setRecording(onlineStatus.data().getRecording());
                        BindingAdapterUtil.setChatConvoTextView(
                                binding.contactStatusTv,
                                onlineStatus.data().getTyping(),
                                onlineStatus.data().getRecording()
                        );
                        BindingAdapterUtil.setChatConvoTextGroup(
                                binding.textLineGroups,
                                onlineStatus.data().getTyping(),
                                onlineStatus.data().getRecording()
                        );

                    } else {
                        binding.setTyping(false);
                        binding.setRecording(false);
                    }
                } else {
                    binding.setTyping(false);
                    binding.setRecording(false);
                }
            } );

            unread_messages.observe(lifecycleOwner, unread -> {
                if (unread.size() > 0) {
                    binding.counter.setVisibility(View.VISIBLE);
                    binding.counterNo.setVisibility(View.VISIBLE);
                    binding.counterNo.setText(String.valueOf(unread.size()));
                    TypedValue typedValue = new TypedValue();
                    binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
                    binding.time.setTextColor(typedValue.data);
                    this.unread = unread.size();
                    this.unread_id = unread.get(0).getMessage_id();
                } else {
                    binding.counter.setVisibility(View.GONE);
                    binding.counterNo.setVisibility(View.GONE);
                    this.unread = unread.size();
                    this.unread_id = "null";
                }

            });

            binding.displayPicImg.setOnClickListener(v ->
                    itemActionListener.onClickProfilePicture(this.contact.getValue()));

            binding.displayNameTv.setOnClickListener(v ->
                    itemActionListener.onClickChat(this.chatConversation.getId(), this.number, this.unread, this.unread_id));

            binding.textLineGroups.setOnClickListener(v ->
                    itemActionListener.onClickChat(this.chatConversation.getId(), this.number, unread, this.unread_id));
        }

        public void setItemActionListener(ItemActionListener listener) {
            this.itemActionListener = listener;
        }

        public void setContact(LiveData<Contact> contact) {
            this.contact = contact;
        }

        public void setOnlineStatus(DatabaseDocumentLiveData<OnlineStatus> onlineStatus) {
            this.onlineStatus = onlineStatus;
        }

        public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
            this.lifecycleOwner = lifecycleOwner;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public void setUnread_messages(LiveData<List<Message>> unread_messages) {
            this.unread_messages = unread_messages;
        }

        public void setLast_message(LiveData<Message> last_message) {
            this.last_message = last_message;
        }

    }


}
