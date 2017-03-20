package com.jelliroo.api.contactselector.adapters;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jelliroo.api.contactselector.R;
import com.jelliroo.api.contactselector.entities.Contact;
import com.jelliroo.api.contactselector.vholders.ContactViewHolder;
import com.jelliroo.api.contextmenu.adapters.RecyclerViewAdapter;
import com.squareup.picasso.Picasso;

import static com.jelliroo.api.contactselector.activities.ContactActivity.MODE_EMAIL;
import static com.jelliroo.api.contactselector.activities.ContactActivity.MODE_NONE;
import static com.jelliroo.api.contactselector.activities.ContactActivity.MODE_PHONE;

/**
 * Created on 3/20/2017 by roger
 */

@SuppressWarnings("unused")
public class ContactAdapter extends RecyclerViewAdapter<ContactViewHolder, String, Contact> {

    private int mode = MODE_EMAIL;
    private int countryCode = 91;
    private Context context;


    public ContactAdapter(Context context){
        this.context = context;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, null);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        final Contact contact = getItemAt(position);
        holder.nameTextView.setText(contact.getName());
        if (contact.getPhotoUri() == null) {
            holder.avatar.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_person_black_24dp, null));
        } else {
            Picasso
                    .with(context)
                    .load(contact.getPhotoUri())
                    .into(holder.avatar);
        }
        if(mode == MODE_NONE)
            holder.phoneTextView.setText(contact.getAccountType());
        else if(mode == MODE_PHONE){
            holder.phoneTextView.setText(contact.getPhoneNumbers().get(0));
        } else {
            holder.phoneTextView.setText(contact.getEmails().get(0));
        }

        if(isItemAtPositionSelected(position)){
            holder.container.setBackgroundColor(ContextCompat.getColor(context, R.color.selected));
        } else {
            int[] attrs = new int[] { android.R.attr.selectableItemBackground };
            TypedArray ta = context.obtainStyledAttributes(attrs);
            Drawable drawableFromTheme = ta.getDrawable(0);
            ta.recycle();
            holder.container.setBackground(drawableFromTheme);
        }
    }

    public void initializeContacts(Context context, int countryCode, int mode){
        this.countryCode = countryCode;
        this.mode = mode;
        if(mode == MODE_NONE)
            loadAll(context);
        else if(mode == MODE_PHONE)
            loadAllPhones(context);
        else
            loadAllEmails(context);
        notifyDataSetChanged();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public void setCountryCode(int countryCode) {
        this.countryCode = countryCode;
    }

    private void loadAll(Context context){
        removeAll();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
        if(contactCursor == null) return;
        if (contactCursor.getCount() > 0) {
            while (contactCursor.moveToNext()) {
                String id = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
                String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String accountType = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_NAME));
                String photoUri = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                Contact contact = new Contact(id, name);
                contact.setPhotoUri(photoUri);
                contact.setAccountType(accountType);
                addItem(id, contact);
            }
        }
        contactCursor.close();
    }

    private void loadAllPhones(Context context){
        removeAll();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
        if(contactCursor == null) return;
        if (contactCursor.getCount() > 0) {
            Contact contact;
            while (contactCursor.moveToNext()) {
                String id = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (contactCursor.getInt(contactCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                            new String[]{id}, null);

                    if(pCur == null) continue;

                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER)).trim().replaceAll(" ", "").replaceAll("[^a-zA-Z0-9]", "");
                        phoneNo = formatPhoneNumber(phoneNo);
                        String photoUri = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                        contact = new Contact(id, name);
                        contact.addPhone(phoneNo);
                        contact.setPhotoUri(photoUri);
                        addItem(phoneNo, contact);
                    }
                    pCur.close();
                }
            }
        }
        contactCursor.close();
    }

    private String formatPhoneNumber(String phoneNo){
        while(phoneNo.length() > 0 && phoneNo.charAt(0) == '0'){
            phoneNo = phoneNo.replaceFirst("0", "");
        }

        String subscriberNumber, nationalDestination, cc;

        if(phoneNo.length() > 6){
            subscriberNumber = phoneNo.substring(phoneNo.length() - 6, phoneNo.length());
            if(phoneNo.length() >= 10){
                nationalDestination = phoneNo.substring(phoneNo.length() - 10, phoneNo.length() - 6);
                if(phoneNo.length() > 10){
                    cc = phoneNo.substring(0, phoneNo.length() - 10);
                } else cc = Integer.toString(countryCode);
            } else {
                nationalDestination = phoneNo.substring(0, phoneNo.length() - 6);
                cc = Integer.toString(countryCode);
            }

            phoneNo = cc + " " + nationalDestination + " " + subscriberNumber;
        } else {
            phoneNo = countryCode + " " + phoneNo;
        }
        return phoneNo;
    }

    private void loadAllEmails(Context context){
        removeAll();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactCursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC");
        if(contactCursor == null) return;
        Contact contact;
        if (contactCursor.getCount() > 0) {
            while (contactCursor.moveToNext()) {
                String id = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Cursor pCur = contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID +" = ?",
                        new String[]{id}, null);
                if(pCur == null) return;
                while (pCur.moveToNext()) {

                    String email = pCur.getString(pCur.getColumnIndex(
                            ContactsContract.CommonDataKinds.Email.DATA
                    ));
                    String photoUri = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                    contact = new Contact(id, name);
                    contact.addEmail(email);
                    contact.setPhotoUri(photoUri);
                    addItem(email, contact);
                }
                pCur.close();
            }
        }
        contactCursor.close();
    }
}
