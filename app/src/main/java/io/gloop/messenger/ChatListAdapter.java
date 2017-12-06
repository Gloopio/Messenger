package io.gloop.messenger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import br.com.instachat.emojilibrary.model.layout.EmojiTextView;
import io.gloop.GloopList;
import io.gloop.GloopOnChangeListener;
import io.gloop.messenger.model.ChatMessage;
import io.gloop.messenger.model.Status;
import io.gloop.messenger.model.UserInfo;

public class ChatListAdapter extends BaseAdapter {

    private GloopList<ChatMessage> chatMessages;
    private Context context;
    private UserInfo ownerUserInfo;
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.US);

    ChatListAdapter(GloopList<ChatMessage> chatMessages, Context context, UserInfo ownerUserInfo) {
        this.chatMessages = chatMessages;
        this.context = context;
        this.ownerUserInfo = ownerUserInfo;

        this.chatMessages.addOnChangeListener(new GloopOnChangeListener() {
            @Override
            public void onChange() {
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getCount() {
        return chatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        ChatMessage message = chatMessages.get(position);
        ViewHolder1 holder1;
        ViewHolder2 holder2;

        if (!message.getAuthor().equals(ownerUserInfo.getPhone())) {
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.chat_user1_item, null, false);
                holder1 = new ViewHolder1();


                holder1.messageTextView = (EmojiTextView) v.findViewById(R.id.textview_message);
                holder1.timeTextView = (TextView) v.findViewById(R.id.textview_time);

                v.setTag(holder1);
            } else {
                v = convertView;
                holder1 = (ViewHolder1) v.getTag();

            }

            holder1.messageTextView.setText(message.getMessageText());
            holder1.messageTextView.setUseSystemDefault(Boolean.FALSE);

            holder1.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.getMessageTime()));

        } else {

            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.chat_user2_item, null, false);

                holder2 = new ViewHolder2();


                holder2.messageTextView = (EmojiTextView) v.findViewById(R.id.textview_message);
                holder2.timeTextView = (TextView) v.findViewById(R.id.textview_time);
                holder2.messageStatus = (ImageView) v.findViewById(R.id.user_reply_status);
                v.setTag(holder2);

            } else {
                v = convertView;
                holder2 = (ViewHolder2) v.getTag();

            }

            holder2.messageTextView.setText(message.getMessageText());
            holder2.messageTextView.setUseSystemDefault(Boolean.FALSE);


            holder2.timeTextView.setText(SIMPLE_DATE_FORMAT.format(message.getMessageTime()));

            if (message.getMessageStatus() == Status.DELIVERED) {
                holder2.messageStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.message_got_receipt_from_target));
            } else if (message.getMessageStatus() == Status.SENT) {
                holder2.messageStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.message_got_receipt_from_server));

            }
        }

        return v;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        if (!message.getAuthor().equals(ownerUserInfo.getPhone()))
            return 1;
        else
            return 0;
    }

    private class ViewHolder1 {
        EmojiTextView messageTextView;
        TextView timeTextView;


    }

    private class ViewHolder2 {
        ImageView messageStatus;
        EmojiTextView messageTextView;
        TextView timeTextView;

    }
}
