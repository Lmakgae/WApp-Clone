package com.hlogi.wappclone.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.google.firebase.firestore.QuerySnapshot;
import com.hlogi.wappclone.contacts.data.ContactsRepository;
import com.hlogi.wappclone.contacts.data.model.Contact;
import com.hlogi.wappclone.contacts.data.model.Contacts;
import com.hlogi.wappclone.contacts.util.ContactsManager;
import com.hlogi.wappclone.firebase.FirebaseDatabasePaths;
import com.hlogi.wappclone.ui.main.MainActivity;
import com.hlogi.wappclone.util.Constants;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private CollectionReference public_users;
    private ContactsRepository repository;

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        this.repository = ContactsRepository.getInstance(context);
        this.public_users = FirebaseFirestore.getInstance().collection(FirebaseDatabasePaths.COLLECTIONS_USERS_PUBLIC);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        ArrayList<Contacts> mContactsList = getContactData();
        ArrayList<Contact> registeredNumbers = getRegisteredNumbersFromServer();

        if (registeredNumbers.isEmpty())
            return;

        for (Contacts contacts: mContactsList) {

            for (int j = 0; j < contacts.getNumbers().size(); j++) {
                String number = contacts.getNumbers().get(j);

                if (isNumberAlreadyRegistered(number)) {
                    boolean numberExistsInServer = false;

                    for (int i = 0; i < registeredNumbers.size(); i++) {

                        if(registeredNumbers.get(i).getNumber().equals(number)) {
                            numberExistsInServer = true;

                            repository.insertContact(new Contact(
                                    number,
                                    contacts.getId(),
                                    registeredNumbers.get(i).getServer_id(),
                                    contacts.getDisplay_name(),
                                    registeredNumbers.get(i).getName(),
                                    contacts.getNumbersType().get(j),
                                    contacts.getLast_updated_timestamp(),
                                    registeredNumbers.get(i).getProfile_photo_url(),
                                    registeredNumbers.get(i).getProfile_photo_path(),
                                    registeredNumbers.get(i).getDevice_instance_id(),
                                    registeredNumbers.get(i).getStatus(),
                                    registeredNumbers.get(i).getStatus_timestamp()
                            ));
                        }
                    }

                    if (!numberExistsInServer) {
                        ContactsManager.deleteNumber(getContext(), number);
                        repository.deleteContact(number);
                    }

                } else {
                    for (int i = 0; i < registeredNumbers.size(); i++) {

                        if(registeredNumbers.get(i).getNumber().equals(number)) {

                           ContactsManager.registerNumber(getContext(), contacts.getId() , number , contacts.getNumbersTypeInts().get(j),contacts.getDisplay_name());
                            repository.insertContact(new Contact(
                                    number,
                                    contacts.getId(),
                                    registeredNumbers.get(i).getServer_id(),
                                    contacts.getDisplay_name(),
                                    registeredNumbers.get(i).getName(),
                                    contacts.getNumbersType().get(j),
                                    contacts.getLast_updated_timestamp(),
                                    registeredNumbers.get(i).getProfile_photo_url(),
                                    registeredNumbers.get(i).getProfile_photo_path(),
                                    registeredNumbers.get(i).getDevice_instance_id(),
                                    registeredNumbers.get(i).getStatus(),
                                    registeredNumbers.get(i).getStatus_timestamp()
                            ));
                        }
                    }
                }

            }


        }

        // send broadcast response for manual refresh request
        getContext().sendBroadcast(new Intent(MainActivity.ACTION_SYNC_COMPLETED));
    }

    /**
     * Method to get registered numbers from the server
     */
    @NonNull
    private ArrayList<Contact> getRegisteredNumbersFromServer() {
       ArrayList<Contact> contacts = new ArrayList<>();

        Task<QuerySnapshot> querySnapshotTask = public_users.get();
        try {
            QuerySnapshot querySnapshot = Tasks.await(querySnapshotTask);
            if (querySnapshotTask.isComplete() && querySnapshotTask.isSuccessful()) {

                if (!querySnapshot.isEmpty()) {
                    for (QueryDocumentSnapshot documentSnapshot : querySnapshot) {
                        contacts.add(documentSnapshot.toObject(Contact.class));
                    }
                }
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    /**
     * Method to check if number is already registered
     */
    private Boolean isNumberAlreadyRegistered(String number) {
        boolean isRegistered = false;

        //region Get RawContactId's
        Cursor rawContactIdCursor = getContext().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID},
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[]{number},
                null
        );

        ArrayList<String> rawContactIdList = new ArrayList<>();

        if(rawContactIdCursor != null && rawContactIdCursor.moveToFirst()) {
            do {
                rawContactIdList.add(rawContactIdCursor.getString(rawContactIdCursor
                        .getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID)));
            }while (rawContactIdCursor.moveToNext());

        }

        if (rawContactIdCursor != null)
            rawContactIdCursor.close();

        //endregion

        //region Check Account type
        for(String rawContactId : rawContactIdList) {
            Cursor accTypeCursor = getContext().getContentResolver().query(
                    ContactsContract.RawContacts.CONTENT_URI,
                    new String[]{ContactsContract.RawContacts.ACCOUNT_TYPE},
                    ContactsContract.RawContacts._ID + " = ?",
                    new String[]{rawContactId},
                    null);

            ArrayList<String> accTypeList = new ArrayList<>();
            if(accTypeCursor != null && accTypeCursor.moveToFirst()) {
                do {
                    accTypeList.add(accTypeCursor.getString(accTypeCursor
                            .getColumnIndexOrThrow(ContactsContract.RawContacts.ACCOUNT_TYPE)));
                } while (accTypeCursor.moveToNext());
            }

            if (accTypeCursor != null)
                accTypeCursor.close();

            if(accTypeList.contains(Constants.ACCOUNT_TYPE)) {
                isRegistered = true;
                break;
            }
        }
        //endregion

        return isRegistered;
    }

    /**
     * Method to get all contact data
     */
    @NonNull
    private ArrayList<Contacts> getContactData() {
        ArrayList<Contacts> list = new ArrayList<>();

        ContentResolver cr = getContext().getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        if ((cursor != null ? cursor.getCount() : 0) > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.Contacts._ID));

                String name = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                String timestamp = cursor.getString(cursor.getColumnIndex(
                        ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP));

                if (cursor.getInt(cursor.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);

                    ArrayList<String> numbers = new ArrayList<>();
                    ArrayList<String> numbersType = new ArrayList<>();
                    ArrayList<Integer> numbersTypeInts = new ArrayList<>();

                    while (pCur != null && pCur.moveToNext()) {
                        String normal_number = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER));

                        int phoneNoType = pCur.getInt(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.TYPE));
                        String customLabel = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.LABEL));
                        CharSequence typeLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getContext().getResources(), phoneNoType, customLabel);

                        if (normal_number != null) {
                            numbers.add(normal_number);
                            numbersType.add(typeLabel.toString());
                            numbersTypeInts.add(phoneNoType);
                        }
                    }
                    if (pCur != null)
                        pCur.close();

                    if (!numbers.isEmpty())
                        list.add(new Contacts(
                            id,
                            name,
                            numbers,
                            numbersType,
                            numbersTypeInts,
                            Long.parseLong(timestamp)
                    ));
                }

            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

}
