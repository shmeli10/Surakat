package com.shmeli.surakat.holders;

import android.support.v7.widget.RecyclerView;
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
 * Created by Serghei Ostrovschi on 10/19/17.
 */

public class UserViewHolder extends RecyclerView.ViewHolder {

    public View itemView;

    TextView userNameTextView;
    TextView userStatusTextView;

    CircleImageView userAvatar;

    private String imageUrl = "";

    public UserViewHolder(View itemView) {
        super(itemView);

        this.itemView = itemView;

        userNameTextView    = UiUtils.findView(itemView, R.id.userRowName);
        userStatusTextView  = UiUtils.findView(itemView, R.id.userRowStatus);
        userAvatar          = UiUtils.findView(itemView, R.id.userRowAvatar);
    }

    public void setName(String userName) {

        userNameTextView.setText(userName);
    }

    public void setStatus(String userStatus) {

        userStatusTextView.setText(userStatus);
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
                    .into(userAvatar);;
        }
    };
}
