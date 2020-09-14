package com.hlogi.wappclone.util;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();

    public static final int FILE_TYPE_IMAGE = 1;

    public static final int FILE_TYPE_VIDEO = 2;

    public static final int FILE_TYPE_VOICE = 3;

    public static final int FILE_TYPE_PROFILE_PHOTO = 4;

    public static final String IMAGES_PATH = "WApp Clone/Media/WApp Clone Images";

    public static final String VIDEOS_PATH = "WApp Clone/Media/WApp Clone Videos";

    public static final String VOICE_NOTES_PATH = "WApp Clone/Media/WApp Clone Voice Notes";

    public static final String PROFILE_PHOTOS_PATH = "WApp Clone/Media/WApp Clone Profile Photos";

    public static final String IMAGE_SUFFIX = ".jpg";

    public static final String VIDEO_SUFFIX = ".mp4";

    public static final String VOICE_NOTE_SUFFIX = ".aac";

    public static File createMediaFile(@NonNull Integer file_type) throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileName;
        int index = 0;

        File storageDir;
        File media;

        switch (file_type) {
            case FILE_TYPE_IMAGE:
                fileName = "IMG-" + timestamp + "-WA";

                storageDir = new File(Environment.getExternalStorageDirectory(), IMAGES_PATH);

                if (!storageDir.exists()) {
                    storageDir.mkdirs();
                }

                media = new File(storageDir,fileName + index + IMAGE_SUFFIX);

                while (!media.createNewFile()) {
                    index++;
                    media = new File(storageDir, fileName + index + IMAGE_SUFFIX);
                }
                return media;
            case FILE_TYPE_VIDEO:
                fileName = "VID-" + timestamp + "-WA";

                storageDir = new File(Environment.getExternalStorageDirectory(), VIDEOS_PATH);

                if (!storageDir.exists()) {
                    storageDir.mkdirs();
                }

                media = new File(storageDir,fileName + index);

                while (!media.createNewFile()) {
                    index++;
                    media = new File(storageDir,fileName + index);
                }
                return media;
            case FILE_TYPE_VOICE:
                fileName = "VN-" + timestamp + "-WA";

                storageDir = new File(Environment.getExternalStorageDirectory(), VOICE_NOTES_PATH);
                if (!storageDir.exists()) {
                    storageDir.mkdirs();
                }

                media = new File(storageDir,fileName + index + VOICE_NOTE_SUFFIX);

                while (!media.createNewFile()) {
                    index++;
                    media = new File(storageDir, fileName + index + VOICE_NOTE_SUFFIX);
                }
                return media;
            case FILE_TYPE_PROFILE_PHOTO:
                fileName = "PP-" + timestamp + "-WA";

                storageDir = new File(Environment.getExternalStorageDirectory(), PROFILE_PHOTOS_PATH);
                if (!storageDir.exists()) {
                    storageDir.mkdirs();
                }

                media = new File(storageDir,fileName + index + IMAGE_SUFFIX);

                while (!media.createNewFile()) {
                    index++;
                    media = new File(storageDir, fileName + index + IMAGE_SUFFIX);
                }
                return media;
        }

        return null;
    }

    public static void deleteFile(String file_name) {
        File file = new File(file_name);
        if (file.exists() && file.canWrite())
            file.delete();
    }

    public static void scanMedia(String file_path, @NonNull Context context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(file_path);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    public static File copyFileFromUri(Context context, Uri uri, @NonNull Integer file_type) throws IOException {
        FileInputStream input = null;
        FileOutputStream output = null;

        File file = createMediaFile(file_type);
        if (file == null)
            return null;
        String filePath = file.getAbsolutePath();

        try {
            ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            if (pfd == null)
                return null;

            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);
            output = new FileOutputStream(filePath);
            int read;
            byte[] bytes = new byte[4096];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }

            input.close();
            output.close();
            fd = null;
            pfd.close();
            pfd = null;
            return new File(filePath);
        } catch (IOException ignored) {
            ignored.printStackTrace();
        }
        return null;
    }


    public static String getPath(final Context context, final Uri uri, Integer media_type) {
        // DocumentProvider
        if (DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }

            //DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);

                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));

                //return getDataColumn(context, uri, null, null);
                return getDataColumn(context, contentUri, null, null, uri);
            }

            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs, uri);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null, uri);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }


    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(@NonNull Context context, Uri uri, String selection,
                                       String[] selectionArgs, Uri filePathUri) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        FileInputStream input = null;
        FileOutputStream output = null;

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } catch (IllegalArgumentException e){
            e.printStackTrace();

            File file = new File(context.getCacheDir(), "tmp");
            String filePath = file.getAbsolutePath();

            try {
                ParcelFileDescriptor pfd = context.getContentResolver().openFileDescriptor(filePathUri, "r");
                if (pfd == null)
                    return null;

                FileDescriptor fd = pfd.getFileDescriptor();
                input = new FileInputStream(fd);
                output = new FileOutputStream(filePath);
                int read;
                byte[] bytes = new byte[4096];
                while ((read = input.read(bytes)) != -1) {
                    output.write(bytes, 0, read);
                }

                input.close();
                output.close();
                return new File(filePath).getAbsolutePath();
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        } finally{
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(@NonNull Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(@NonNull Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(@NonNull Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(@NonNull Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


}
