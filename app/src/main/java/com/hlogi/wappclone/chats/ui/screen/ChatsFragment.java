package com.hlogi.wappclone.chats.ui.screen;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.chats.viewmodel.ChatsViewModel;
import com.hlogi.wappclone.chats.viewmodel.ChatsViewModelFactory;
import com.hlogi.wappclone.chats.adapters.ChatsAdapter;
import com.hlogi.wappclone.chats.ui.dialog.ViewProfileDialog;
import com.hlogi.wappclone.contacts.data.model.Contact;
import com.hlogi.wappclone.databinding.FragmentChatsBinding;

public class ChatsFragment extends Fragment {

    static final String TAG = ChatsFragment.class.getSimpleName();
    static final String ARG_PROFILE_PHOTO = "profile_photo";
    static final String ARG_DISPLAY_NAME = "display_name";
    static final String ARG_NUMBER = "number";
    static final String ARG_CONVO_ID = "convo_id";
    static final String ARG_UNREAD = "unread";
    static final String ARG_UNREAD_MESSAGE_ID = "unread_id";
    private FragmentChatsBinding binding;
    private ChatsViewModel chatsViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater, container, false);

        ChatsViewModelFactory chatsViewModelFactory = ChatsViewModelFactory.createFactory(requireActivity());
        chatsViewModel = new ViewModelProvider(requireActivity(), chatsViewModelFactory).get(ChatsViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ChatsAdapter adapter = new ChatsAdapter(chatsViewModel, getViewLifecycleOwner());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));

        adapter.setHasStableIds(true);

        binding.recyclerView.setAdapter(adapter);

        chatsViewModel.getChatConvoList().observe(getViewLifecycleOwner(), adapter::submitList);

        adapter.setItemActionListener(new ChatsAdapter.ItemActionListener() {
            @Override
            public void onClickProfilePicture(Contact contact) {
                ViewProfileDialog dialog = new ViewProfileDialog(contact);
                dialog.setActionListener(viewProfileDialogListener);
                dialog.show(getParentFragmentManager(), ViewProfileDialog.class.getSimpleName());
            }

            @Override
            public void onClickChat(String chat_convo_id, String number, Integer unread, String unread_id) {
                Bundle bundle = new Bundle();
                bundle.putString(ARG_CONVO_ID, chat_convo_id);
                bundle.putString(ARG_NUMBER, number);
                bundle.putInt(ARG_UNREAD, unread);
                bundle.putString(ARG_UNREAD_MESSAGE_ID, unread_id);
                Navigation.findNavController(requireView()).navigate(R.id.action_mainFragment_to_messagesFragment, bundle);
            }

        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private ViewProfileDialog.ViewProfileDialogListener viewProfileDialogListener = new ViewProfileDialog.ViewProfileDialogListener() {
        @Override
        public void onClickProfilePicture(String profile_photo, String display_name) {
            Bundle bundle = new Bundle();
            bundle.putString(ARG_PROFILE_PHOTO, profile_photo);
            bundle.putString(ARG_DISPLAY_NAME, display_name);
            Navigation.findNavController(requireView()).navigate(R.id.action_mainFragment_to_viewProfilePictureFragment, bundle);
        }

        @Override
        public void onClickMessage(String number) {
            Bundle bundle = new Bundle();
            bundle.putString(ARG_NUMBER, number);
            bundle.putString(ARG_CONVO_ID, getString(R.string.null_t));
            Navigation.findNavController(requireView()).navigate(R.id.action_mainFragment_to_messagesFragment, bundle);
        }

        @Override
        public void onClickCall(String number) {

        }

        @Override
        public void onClickVideoCall(String number) {

        }

        @Override
        public void onClickInfo(String number) {
            Bundle bundle = new Bundle();
            bundle.putString(ARG_NUMBER, number);
            Navigation.findNavController(requireView()).navigate(R.id.action_mainFragment_to_viewProfileFragment, bundle);
        }

    };
}
