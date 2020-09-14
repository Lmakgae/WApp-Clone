package com.hlogi.wappclone.contacts.data;

import android.content.Context;

import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.hlogi.wappclone.contacts.data.model.Contact;
import com.hlogi.wappclone.data.WAppDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class ContactsRepository {

    private final ContactsDao mDao;
    private final ExecutorService mIoExecutor;
    private static volatile ContactsRepository sInstance = null;

    public static ContactsRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (ContactsRepository.class) {
                if (sInstance == null) {
                    WAppDatabase database = WAppDatabase.getInstance(context);
                    sInstance = new ContactsRepository(database.contactDao(),
                            WAppDatabase.databaseRWExecutor);
                }
            }
        }
        return sInstance;
    }

    private ContactsRepository(ContactsDao dao, ExecutorService executor) {
        mIoExecutor = executor;
        mDao = dao;
    }

    @WorkerThread
    public LiveData<PagedList<Contact>> getAllContactsOnWApp() {
        return new LivePagedListBuilder<>(mDao.getAllContactsOnWApp(), 25).build();
    }

    public LiveData<List<Contact>> getContactsOtherNumbers(String id) {
        return mDao.getContactsOtherNumbers(id);
    }

    public LiveData<Contact> getContact(String number){
        return mDao.getContact(number);
    }

    public Contact getContactObject(String number) {
        return mDao.getContactObject(number);
    }

    public void insertContact(Contact contact) {
        mIoExecutor.execute(() -> mDao.insert(contact));
    }

    public void insertContacts(List<Contact> contacts) {
        mIoExecutor.execute(() -> mDao.insert(contacts));
    }

    public void deleteContact(Contact contact) {
        mIoExecutor.execute(() -> mDao.delete(contact));
    }

    public void deleteContact(String number) {
        mIoExecutor.execute(() -> mDao.delete(number));
    }

    public void deleteAllContacts() {
        mIoExecutor.execute(mDao::deleteAllContacts);
    }

}
