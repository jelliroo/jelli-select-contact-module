# jelli-select-contact-module
A convenience module that allows you to ask the user for single or multiple contacts grouped by email, phone or account type.

# Usage:

Add the library to your android project create the intent

```java
Intent intent = new Intent(this, ContactActivity.class);
```

Set the parameters

```java
intent.putExtra(ContactActivity.ARG_SINGULAR_TITLE, "Contact"); //Title that appears on context menu while multi-select
intent.putExtra(ContactActivity.ARG_PLURAL_TITLE, "Contacts"); //Title that appears on context menu while multi-select
intent.putExtra(ContactActivity.ARG_CONTEXT_MENU_RES, R.menu.menu_context); //Menu to be set on the context menu while multi-select
intent.putExtra(ContactActivity.ARG_MULTI_SELECT, true); //Is multi-select enabled
intent.putExtra(ContactActivity.ARG_SINGLE_SELECT, true); //Is single-select enabled
intent.putExtra(ContactActivity.ARG_SELECT_ALL_MENU_ID, R.id.action_select_all); //The action id of select all option on your menu xml
```

Start the activity for result

```java
startActivityForResult(intent, 0 /* requestCode */);
```

Override the onActivityResult to get the result

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if(resultCode == RESULT_OK){
        List<Contact> contactList = data.getParcelableArrayListExtra(ContactActivity.ARG_SELECTED_CONTACTS);
        int actionCode = data.getIntExtra(ContactActivity.ARG_ACTION_CODE, -1);
    }
}
```
