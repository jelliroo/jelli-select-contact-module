package com.jelliroo.api.contactselector.vholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jelliroo.api.contactselector.R;

/**
 * Created on 3/4/2017 by roger
 */

public class ContactViewHolder extends RecyclerView.ViewHolder {

    public ImageView avatar;

    public TextView nameTextView, phoneTextView;

    public View container;

    public ContactViewHolder(final View itemView) {
        super(itemView);

        avatar = (ImageView) itemView.findViewById(R.id.avatar);
        nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
        phoneTextView = (TextView) itemView.findViewById(R.id.phone_number);
        container = itemView;
    }

}
