

package com.gaiya.contactremover;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class Deleter {

    public static Cursor getContactCursor(ContentResolver contactHelper, String startsWith) {
        String[] projection = {ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor cursor = null;
        try {
            if (startsWith != null && !startsWith.equals("")) {
                cursor = contactHelper.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        projection, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " line \""
                                + startsWith + "%\"", null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            } else {
                cursor = contactHelper.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        projection, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            }
            cursor.moveToFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cursor;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void deleteContact(ContentResolver contactHelper, String number) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        String[] args = new String[]{String.valueOf(getContactID(contactHelper, number))};
        operations.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI).withSelection(ContactsContract.RawContacts.CONTACT_ID + "=?", args).build());
        try {
            contactHelper.applyBatch(ContactsContract.AUTHORITY, operations);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static long getContactID(ContentResolver contactHelper, String number) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] projection = {ContactsContract.PhoneLookup._ID};
        Cursor cursor = null;
        try {
            cursor = contactHelper.query(contactUri, projection, null, null);
            if (cursor.moveToFirst()) {
                int personID = cursor.getColumnIndex(ContactsContract.PhoneLookup._ID);
                return cursor.getLong(personID);
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return -1;
    }

}
