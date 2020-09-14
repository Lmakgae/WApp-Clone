package com.hlogi.wappclone.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.hlogi.wappclone.auth.data.UserDao;
import com.hlogi.wappclone.auth.data.model.User;
import com.hlogi.wappclone.chats.data.ChatConversationDao;
import com.hlogi.wappclone.chats.data.MessagesDao;
import com.hlogi.wappclone.chats.data.UploadMediaSessionDao;
import com.hlogi.wappclone.chats.data.model.ChatConversation;
import com.hlogi.wappclone.chats.data.model.Message;
import com.hlogi.wappclone.chats.data.model.UploadMediaSession;
import com.hlogi.wappclone.contacts.data.model.Contact;
import com.hlogi.wappclone.contacts.data.ContactsDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {User.class, Contact.class, ChatConversation.class, Message.class, UploadMediaSession.class}, version = 17)
@TypeConverters({Converters.class})
public abstract class WAppDatabase extends RoomDatabase {

    private static final int NUMBER_OF_THREADS = 4;

    public abstract UserDao userDao();

    public abstract ContactsDao contactDao();

    public abstract ChatConversationDao chatConversationDao();

    public abstract MessagesDao messagesDao();

    public abstract UploadMediaSessionDao uploadMediaSessionDao();

    private static volatile WAppDatabase sInstance = null;

    public static final ExecutorService databaseRWExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    @NonNull
    public static WAppDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (WAppDatabase.class) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                                WAppDatabase.class,
                                "WApp_database")
                                .fallbackToDestructiveMigration()
                                .setQueryExecutor(databaseRWExecutor)
                                .setTransactionExecutor(databaseRWExecutor)
                                .addCallback(new Callback() {
                                    @Override
                                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                        super.onCreate(db);

                                    }
                                }).build();
                }
            }
        }
        return sInstance;
    }

}
