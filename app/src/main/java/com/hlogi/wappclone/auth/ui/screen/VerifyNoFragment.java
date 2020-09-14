package com.hlogi.wappclone.auth.ui.screen;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.auth.data.model.Country;
import com.hlogi.wappclone.auth.viewmodel.AuthViewModel;
import com.hlogi.wappclone.auth.viewmodel.AuthViewModelFactory;
import com.hlogi.wappclone.auth.ui.dialog.VerifyNoAlertDialog;
import com.hlogi.wappclone.databinding.FragmentVerifyNoBinding;
import com.hlogi.wappclone.ui.dialogs.AlertDialogFragment;
import com.hlogi.wappclone.ui.dialogs.LoadingDialogFragment;

import java.util.List;

public class VerifyNoFragment extends Fragment{

    private FragmentVerifyNoBinding binding;
    private AuthViewModel viewModel;
    private LoadingDialogFragment mLoadingDialog = new LoadingDialogFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVerifyNoBinding.inflate(inflater, container, false);
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
            if (mLoadingDialog.isVisible()) {
                mLoadingDialog.dismiss();
            }
        });

        viewModel.getVerifyNumberException().observe(getViewLifecycleOwner(), exception -> {
            if (exception != null) {
                DialogFragment dialog = new AlertDialogFragment(exception.getLocalizedMessage());
                dialog.show(getChildFragmentManager(), AlertDialogFragment.class.getSimpleName());
            }
        });

        viewModel.getCodeSentStatus().observe(getViewLifecycleOwner(), sent -> {
            if (sent) {
                Navigation.findNavController(requireView()).navigate(R.id.action_verifyNoFragment_to_codeFragment);
            }
        });

        viewModel.getSelectedCountry().observe(getViewLifecycleOwner(), country -> {
            if (country != null) {
                binding.codeEditText.setText(country.getCode());
                binding.countryText.setText(country.getName());
            }
        });

        binding.countryText.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_verifyNoFragment_to_chooseCountryFragment));

        binding.countryDropdown.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_verifyNoFragment_to_chooseCountryFragment));

        binding.nextBtn.setOnClickListener(v -> {
            if ( binding.noEditText.getText().toString().length() > 8 &&
                    binding.codeEditText.getText().toString().length() > 0 &&
                    !binding.countryText.getText().toString().equals(getString(R.string.invalid_country_code)) &&
                    !binding.countryText.getText().equals(getString(R.string.choose_a_country)) ) {

                showVerifyNoAlertDialog(binding.codeEditText.getText().toString(), binding.noEditText.getText().toString());
            } else {
                if (binding.codeEditText.getText().toString().isEmpty() || binding.countryText.getText().equals(getString(R.string.choose_a_country))) {
                    showAlertDialog(getString(R.string.choose_country_or_enter_code));
                } else if (binding.countryText.getText().toString().equals(getString(R.string.invalid_country_code))) {
                    showAlertDialog(getString(R.string.enter_valid_code));
                } else if (binding.noEditText.getText().toString().isEmpty()) {
                    showAlertDialog(getString(R.string.enter_phone_no));
                } else if (binding.noEditText.getText().toString().length() < 9) {
                    showAlertDialog(getString(R.string.enter_valid_phone_no));
                }
            }
        });

        binding.codeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    if (before == 1 && count == 0) {
                        binding.countryText.setText(getString(R.string.choose_a_country));
                    }
                } else {
                    if ((before == 0 && count == 1) || (before == 1 && count == 0)) {
                        if (viewModel.getCountryList() != null && viewModel.getCountryList().getValue() != null) {
                            List<Country> countryList = viewModel.getCountryList().getValue();
                            boolean found = false;
                            for (Country country :
                                    countryList) {
                                if (country.getCode().equals(binding.codeEditText.getEditableText().toString())) {
                                    viewModel.getSelectedCountry().setValue(country);
                                    found = true;
                                    return;
                                }
                            }
                            if (!found)
                                binding.countryText.setText(getString(R.string.invalid_country_code));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mLoadingDialog.isVisible()) {
            mLoadingDialog.dismiss();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void initiateVerification() {
        viewModel.verifyNo(binding.codeEditText.getText().toString(), binding.noEditText.getText().toString(), requireActivity());
    }

    private void showAlertDialog(String message) {
        DialogFragment dialog = new AlertDialogFragment(message);
        dialog.show(getChildFragmentManager(), AlertDialogFragment.class.getSimpleName());
    }

    private void showVerifyNoAlertDialog(String code, String no) {
        VerifyNoAlertDialog dialog = new VerifyNoAlertDialog(code, no);
        dialog.setDialogListener(dialog1 -> {
            dialog1.dismiss();
            initiateVerification();
        });
        dialog.show(getChildFragmentManager(), VerifyNoAlertDialog.class.getSimpleName());
    }

}