package com.jelliroo.api.contactselector.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;

import com.jelliroo.api.contactselector.adapters.ContactAdapter;
import com.jelliroo.api.contactselector.R;
import com.jelliroo.api.contactselector.entities.Contact;
import com.jelliroo.api.contextmenu.activities.SupportActionModeActivity;

import java.util.ArrayList;
import java.util.List;

public class ContactActivity extends SupportActionModeActivity{

    public static final int MODE_NONE = 1;
    public static final int MODE_PHONE = 2;
    public static final int MODE_EMAIL = 3;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    public static final String ARG_PLURAL_TITLE = "ARG_PLURAL_TITLE",
                            ARG_SINGULAR_TITLE = "ARG_SINGULAR_TITLE",
                            ARG_CONTEXT_MENU_RES = "ARG_CONTEXT_MENU_RES",
                            ARG_SELECTED_CONTACTS = "ARG_SELECTED_CONTACTS",
                            ARG_ACTION_CODE = "ARG_ACTION_CODE",
                            ARG_MULTI_SELECT = "ARG_MULTI_SELECT",
                            ARG_SINGLE_SELECT = "ARG_SINGLE_SELECT",
                            ARG_SELECT_ALL_MENU_ID = "ARG_SELECT_ALL_MENU_ID";


    ContactAdapter contactAdapter;

    int selectAllMenuId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        loadExtras();
        init();
    }

    public void init(){
        setRecyclerView(R.id.contact_recycler);
        contactAdapter = new ContactAdapter(this);
        getRecyclerView().setAdapter(contactAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        getRecyclerView().setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                getRecyclerView().getContext(),
                linearLayoutManager.getOrientation());
        getRecyclerView().addItemDecoration(dividerItemDecoration);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            contactAdapter.initializeContacts(this, 91, MODE_EMAIL);
        }
    }

    public void loadExtras(){
        setSingleSelectEnabled(getIntent().getBooleanExtra(ARG_SINGLE_SELECT, true));
        setMultiSelectEnabled(getIntent().getBooleanExtra(ARG_MULTI_SELECT, true));
        selectAllMenuId = getIntent().getIntExtra(ARG_SELECT_ALL_MENU_ID, selectAllMenuId);
        String singularTitle, pluralTitle;

        int menuId;

        singularTitle = getIntent().getStringExtra(ARG_SINGULAR_TITLE);
        if(singularTitle == null) singularTitle = "";
        pluralTitle = getIntent().getStringExtra(ARG_PLURAL_TITLE);
        if(pluralTitle == null) pluralTitle = "";
        menuId = getIntent().getIntExtra(ARG_CONTEXT_MENU_RES, R.menu.context_menu);

        setPluralItemTitle(pluralTitle);
        setSingularItemTitle(singularTitle);
        setMenuId(menuId);
    }

    @Override
    public ContactAdapter getAdapter() {
        return contactAdapter;
    }

    @Override
    public void onContextMenuClosed() {

    }

    @Override
    public void onContextMenuOpened() {

    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean onContextMenuItemSelected(List selectedItems, int actionCode) {
        if(actionCode == selectAllMenuId){
            toggleSelectAll();
        } else {
            Intent data = new Intent();
            data.putParcelableArrayListExtra(ARG_SELECTED_CONTACTS, (ArrayList<? extends Parcelable>) selectedItems);
            data.putExtra(ARG_ACTION_CODE, actionCode);
            setResult(RESULT_OK, data);
            finish();
        }
        return false;
    }

    @Override
    public void onItemSelected(Object object) {
        if(object != null && object instanceof Contact) {
            Intent data = new Intent();
            ArrayList<Contact> contacts = new ArrayList<>();
            contacts.add((Contact) object);
            data.putParcelableArrayListExtra(ARG_SELECTED_CONTACTS, contacts);
            data.putExtra(ARG_ACTION_CODE, -1);
            setResult(RESULT_OK, data);
            finish();
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    contactAdapter.initializeContacts(this, 91, MODE_EMAIL);
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }


}
