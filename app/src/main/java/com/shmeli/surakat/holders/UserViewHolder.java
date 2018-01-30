package com.shmeli.surakat.holders;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
import com.shmeli.surakat.utils.UiUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Serghei Ostrovschi on 10/19/17.
 */

public class UserViewHolder extends RecyclerView.ViewHolder {

    public View             itemView;

    private TextView        userNameTextView;
    private TextView        userStatusTextView;

    private CircleImageView userAvatar;

    private ImageView       userStatusImageView;

    private String          imageUrl = "";

    public UserViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;

        userNameTextView    = UiUtils.findView(itemView, R.id.userRowName);
        userStatusTextView  = UiUtils.findView(itemView, R.id.userRowStatus);
        userAvatar          = UiUtils.findView(itemView, R.id.userRowAvatar);
        userStatusImageView = UiUtils.findView(itemView, R.id.userRowStatusImageView);
    }

    public void setName(String userName) {

        if( (!TextUtils.isEmpty(userName)) &&
            (!userName.equals(CONST.DEFAULT_VALUE))) {

            userNameTextView.setText(userName);
        }
        else {
            userNameTextView.setText("");
        }
    }

    public void setStatus(String userStatus) {

        if( (!TextUtils.isEmpty(userStatus)) &&
            (!userStatus.equals(CONST.DEFAULT_VALUE))) {

            userStatusTextView.setText(userStatus);
        }
        else {

            userStatusTextView.setText("");
        }
    }

    public void setAvatar(String imageUrl) {

        if(imageUrl != null) {

            this.imageUrl = imageUrl;

            if (!imageUrl.equals(CONST.DEFAULT_VALUE)) {

                Picasso.with(itemView.getContext())
                        .load(imageUrl)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.default_avatar)
                        .into(userAvatar,
                                loadImageCallback);
            }
        }
        else {
            this.imageUrl = "";
        }

        /*Picasso.with(itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.default_avatar)
                .into(userAvatar);*/

    }

    public void setOnlineStatus(boolean isUserOnline) {

        if(isUserOnline) {
            userStatusImageView.setVisibility(View.VISIBLE);
        }
        else {
            userStatusImageView.setVisibility(View.INVISIBLE);
        }
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
                    .into(userAvatar);
        }
    };
}
