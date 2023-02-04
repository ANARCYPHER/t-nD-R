package com.rifcode.nearheart.Adapter;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rifcode.nearheart.R;


public class ChatViewHolders extends RecyclerView.ViewHolder{
    public TextView tvMessageInComing,tvTimeMessage;
    public LinearLayout lyKingItemChat,lyIncomming;
    public ImageView imgvSeenMessage,myimgvMessage;

    public ChatViewHolders(View itemView) {
        super(itemView);

//        tvMessageInComing = itemView.findViewById(R.id.tvMessageInComing);
//        tvMessageOutComing = itemView.findViewById(R.id.tvMessageOutComing);
        tvMessageInComing = itemView.findViewById(R.id.tvMessageInComing);
        lyKingItemChat = itemView.findViewById(R.id.lyKingItemChat);
        tvTimeMessage = itemView.findViewById(R.id.tvTimeMessage);
        lyIncomming = itemView.findViewById(R.id.lyIncomming);
        imgvSeenMessage = itemView.findViewById(R.id.imgvSeenMessage);
        myimgvMessage = itemView.findViewById(R.id.myimgvMessage);
//        lyMessageOutComing = itemView.findViewById(R.id.lyOutcomming);
    }

}
