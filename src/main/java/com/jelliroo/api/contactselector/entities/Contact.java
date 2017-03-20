package com.jelliroo.api.contactselector.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 3/4/2017 by roger
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class Contact implements Parcelable {

    private String id;

    private String name;

    private String photoUri;

    private List<String> phoneNumbers;

    private List<String> emails;

    private String accountType;

    public Contact(String id, String name){
        this.id = id;
        this.name = name;
        phoneNumbers = new ArrayList<>();
        emails = new ArrayList<>();
    }

    protected Contact(Parcel in) {
        id = in.readString();
        name = in.readString();
        photoUri = in.readString();
        accountType = in.readString();
        if (in.readByte() == 0x01) {
            phoneNumbers = new ArrayList<>();
            in.readList(phoneNumbers, String.class.getClassLoader());
        } else {
            phoneNumbers = null;
        }
        if (in.readByte() == 0x01) {
            emails = new ArrayList<>();
            in.readList(emails, String.class.getClassLoader());
        } else {
            emails = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(photoUri);
        dest.writeString(accountType);
        if (phoneNumbers == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(phoneNumbers);
        }
        if (emails == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(emails);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Contact> CREATOR = new Parcelable.Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void addPhone(String phone){
        if(phone == null) return;
        if(phoneNumbers == null) phoneNumbers = new ArrayList<>();
        phoneNumbers.add(phone);
    }

    public void addEmail(String email){
        if(email == null) return;
        if(emails == null) emails = new ArrayList<>();
        emails.add(email);
    }
}