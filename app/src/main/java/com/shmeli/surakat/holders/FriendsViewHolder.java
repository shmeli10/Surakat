package com.shmeli.surakat.holders;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
 * Created by Serghei Ostrovschi on 10/26/17.
 */

public class FriendsViewHolder extends RecyclerView.ViewHolder {

    public View itemView;

    private TextView userNameTextView;
    private TextView userStatusTextView;

    private CircleImageView userAvatar;

    private String imageUrl = "";

    public FriendsViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;

        userNameTextView    = UiUtils.findView(itemView, R.id.userRowName);
        userStatusTextView  = UiUtils.findView(itemView, R.id.userRowStatus);
        userAvatar          = UiUtils.findView(itemView, R.id.userRowAvatar);
    }

    public void setName(String userName) {

        if(!TextUtils.isEmpty(userName))
            userNameTextView.setText(userName);
        else
            userNameTextView.setText(CONST.NO_NAME_TEXT);
    }

    public void setDate(String date) {

        if(!TextUtils.isEmpty(date))
            userStatusTextView.setText(date);
        else
            userStatusTextView.setText(CONST.NO_DATE_TEXT);
    }

    public void setAvatar(String imageUrl) {

        this.imageUrl = imageUrl;

        if(!imageUrl.equals(CONST.DEFAULT_VALUE)) {

            Picasso.with(itemView.getContext())
                    .load(imageUrl)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.default_avatar)
                    .into(  userAvatar,
                            loadImageCallback);
        }
    }

    Callback loadImageCallback = new Callback() {
        @Override
        public void onSuccess() { }

        @Override
        public void onError() {

            Picasso.with(itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.default_avatar)
                    .into(userAvatar);;
        }
    };
}
