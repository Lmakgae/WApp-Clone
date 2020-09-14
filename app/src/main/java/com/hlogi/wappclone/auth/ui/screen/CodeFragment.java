package com.hlogi.wappclone.auth.ui.screen;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.auth.viewmodel.AuthViewModel;
import com.hlogi.wappclone.auth.viewmodel.AuthViewModelFactory;
import com.hlogi.wappclone.databinding.FragmentCodeBinding;
import com.hlogi.wappclone.ui.dialogs.AlertDialogFragment;
import com.hlogi.wappclone.ui.dialogs.LoadingDialogFragment;

import java.util.ArrayList;

public class CodeFragment extends Fragment {

    private final static String TAG = "CodeFragment";
    private FragmentCodeBinding binding;
    private AuthViewModel viewModel;
    private ArrayList<EditText> codeEditTextList;
    private LoadingDialogFragment mLoadingDialog = new LoadingDialogFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCodeBinding.inflate(getLayoutInflater());
        AuthViewModelFactory viewModelFactory = AuthViewModelFactory.createFactory(requireActivity());
        viewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(AuthViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.loading().observe(getViewLifecycleOwner(), loading -> {
            if (loading)
                mLoadingDialog.show(getChildFragmentManager(), LoadingDialogFragment.class.getSimpleName());
            else
                if (mLoadingDialog.isVisible() && !isStateSaved()) {
                    mLoadingDialog.dismiss();
                }
        });

        binding.toolbar.setTitle(getString(R.string.verify_no_title, viewModel.getCountryCode().getValue(), viewModel.getPhoneNumber().getValue()));
        binding.no.setText(getString(R.string.phone_no_dynamically, viewModel.getCountryCode().getValue(), viewModel.getPhoneNumber().getValue()));
        binding.wrongNumber.setOnClickListener(v -> {
            viewModel.getCodeSentStatus().setValue(false);
            Navigation.findNavController(v).navigateUp();
        });

        viewModel.getFirebaseUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                if (user.isSuccessful()) {
                    if (mLoadingDialog.isVisible() ) {
                        mLoadingDialog.dismiss();
                    }
                    Navigation.findNavController(requireView()).navigate(R.id.action_codeFragment_to_profileInfoFragment);
                } else {
                    if (!user.isEmpty()) {
                        //Handle error
                        if (user.error() != null) {
                            DialogFragment dialog = new AlertDialogFragment(user.error().getLocalizedMessage());
                            dialog.show(getChildFragmentManager(), AlertDialogFragment.class.getSimpleName());
                        }
                    }
                }

            }
        });

        //TODO: Fix this hack
        Navigation.findNavController(requireView()).addOnDestinationChangedListener((controller, destination, arguments) -> {
            if ( destination.getId() == R.id.verifyNoFragment) {
                viewModel.getCodeSentStatus().setValue(false);
            }
        });

        loadTextWatchers();

        binding.code1.post(() -> {
            binding.code1.requestFocus();
            InputMethodManager imgr = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imgr != null) {
                imgr.showSoftInput(binding.code1, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        binding.nextBtn.setOnClickListener(v -> {
            verifyCode();
        });

    }

    private void loadTextWatchers() {
        codeEditTextList = new ArrayList<>();
        codeEditTextList.add(binding.code1);
        codeEditTextList.add(binding.code2);
        codeEditTextList.add(binding.code3);
        codeEditTextList.add(binding.code4);
        codeEditTextList.add(binding.code5);
        codeEditTextList.add(binding.code6);

        for (int i = 0; i < codeEditTextList.size(); i++) {

            final int finalI = i;

            codeEditTextList.get(finalI).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 0) {
                        if (before == 1 && count == 0) {
                            if ((finalI - 1) >= 0) {
                                codeEditTextList.get(finalI - 1).requestFocus();
                            }
                        }
                    } else {
                        if (before == 0 && count == 1) {
                            if (codeEditTextList.get(finalI).getId() == binding.code6.getId()) {
                                verifyCode();
                            } else {
                                codeEditTextList.get(finalI + 1).requestFocus();
                            }
                        }
                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mLoadingDialog.isVisible()) {
            mLoadingDialog.dismiss();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        binding = null;
    }

    private void verifyCode() {
        viewModel.verifyCode(
                binding.code1.getText().toString() +
                binding.code2.getText().toString() +
                binding.code3.getText().toString() +
                binding.code4.getText().toString() +
                binding.code5.getText().toString() +
                binding.code6.getText().toString());
    }

}
