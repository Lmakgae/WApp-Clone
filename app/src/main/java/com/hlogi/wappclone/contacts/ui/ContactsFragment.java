package com.hlogi.wappclone.contacts.ui;

import android.Manifest;
import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.chats.ui.dialog.ViewProfileDialog;
import com.hlogi.wappclone.contacts.util.ContactsManager;
import com.hlogi.wappclone.contacts.viewmodel.ContactsViewModel;
import com.hlogi.wappclone.contacts.viewmodel.ContactsViewModelFactory;
import com.hlogi.wappclone.contacts.adapters.ContactsAdapter;
import com.hlogi.wappclone.contacts.data.model.Contact;
import com.hlogi.wappclone.databinding.FragmentContactsBinding;
import com.hlogi.wappclone.qr.QRcodeActivity;
import com.hlogi.wappclone.util.Constants;
import com.hlogi.wappclone.ui.main.MainActivity;

import java.util.List;

public class ContactsFragment extends Fragment {

    static final String TAG = ContactsFragment.class.getSimpleName();
    static final int REQUEST_WRITE_CONTACTS = 79;
    static final String ARG_SIDE_KEY = "side";
    static final String ARG_PROFILE_PHOTO = "profile_photo";
    static final String ARG_DISPLAY_NAME = "display_name";
    static final String ARG_NUMBER = "number";
    static final String ARG_CONVO_ID = "convo_id";
    private FragmentContactsBinding binding;
    private ContactsViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentContactsBinding.inflate(inflater, container, false);

        assert getArguments() != null;
        binding.setSide(getArguments().getString(ARG_SIDE_KEY));

        ContactsViewModelFactory viewModelFactory = ContactsViewModelFactory.createFactory(requireActivity());
        mViewModel = new ViewModelProvider(requireActivity(), viewModelFactory).get(ContactsViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.toolbar.setNavigationOnClickListener(v -> {
            Navigation.findNavController(requireView()).navigateUp();
        });


        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            //mViewModel.getContacts(requireActivity().getApplicationContext());
        } else {
            requestPermission();
        }

        ContactsAdapter adapter = new ContactsAdapter(binding.getSide());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false));

        binding.recyclerView.setAdapter(adapter);

        mViewModel.getContactsOnWAppList().observe(getViewLifecycleOwner(), contacts -> {
            adapter.submitList(contacts);
            binding.noContacts.setText(getResources().getQuantityString(
                    R.plurals.number_of_contacts, contacts.size(), contacts.size()));
        });

        adapter.setItemActionListener(new ContactsAdapter.ItemActionListener() {
            @Override
            public void onClickContact(String number) {
                Bundle bundle = new Bundle();
                bundle.putString(ARG_NUMBER, number);
                bundle.putString(ARG_CONVO_ID, getString(R.string.null_t));
                Navigation.findNavController(requireView()).navigate(R.id.action_contactsFragment_to_messagesFragment, bundle);
            }

            @Override
            public void onClickCall(String contact_id) {

            }

            @Override
            public void onClickVideoCall(String contact_id) {

            }

            @Override
            public void onClickImage(Contact contact) {
                ViewProfileDialog dialog = new ViewProfileDialog(contact);
                dialog.setActionListener(viewProfileDialogListener);
                dialog.show(getParentFragmentManager(), "ViewProfileDialog");
            }
        });

        binding.newContactText.setOnClickListener(v -> {

        });

        binding.newContactQr.setOnClickListener(v -> {
            requireActivity().startActivity(new Intent(requireActivity(), QRcodeActivity.class));
        });


        binding.toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_contact_invite:
                    return true;
                case R.id.menu_contacts_refresh:
                    refreshContacts();
                    return true;
                case R.id.menu_contacts_help:
                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_contactsFragment_to_contactsHelpFragment);
                    return true;
                case R.id.menu_contacts_contacts:
                    Intent contacts_intent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
                    // Verify it resolves
                    PackageManager packageManager = requireActivity().getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(contacts_intent, 0);
                    boolean isIntentSafe = activities.size() > 0;

                    // Start an activity if it's safe
                    if (isIntentSafe) {
                        startActivity(contacts_intent);
                    }
                    return true;
            }

            return false;
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_SYNC_COMPLETED);
        requireActivity().registerReceiver(mSyncBroadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        requireActivity().unregisterReceiver(mSyncBroadcastReceiver);
        super.onPause();
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.WRITE_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_CONTACTS},
                    REQUEST_WRITE_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_WRITE_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //mViewModel.getContacts(requireActivity().getApplicationContext());
                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        binding = null;
    }

    private void refreshContacts() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(new Account(Constants.ACCOUNT_NAME, Constants.ACCOUNT_TYPE), ContactsContract.AUTHORITY, bundle);
        binding.refreshProgressBar.setVisibility(View.VISIBLE);
    }

    private ViewProfileDialog.ViewProfileDialogListener viewProfileDialogListener = new ViewProfileDialog.ViewProfileDialogListener() {
        @Override
        public void onClickProfilePicture(String profile_photo, String display_name) {
            Bundle bundle = new Bundle();
            bundle.putString(ARG_PROFILE_PHOTO, profile_photo);
            bundle.putString(ARG_DISPLAY_NAME, display_name);
            Navigation.findNavController(requireView()).navigate(R.id.action_contactsFragment_to_viewProfilePictureFragment, bundle);
        }

        @Override
        public void onClickMessage(String number) {
            Bundle bundle = new Bundle();
            bundle.putString(ARG_NUMBER, number);
            bundle.putString(ARG_CONVO_ID, getString(R.string.null_t));
            Navigation.findNavController(requireView()).navigate(R.id.action_contactsFragment_to_messagesFragment, bundle);
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
            Navigation.findNavController(requireView()).navigate(R.id.action_contactsFragment_to_viewProfileFragment, bundle);
        }
    };

    private BroadcastReceiver mSyncBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            binding.refreshProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(requireActivity(), R.string.contact_list_is_updated, Toast.LENGTH_LONG).show();
        }
    };
}