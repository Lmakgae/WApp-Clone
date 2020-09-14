package com.hlogi.wappclone.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.appcompat.app.AppCompatActivity;

import com.hlogi.wappclone.R;
import com.hlogi.wappclone.contacts.viewmodel.ContactsViewModel;
import com.hlogi.wappclone.contacts.viewmodel.ContactsViewModelFactory;
import com.hlogi.wappclone.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public static final String ACTION_SYNC_COMPLETED = "com.hlogi.wappclone.ACTION_FINISHED_SYNC";
    private ActivityMainBinding binding;
    private ContactsViewModel contactsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ContactsViewModelFactory contactsViewModelFactory = ContactsViewModelFactory.createFactory(this);
        contactsViewModel = new ViewModelProvider(this, contactsViewModelFactory).get(ContactsViewModel.class);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_main);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        contactsViewModel.setUserOnlineStatus(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        contactsViewModel.setUserOnlineStatus(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}