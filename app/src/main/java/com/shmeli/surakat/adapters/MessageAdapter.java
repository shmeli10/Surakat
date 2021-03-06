package com.shmeli.surakat.adapters;

import android.content.Context;

import android.support.v7.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import com.shmeli.surakat.R;
import com.shmeli.surakat.data.CONST;
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

    private String          recipientName = "";

    public MessageAdapter(  Context         context,
                            List<Message>   messagesList,
                            String          recipientName) {

        this.context        = context;
        this.messagesList   = messagesList;

        if(!TextUtils.isEmpty(recipientName)) {
            this.recipientName = recipientName;
        }


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
        int darkTextColorResId  = context.getResources().getColor(R.color.colorPrimaryDark);

        Message message         = messagesList.get(position);

        String currentUserId    = fbAuth.getCurrentUser().getUid();
        String messageAuthorId  = message.getMessageAuthorId();

        // ----------------------------- SORT ORDER -------------------------------- //

        Message previousMessage = null;

        int previousMessagePosition = (position - 1);

        if(previousMessagePosition >= 0) {
            previousMessage = messagesList.get(previousMessagePosition);
        }

        // current user is the author of the message
        if(messageAuthorId.equals(currentUserId)) {

            viewHolder.setMessageAuthorName("");

            viewHolder.messageText.setBackgroundResource(R.drawable.message_row_sender_background);
            viewHolder.messageText.setTextColor(darkTextColorResId);

            viewHolder.setMargin(   0,
                                    CONST.MESSAGE_MARGIN_PX);
        }
        // current user is not the author of the message
        else {

            if( (previousMessage != null) &&
                (previousMessage.getMessageAuthorId().equals(messageAuthorId))) {

                viewHolder.setMessageAuthorName("");
            }
            else {
                viewHolder.setMessageAuthorName(recipientName);
            }

            viewHolder.messageText.setBackgroundResource(R.drawable.message_row_recipient_background);
            viewHolder.messageText.setTextColor(whiteColorResId);

            viewHolder.setMargin(   CONST.MESSAGE_MARGIN_PX,
                                    0);
        }

        viewHolder.setMessageText(message.getMessageText());
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
