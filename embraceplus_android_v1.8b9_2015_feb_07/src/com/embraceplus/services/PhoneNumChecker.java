package com.embraceplus.services;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

public class PhoneNumChecker {
	private static PhoneNumChecker util = new PhoneNumChecker();

	private PhoneNumChecker() {

	}

	public static PhoneNumChecker getInstant() {
		return util;
	}

	public boolean isUnknownPhone(String phoneNumber, Context context) {
		boolean isUnKnowPhone = false;

		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = null;

		String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER };

		cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[] { phoneNumber }, "");
		if (cursor.getCount() == 0) {
			// myTextView1.setText("unknown Number:" + phoneNumber);
			isUnKnowPhone = true;
		} else if (cursor.getCount() > 0) {
			// cursor.moveToFirst();
			// String name = cursor.getString(1);
			// myTextView1.setText(name + ":" + incomingNumber);
			isUnKnowPhone = false;
		}

		return isUnKnowPhone;
	}

	public String getUserName(String phoneNumber, Context context) {

		ContentResolver contentResolver = context.getContentResolver();
		Cursor cursor = null;

		String[] projection = new String[] { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER };

		cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[] { phoneNumber }, "");
		String userName = null;
		while (cursor.moveToNext()) {
			userName = cursor.getString(1);
		}

		return userName;
	}
}
