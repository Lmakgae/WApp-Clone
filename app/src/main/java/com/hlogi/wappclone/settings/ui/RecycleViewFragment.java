package com.hlogi.wappclone.settings.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.databinding.FragmentRecycleviewBinding;
import com.hlogi.wappclone.settings.adapter.IconTitleSubtitleAdapter;

import java.util.List;

public class RecycleViewFragment extends Fragment {

    static final String ARG_TITLE = "title";
    static final String ARG_TITLES = "titles";
    static final String ARG_SUBTITLES = "subtitles";
    static final String ARG_ICONS = "icons";
    static final String ARG_ACTIONS = "actions";
    private FragmentRecycleviewBinding binding;
    private String title;
    private List<Integer> titles;
    private List<Integer> subtitles;
    private List<Integer> icons;
    private List<Integer> actions;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRecycleviewBinding.inflate(inflater, container, false);

        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            titles = getArguments().getIntegerArrayList(ARG_TITLES);
            subtitles = getArguments().getIntegerArrayList(ARG_SUBTITLES);
            icons = getArguments().getIntegerArrayList(ARG_ICONS);
            actions = getArguments().getIntegerArrayList(ARG_ACTIONS);
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        IconTitleSubtitleAdapter adapter = new IconTitleSubtitleAdapter(titles, subtitles, icons);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));

        binding.recyclerView.setAdapter(adapter);

        adapter.setItemActionListener(title -> {

            for (int i = 0; i < titles.size(); i++) {
                if(title.equals(titles.get(i))) {
                    if(!actions.get(i).equals(R.string.null_t))
                        Navigation.findNavController(requireView()).navigate(actions.get(i));
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