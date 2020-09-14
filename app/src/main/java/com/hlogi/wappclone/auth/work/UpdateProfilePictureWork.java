package com.hlogi.wappclone.auth.work;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hlogi.wappclone.chats.work.WorkConstants;
import com.hlogi.wappclone.firebase.FirebaseDatabasePaths;
import com.hlogi.wappclone.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class UpdateProfilePictureWork extends Worker {

    private static final String TAG = UpdateProfilePictureWork.class.getSimpleName();
    private final CollectionReference reference;
    private StorageReference storageReference;
    private StorageReference profilePictureRef;

    public UpdateProfilePictureWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        storageReference = FirebaseStorage.getInstance().getReference();
        reference = FirebaseFirestore.getInstance().collection(FirebaseDatabasePaths.COLLECTION_USERS);
    }

    @NonNull
    @Override
    public Result doWork() {

        String file_name_path = getInputData().getString(WorkConstants.ARG_KEY_FILE_NAME);
        String action = getInputData().getString(WorkConstants.ARG_KEY_ACTION);

        profilePictureRef = storageReference.
                child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + "/profile_picture");

        assert action != null;
        switch (action) {
            case WorkConstants.ACTION_UPDATE_PROFILE_PICTURE:
                return updateProfilePicture(file_name_path);
            case WorkConstants.ACTION_REMOVE_PROFILE_PICTURE:
                return removeProfilePicture();
        }

        return Result.failure();
    }

    private Result updateProfilePicture(String file_name_path){
        try {

            File localFile = null;

            assert file_name_path != null;
            Uri fileUri = Uri.parse(file_name_path);

            if (Objects.equals(fileUri.getScheme(), "file")){
                localFile = new File(file_name_path);
            } else if (Objects.equals(fileUri.getScheme(), "content")) {
                localFile = FileUtil.copyFileFromUri(getApplicationContext(), fileUri, FileUtil.FILE_TYPE_VIDEO);
            }

            assert localFile != null;

            Task<Uri> uploadTask = profilePictureRef.putFile(Uri.fromFile(localFile)).continueWithTask(
                    new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }
                            return profilePictureRef.getDownloadUrl();
                        }
                    }
            );

            Uri downloadUri = Tasks.await(uploadTask);

            if (uploadTask.isComplete()) {
                Log.e(TAG, "doWork: Upload is complete");
                if (uploadTask.isSuccessful()) {

                    DocumentReference userRef = reference
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    Map<String, String> data = new HashMap<>();
                    data.put("profile_photo_url", downloadUri.toString());

                    Log.e(TAG, "doWork: Url: " + downloadUri.toString());

                    Tasks.await(userRef.set(data, SetOptions.merge()));

                    return Result.success();

                }
            }

        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return Result.failure();
    }

    private Result removeProfilePicture() {

        try {
            Task<Void> deleteOp = profilePictureRef.delete();

            Tasks.await(deleteOp);

            if (deleteOp.isComplete()) {
                if (deleteOp.isSuccessful()) {

                    DocumentReference userRef = reference
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    Map<String, String> data = new HashMap<>();
                    data.put("profile_photo_url", null);

                    Tasks.await(userRef.set(data, SetOptions.merge()));

                    return Result.success();

                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return Result.failure();
    }
}
