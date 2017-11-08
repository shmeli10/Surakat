package com.shmeli.surakat.holders;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.utils.UiUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Serghei Ostrovschi on 11/8/17.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public View             itemView;

    public TextView         messageText;
    public CircleImageView  messageAuthorAvatar;

    private String imageUrl = "";

    public MessageViewHolder(View itemView) {
        super(itemView);

        this.itemView       = itemView;

        messageText         = UiUtils.findView(itemView, R.id.messageRowMessageTextView);
        messageAuthorAvatar = UiUtils.findView(itemView, R.id.messageRowAvatar);
    }

    public void setMessageText(String text) {
        Log.e("LOG", "MessageViewHolder: setMessageText(): text= " +text);
        messageText.setText(text);
    }

    public void setAvatar(String imageUrl) {

        this.imageUrl = imageUrl;

        if(!imageUrl.equals(CONST.DEFAULT_VALUE)) {

            Picasso.with(itemView.getContext())
                    .load(imageUrl)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_avatar)
                    .into(  messageAuthorAvatar,
                            loadImageCallback);
        }

        /*Picasso.with(itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.default_avatar)
                .into(userAvatar);*/

    }

    Callback loadImageCallback = new Callback() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

            Picasso.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.default_avatar)
                    .into(messageAuthorAvatar);
        }
    };
}
