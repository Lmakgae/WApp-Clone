package com.hlogi.wappclone.starred;

import android.os.Bundle;
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

import com.hlogi.wappclone.chats.viewmodel.MessagesViewModel;
import com.hlogi.wappclone.chats.viewmodel.MessagesViewModelFactory;
import com.hlogi.wappclone.databinding.FragmentStarredMessagesBinding;

public class StarredMessagesFragment extends Fragment {

    static final String ARG_CONVO_ID = "convo_id";
    public static final String ALL_STARRED_MESSAGES = "all_starred";
    private FragmentStarredMessagesBinding binding;
    private MessagesViewModel messagesViewModel;
    private String chat_convo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MessagesViewModelFactory messagesViewModelFactory = MessagesViewModelFactory.createFactory(requireActivity());
        messagesViewModel = new ViewModelProvider(requireActivity(), messagesViewModelFactory).get(MessagesViewModel.class);

        if (getArguments() != null) {
            chat_convo = getArguments().getString(ARG_CONVO_ID);
            assert chat_convo != null;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStarredMessagesBinding.inflate(inflater, container, false);
        binding.toolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        StarredMessagesAdapter adapter = new StarredMessagesAdapter();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));

        binding.recyclerView.setAdapter(adapter);

        messagesViewModel.getStarredMessagesFromChat(chat_convo).observe(getViewLifecycleOwner(), list -> {
            if (list != null) {
                if (!list.isEmpty()) {
                    binding.noMessagesLayout.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    adapter.submitList(list);
                } else {
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.noMessagesLayout.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        binding = null;
    }
}