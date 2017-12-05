package io.gloop.messenger.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;

import io.gloop.Gloop;
import io.gloop.GloopList;
import io.gloop.messenger.model.UserInfo;
import io.gloop.query.GloopQuery;

/**
 * Created by Alex Untertrifaller on 04.12.17.
 */

public class ContactUtil {


    public static GloopList<UserInfo> syncContacts(Activity activity) {

        List<String> phoneNumbers = getPhoneNumbers(activity);

        if (phoneNumbers != null && phoneNumbers.size() > 0) {

            GloopQuery<UserInfo> where = Gloop.all(UserInfo.class).where();

            int i = 0;
            for (String phoneNumber : phoneNumbers) {
                phoneNumber = phoneNumber.replace(" ", "").replace("-","");
                if (i < phoneNumbers.size() -1) {
                    where = where.equalsTo("phone", phoneNumber).or();
                    i++;
                } else
                    return where.equalsTo("phone", phoneNumber).all();
            }
        }
        return null;
    }


    public static List<String> getPhoneNumbers(Activity activity) {
        ContentResolver cr = activity.getContentResolver(); //Activity/Application android.content.Context
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor.moveToFirst()) {
            ArrayList<String> alContacts = new ArrayList<String>();
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        alContacts.add(contactNumber);
                        break;
                    }
                    pCur.close();
                }

            } while (cursor.moveToNext());

            return alContacts;

        }
        return null;
    }
}
