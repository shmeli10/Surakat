package com.shmeli.surakat.holders;


import android.support.v7.widget.RecyclerView;

import android.text.TextUtils;

import android.util.TypedValue;

import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shmeli.surakat.R;
import com.shmeli.surakat.utils.UiUtils;

/**
 * Created by Serghei Ostrovschi on 11/8/17.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {

    public View             itemView;

    private RelativeLayout  messageContainer;

    public TextView         messageAuthorName;
    public TextView         messageText;

    private String imageUrl = "";

    public MessageViewHolder(View itemView) {
        super(itemView);

        this.itemView       = itemView;

        messageContainer    = UiUtils.findView(itemView,    R.id.messageContainer);

        messageAuthorName   = UiUtils.findView(itemView,    R.id.messageAuthorName);
        messageText         = UiUtils.findView(itemView,    R.id.messageRowMessageTextView);
    }

    public void setMessageAuthorName(String authorName) {
        //Log.e("LOG", "MessageViewHolder: setMessageAuthorName(): authorName= " +authorName);

        if(!TextUtils.isEmpty(authorName)) {
            messageAuthorName.setText(authorName);
            messageAuthorName.setVisibility(View.VISIBLE);
        }
        else {
            messageAuthorName.setVisibility(View.GONE);
        }
    }

    public void setMessageText(String text) {
        //Log.e("LOG", "MessageViewHolder: setMessageText(): text= " +text);
        messageText.setText(text);
    }

    public void setMargin(int   marginLeft,
                          int   marginRight) {

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(   ViewGroup.LayoutParams.MATCH_PARENT,
                                                                        ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(  getPixelsFromDp(marginLeft),
                        0,
                        getPixelsFromDp(marginRight),
                        0);

        messageContainer.setLayoutParams(lp);
    }

    /**
     * Convert dp to pixels
     * @param dp to convert
     * @return value of passed dp in pixels
     */
    private int getPixelsFromDp(int dp) {
        return (int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP,
                                                dp,
                                                itemView.getContext().getResources().getDisplayMetrics());
    }
}
