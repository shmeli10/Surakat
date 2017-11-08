package com.shmeli.surakat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.shmeli.surakat.R;
import com.shmeli.surakat.holders.MessageViewHolder;
import com.shmeli.surakat.model.Message;

import java.util.List;

/**
 * Created by Serghei Ostrovschi on 11/8/17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder>{

    private Context         context;

    private List<Message>   messagesList;

    private FirebaseAuth    fbAuth;

    public MessageAdapter(Context       context,
                          List<Message> messagesList) {

        this.context        = context;
        this.messagesList   = messagesList;

        fbAuth = FirebaseAuth.getInstance();
    }


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup   parent,
                                                int         viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(   R.layout.message_row,
                                                                        parent,
                                                                        false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder  viewHolder,
                                 int                position) {

        int whiteColorResId     = context.getResources().getColor(R.color.white);
        int primaryColorResId   = context.getResources().getColor(R.color.colorPrimary);

        Message message = messagesList.get(position);

        String currentUserId    = fbAuth.getCurrentUser().getUid();
        String messageAuthorId  = message.getMessageAuthorId();

        // current user is the author of the message
        if(messageAuthorId.equals(currentUserId)) {

            //viewHolder.messageText.setBackgroundColor(whiteColorResId);
            viewHolder.messageText.setBackgroundResource(R.drawable.message_row_sender_background);
            viewHolder.messageText.setTextColor(primaryColorResId);
        }
        // current user is not the author of the message
        else {

            viewHolder.messageText.setBackgroundResource(R.drawable.message_row_recipient_background);
            viewHolder.messageText.setTextColor(whiteColorResId);
        }


        viewHolder.setMessageText(message.getMessageText());
//        viewHolder.messageText.setText(message.getMessageText());

//        viewHolder.setAvatar();
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
