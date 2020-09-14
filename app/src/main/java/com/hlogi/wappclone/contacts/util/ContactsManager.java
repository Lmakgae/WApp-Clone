package com.hlogi.wappclone.contacts.util;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import com.hlogi.wappclone.util.Constants;

import java.util.ArrayList;

public class ContactsManager {

    private static final String MESSAGE_MIME_TYPE = "vnd.android.cursor.item/com.hlogi.wappclone.message";
    private static final String VOICE_MIME_TYPE = "vnd.android.cursor.item/com.hlogi.wappclone.voice";
    private static final String VIDEO_MIME_TYPE = "vnd.android.cursor.item/com.hlogi.wappclone.video";

    /**
     * Method to register a number with the app
     */
    public static void registerNumber(@NonNull Context context, String contact_id, String number, Integer type, String contactName) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();

        // insert account name and type
        operations.add(ContentProviderOperation
                .newInsert(addCallerIsSyncAdapterParameter(ContactsContract.RawContacts.CONTENT_URI, true))
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, Constants.ACCOUNT_NAME)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, Constants.ACCOUNT_TYPE)
                .withValue(ContactsContract.RawContacts.AGGREGATION_MODE,
                        ContactsContract.RawContacts.AGGREGATION_MODE_DEFAULT)
                .build()
        );

        // insert by phone number (because its unique)
        operations.add(ContentProviderOperation
                .newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI, true))
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, number) // Supply the number to be synced
                .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, type)
                .build()
        );

        // insert display name
        operations.add(ContentProviderOperation
                .newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI, true))
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contactName) // Supply the display name to be synced
                .build()
        );

        /* (This will be the data retrieved when you click you app from contacts) */
        // insert your app data for message
        operations.add(ContentProviderOperation
                .newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI, true))
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, MESSAGE_MIME_TYPE)
                .withValue(ContactsContract.Data.DATA1, number)
                .withValue(ContactsContract.Data.DATA2, contactName)
                .withValue(ContactsContract.Data.DATA3, "Message " + number)
                .build()
        );

        // insert your app data for voice call
        operations.add(ContentProviderOperation
                .newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI, true))
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, VOICE_MIME_TYPE)
                .withValue(ContactsContract.Data.DATA1, number)
                .withValue(ContactsContract.Data.DATA2, contactName)
                .withValue(ContactsContract.Data.DATA3, "Voice call " + number)
                .build()
        );

        // insert your app data for video call
        operations.add(ContentProviderOperation
                .newInsert(addCallerIsSyncAdapterParameter(ContactsContract.Data.CONTENT_URI, true))
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE, VIDEO_MIME_TYPE)
                .withValue(ContactsContract.Data.DATA1, number)
                .withValue(ContactsContract.Data.DATA2, contactName)
                .withValue(ContactsContract.Data.DATA3, "Video call " + number)
                .build()
        );

        ContentProviderResult[] contentProviderResult = new ContentProviderResult[0];
        try {
            contentProviderResult = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);
        } catch (OperationApplicationException | RemoteException e) {
            e.printStackTrace();
        }

        /*
        Manually aggregate the new rawcontactid with a rawcontactid of number to be registered
        (If automatic aggregation does not work)
         */
        String newRawContactId = String.valueOf(ContentUris.parseId(contentProviderResult[0].uri));

        manuallyAggregate(context, newRawContactId, contact_id);
    }

    /**
     * Method to delete RawContact of number specified
     */
    public static void deleteNumber(@NonNull Context context, String number) {
        //region Get RawContactId's for the number
        Cursor rawContactIdCursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID},
                ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
                new String[]{number},
                null);

        ArrayList<String> rawContactIdList = new ArrayList<>();
        if(rawContactIdCursor != null && rawContactIdCursor.moveToFirst()) {
            do{
                rawContactIdList.add(rawContactIdCursor.getString(rawContactIdCursor.getColumnIndexOrThrow(
                        ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID
                )));
            }while (rawContactIdCursor.moveToNext());

        }
        //endregion
        if (rawContactIdCursor != null)
            rawContactIdCursor.close();

        //region Get the RawContactId with the app's account type
        String appRawContactId = "";
        for(String rawContactId : rawContactIdList) {
            Cursor accTypeCursor = context.getContentResolver().query(ContactsContract.RawContacts.CONTENT_URI,
                    new String[]{ContactsContract.RawContacts.ACCOUNT_TYPE},
                    ContactsContract.RawContacts._ID + " = ?",
                    new String[]{rawContactId},
                    null);

            if(accTypeCursor != null && accTypeCursor.moveToFirst()) {
                do{
                    String accountType = accTypeCursor.getString(accTypeCursor.getColumnIndexOrThrow(
                            ContactsContract.RawContacts.ACCOUNT_TYPE));
                    if(accountType.equals(Constants.ACCOUNT_TYPE)) {
                        appRawContactId = rawContactId;
                    }
                }while (accTypeCursor.moveToNext());
                accTypeCursor.close();
            }

            if (accTypeCursor != null)
                accTypeCursor.close();

            if(appRawContactId.isEmpty())
                break;
        }
        //endregion

        //region Perform delete
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        operations.add(ContentProviderOperation
                        .newDelete(ContactsContract.RawContacts.CONTENT_URI)
                        .withSelection(ContactsContract.RawContacts._ID + " = ?",
                        new String[]{appRawContactId})
                        .build()
        );

        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, operations);
        } catch (OperationApplicationException | RemoteException e) {
            e.printStackTrace();
        }
        //endregion
    }

    /**
     * Method to manually aggregate two raw contacts
     */
    private static void manuallyAggregate(@NonNull Context context, String id1, String id2) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        ops.add(ContentProviderOperation
                .newUpdate(ContactsContract.AggregationExceptions.CONTENT_URI)
                .withValue(ContactsContract.AggregationExceptions.TYPE, ContactsContract.AggregationExceptions.TYPE_KEEP_TOGETHER)
                .withValue(ContactsContract.AggregationExceptions.RAW_CONTACT_ID1, id1)
                .withValue(ContactsContract.AggregationExceptions.RAW_CONTACT_ID2, id2)
                .build()
        );

        try {
            context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (OperationApplicationException | RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check for sync enabled or disabled
     */
    private static Uri addCallerIsSyncAdapterParameter(Uri uri, @NonNull Boolean isSyncOperation) {
        if(isSyncOperation){
            return uri.buildUpon()
                    .appendQueryParameter(ContactsContract.CALLER_IS_SYNCADAPTER, "true")
                    .build();
        } else {
            return uri;
        }
    }

}
