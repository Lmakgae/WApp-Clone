package com.hlogi.wappclone.contacts.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.PagedList;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hlogi.wappclone.chats.data.OnlineStatusRepository;
import com.hlogi.wappclone.chats.data.model.OnlineStatus;
import com.hlogi.wappclone.contacts.data.ContactsRepository;
import com.hlogi.wappclone.contacts.data.model.Contact;
import com.hlogi.wappclone.firebase.DatabaseDocumentLiveData;
import com.hlogi.wappclone.util.Resource;

import java.util.List;

public class ContactsViewModel extends ViewModel {

    private final ContactsRepository mRepository;
    private final OnlineStatusRepository mOnlineStatusRepository;
    private final FirebaseUser firebaseUser;
    private LiveData<PagedList<Contact>> mContactsOnWAppList;
    private final MutableLiveData<String> mCurrentContactNumber = new MutableLiveData<>("null");
    private final LiveData<Contact> mCurrentContact;
    private final LiveData<List<Contact>> mOtherCurrentContactNumbers;
    private final LiveData<Resource<OnlineStatus>> mOnlineStatus;

    public ContactsViewModel(ContactsRepository repository, OnlineStatusRepository onlineStatusRepository) {
        this.mRepository = repository;
        this.mOnlineStatusRepository = onlineStatusRepository;
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.mContactsOnWAppList = mRepository.getAllContactsOnWApp();
        this.mCurrentContact = Transformations.switchMap(mCurrentContactNumber,
                (number) -> getRepository().getContact(number));
        this.mOtherCurrentContactNumbers = Transformations.switchMap(mCurrentContact,
                (contact) -> getRepository().getContactsOtherNumbers(contact.getId()));
        this.mOnlineStatus = Transformations.switchMap(mCurrentContactNumber,
                (number) -> getOnlineStatusRepository().getContactStatus(number));
    }

    private ContactsRepository getRepository() {
        return mRepository;
    }


    public OnlineStatusRepository getOnlineStatusRepository() {
        return mOnlineStatusRepository;
    }

    public LiveData<PagedList<Contact>> getContactsOnWAppList() {
        if (mContactsOnWAppList == null) {
            mContactsOnWAppList = mRepository.getAllContactsOnWApp();
        }
        return mContactsOnWAppList;
    }


    public LiveData<Contact> getCurrentContact() {
        return mCurrentContact;
    }

    public void setCurrentContactNumber(@NonNull String number){
        if(mCurrentContactNumber.getValue() == null || mCurrentContactNumber.getValue().equals(number)) {
            return;
        }
        else {
            mCurrentContactNumber.setValue(number);
        }
    }

    public MutableLiveData<String> getCurrentContactNumber() {
        return mCurrentContactNumber;
    }

    public LiveData<List<Contact>> getOtherCurrentContactNumbers(){
        return mOtherCurrentContactNumbers;
    }

    public LiveData<Contact> getContact(String number) {
        return mRepository.getContact(number);
    }

    public LiveData<Resource<OnlineStatus>> getCurrentContactOnlineStatus() {
        return mOnlineStatus;
    }

    public DatabaseDocumentLiveData<OnlineStatus> getContactOnlineStatus(String number) {
        return mOnlineStatusRepository.getContactStatus(number);
    }

    public void setUserOnlineStatus(Boolean status) {
        mOnlineStatusRepository.setUserStatus(firebaseUser.getPhoneNumber(), status, Timestamp.now().toDate().getTime(), false, false);
    }

    public void setUserTypingStatus(Boolean status) {
        mOnlineStatusRepository.setUserStatus(firebaseUser.getPhoneNumber(), true, Timestamp.now().toDate().getTime(), status, false);
    }

    public void setUserRecordingStatus(Boolean status) {
        mOnlineStatusRepository.setUserStatus(firebaseUser.getPhoneNumber(), true, Timestamp.now().toDate().getTime(), false, status);
    }

    public void delete() {
        mRepository.deleteAllContacts();
    }
}
