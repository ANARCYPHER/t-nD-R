package com.rifcode.nearheart.View;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.facebook.ads.Ad;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rifcode.nearheart.Models.ChatObject;
import com.rifcode.nearheart.Adapter.ChatViewHolders;
import com.rifcode.nearheart.R;
import com.rifcode.nearheart.Utils.ImagePickerr;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class ChatActivity extends AppCompatActivity {

    private static final int SEND_PIC = 552;
    private RecyclerView rcvMessages;
    DatabaseReference mDatabaseUser, dbrefMyChat,dbrefHischat,dbMessagingMy,dbMessagingHis;
    private String myuserid;
    private String userHisID;
    private EditText edtMessage;
    private ImageView imgBtnSend,imgvSendImageChat;
    private FirebaseRecyclerAdapter<ChatObject, ChatViewHolders> firebaseRecyclerAdapterChats;
    private LinearLayoutManager mChatLayoutManager;
    private DatabaseReference dbrefnotificationstate,dbrefnotification;
    private AdView adAdmobBannerChat;
    private StorageReference storageReference;
    private Random ra;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH);
    String currentdate = dateFormat.format(Calendar.getInstance().getTime());
    private int _PERMISSION_CODE_Exter=6666;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

         userHisID = getIntent().getStringExtra("userIDvisited");
         storageReference = FirebaseStorage.getInstance().getReference();
         myuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users");
        dbrefMyChat = FirebaseDatabase.getInstance().getReference().child("chat").child(myuserid).child(userHisID);
        dbrefHischat = FirebaseDatabase.getInstance().getReference().child("chat").child(userHisID).child(myuserid);
        dbMessagingMy = FirebaseDatabase.getInstance().getReference().child("messages").child(myuserid).child(userHisID);
        dbMessagingHis = FirebaseDatabase.getInstance().getReference().child("messages").child(userHisID).child(myuserid);

        rcvMessages =  findViewById(R.id.rclViewMessages);   rcvMessages.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);

        rcvMessages.setLayoutManager(mChatLayoutManager);

        edtMessage = findViewById(R.id.txtSendMessage);
        imgBtnSend = findViewById(R.id.imgBtnSend);
        imgvSendImageChat = findViewById(R.id.imgvSendImageChat);
        adAdmobBannerChat = findViewById(R.id.adAdmobBannerChat);

        imgBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String txt = edtMessage.getText().toString();
                if (!txt.isEmpty())
                    sendMessage(txt,"text");

            }
        });
        imgvSendImageChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        readExterStoragePermission();
            }
        });

        mDatabaseUser.child(userHisID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("username") && dataSnapshot.hasChild("age")){
                        getSupportActionBar().setTitle(dataSnapshot.child("username").getValue() + ", "
                                + dataSnapshot.child("age").getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // admob ads banner
        AdRequest adRequest = new AdRequest.Builder().build();
        adAdmobBannerChat.loadAd(adRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(ChatActivity.this);
        finish();
    }

    private void readExterStoragePermission(){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                   _PERMISSION_CODE_Exter);
        } else {
            //open gallery and chose image:
            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(galleryIntent,getString(R.string.select_gallery)), SEND_PIC);
        }
    }



//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_user_profile,menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    /// selected items menu:
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        int check = item.getItemId();
//        switch(check) {
//
//            case R.id.itmProfileChat:
//                Intent pp =  new Intent(ChatActivity.this,ProfileUserActivity.class);
//                pp.putExtra("userHisID",userHisID);
//                startActivity(pp);
//                Animatoo.animateSlideLeft(ChatActivity.this);
//                break;
//            default:
//        }
//        return super.onOptionsItemSelected(item);
//
//    }

    private void frChat(){

//        final Query qrMyNewLikes = dbConnections.child(myuserID).child("like").orderByChild("Time").limitToLast(20);
        firebaseRecyclerAdapterChats = new FirebaseRecyclerAdapter<ChatObject, ChatViewHolders>(
                ChatObject.class,
                R.layout.item_chat,
                ChatViewHolders.class,
                    dbMessagingMy
        ) {

            @Override
            protected void populateViewHolder(final ChatViewHolders viewHolder, final ChatObject model, int position) {

                final String listPostKey = getRef(position).getKey();
                seenmsg(listPostKey);
                dbMessagingMy.child(listPostKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                         final String message  = String.valueOf(dataSnapshot.child("message").getValue());
                        final String from = String.valueOf(dataSnapshot.child("from").getValue());
                        final String seen = String.valueOf(dataSnapshot.child("seen").getValue());
                        final String type = String.valueOf(dataSnapshot.child("type").getValue());
                        final long  msgTimeAgo = Long.parseLong(dataSnapshot.child("msgTimeAgo").getValue().toString());
                            getDataMessage(viewHolder,type,from,message,msgTimeAgo,seen);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }


        };

        rcvMessages.setAdapter(firebaseRecyclerAdapterChats);
        rcvMessages.getLayoutManager().scrollToPosition(rcvMessages.getAdapter().getItemCount());
        firebaseRecyclerAdapterChats.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int friendlyMessageCount = firebaseRecyclerAdapterChats.getItemCount();
                int lastVisiblePosition =
                        mChatLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) &&
                        lastVisiblePosition == (positionStart - 1))) {
                    rcvMessages.scrollToPosition(positionStart);
                }
            }
        });

    }

    private void getDataMessage(ChatViewHolders holder,String Type, String from, String message, long msgTimeAgo, String seen) {

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) holder.lyIncomming.getLayoutParams();
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) holder.tvTimeMessage.getLayoutParams();

        if(Locale.getDefault().getLanguage().equals("ar")){

            if (from.equals(myuserid)) {

                holder.lyKingItemChat.setGravity(Gravity.START);
                holder.lyIncomming.setBackgroundResource(R.drawable.bg_speech_bubble_outcoming);

                holder.tvTimeMessage.setTextColor(getResources().getColor(R.color.whiteLow));

                mlp.setMargins(0, 0, 12, 0);

                holder.tvMessageInComing.setTextColor(getResources().getColor(R.color.colorWhite));
                params.leftMargin = 60;
                params.rightMargin = 0;
                params.topMargin = 0;
                params.bottomMargin = 0;

                if(Type.equals("image"))
                {
                    holder.tvMessageInComing.setVisibility(View.GONE);
                    holder.myimgvMessage.setVisibility(View.VISIBLE);
                    Picasso.get().load(message).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.placeholder_image).into(holder.myimgvMessage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(message).placeholder(R.drawable.placeholder_image)
                                    .into(holder.myimgvMessage);
                        }
                    });

                }
                else{
                    holder.myimgvMessage.setVisibility(View.GONE);
                    holder.tvMessageInComing.setVisibility(View.VISIBLE);
                }

                holder.imgvSeenMessage.setVisibility(View.VISIBLE);
                if (seen.equals("true")) {
                    holder.imgvSeenMessage.setImageResource(R.drawable.ic_seen_on_white);
                } else {
                    holder.imgvSeenMessage.setImageResource(R.drawable.ic_seen_off_white);
                }

            } else {

                if(Type.equals("image"))
                {
                    holder.tvMessageInComing.setVisibility(View.GONE);
                    holder.myimgvMessage.setVisibility(View.VISIBLE);
                    Picasso.get().load(message).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.placeholder_image).into(holder.myimgvMessage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(message).placeholder(R.drawable.placeholder_image)
                                    .into(holder.myimgvMessage);
                        }
                    });

                }
                else{
                    holder.myimgvMessage.setVisibility(View.GONE);
                    holder.tvMessageInComing.setVisibility(View.VISIBLE);
                    holder.tvMessageInComing.setTextColor(getResources().getColor(R.color.colorWhite));
                }

                holder.imgvSeenMessage.setVisibility(View.GONE);
                holder.lyKingItemChat.setGravity(Gravity.END);
                holder.lyIncomming.setBackgroundResource(R.drawable.bg_speech_bubble_incoming);
                holder.tvTimeMessage.setTextColor(getResources().getColor(R.color.black_alpha_40));
                holder.tvMessageInComing.setTextColor(getResources().getColor(R.color.colorGrayPure));
                params.leftMargin = 0;
                params.rightMargin = 60;
                params.topMargin = 10;
                params.bottomMargin = 10;
                mlp.setMargins(20, 0, 0, 0);

            }
            holder.tvMessageInComing.setText(message);

            @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("HH:mm");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(msgTimeAgo);
            holder.tvTimeMessage.setText(formatter.format(calendar.getTime()));

        }else {


            if (from.equals(myuserid)) {

                holder.lyKingItemChat.setGravity(Gravity.END);
                holder.lyIncomming.setBackgroundResource(R.drawable.bg_speech_bubble_outcoming);

                holder.tvTimeMessage.setTextColor(getResources().getColor(R.color.whiteLow));

                mlp.setMargins(0, 0, 12, 0);

                if(Type.equals("image"))
                {
                    holder.tvMessageInComing.setVisibility(View.GONE);
                    holder.myimgvMessage.setVisibility(View.VISIBLE);
                    Picasso.get().load(message).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.placeholder_image).into(holder.myimgvMessage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(message).placeholder(R.drawable.placeholder_image)
                                    .into(holder.myimgvMessage);
                        }
                    });

                }
                else{
                    holder.myimgvMessage.setVisibility(View.GONE);
                    holder.tvMessageInComing.setVisibility(View.VISIBLE);
                    holder.tvMessageInComing.setTextColor(getResources().getColor(R.color.colorWhite));
                }

                params.leftMargin = 60;
                params.rightMargin = 0;
                params.topMargin = 0;
                params.bottomMargin = 0;

                holder.imgvSeenMessage.setVisibility(View.VISIBLE);
                if (seen.equals("true")) {
                    holder.imgvSeenMessage.setImageResource(R.drawable.ic_seen_on_white);
                } else {
                    holder.imgvSeenMessage.setImageResource(R.drawable.ic_seen_off_white);
                }

            } else {

                if(Type.equals("image"))
                {
                    holder.tvMessageInComing.setVisibility(View.GONE);
                    holder.myimgvMessage.setVisibility(View.VISIBLE);
                    Picasso.get().load(message).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.placeholder_image).into(holder.myimgvMessage, new Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(message).placeholder(R.drawable.placeholder_image)
                                    .into(holder.myimgvMessage);
                        }
                    });

                }
                else{
                    holder.myimgvMessage.setVisibility(View.GONE);
                    holder.tvMessageInComing.setVisibility(View.VISIBLE);
                    holder.tvMessageInComing.setTextColor(getResources().getColor(R.color.colorWhite));
                }

                holder.imgvSeenMessage.setVisibility(View.GONE);
                holder.lyKingItemChat.setGravity(Gravity.START);
                holder.lyIncomming.setBackgroundResource(R.drawable.bg_speech_bubble_incoming);
                holder.tvTimeMessage.setTextColor(getResources().getColor(R.color.black_alpha_40));
                holder.tvMessageInComing.setTextColor(getResources().getColor(R.color.colorGrayPure));
                params.leftMargin = 0;
                params.rightMargin = 60;
                params.topMargin = 10;
                params.bottomMargin = 10;
                mlp.setMargins(0, 0, 0, 0);

            }
            holder.tvMessageInComing.setText(message);

            @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("HH:mm");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(msgTimeAgo);
            holder.tvTimeMessage.setText(formatter.format(calendar.getTime()));
        }

    }


    private void seenmsg(String idmessage){
        DatabaseReference dbrefseen = dbMessagingHis;
        dbrefseen.child(idmessage).child("seen").setValue("true");
    }


    private void sendMessage(String message, String type){

        DatabaseReference dbrefMyChatsend = dbMessagingMy.push();
        String keypuch = dbrefMyChatsend.getKey();
        DatabaseReference dbrefHIschattsend = dbMessagingHis.child(keypuch);

        // send to my messages
        dbrefMyChatsend.child("from").setValue(myuserid);
        dbrefMyChatsend.child("message").setValue(message);
        dbrefMyChatsend.child("msgTimeAgo").setValue(ServerValue.TIMESTAMP);
        dbrefMyChatsend.child("type").setValue(type);
        dbrefMyChatsend.child("seen").setValue("false");

        // send to user chat
        dbrefHIschattsend.child("from").setValue(myuserid);
        dbrefHIschattsend.child("message").setValue(message);
        dbrefHIschattsend.child("msgTimeAgo").setValue(ServerValue.TIMESTAMP);
        dbrefHIschattsend.child("type").setValue(type);


        rcvMessages.smoothScrollToPosition(rcvMessages.getAdapter().getItemCount());

        firebaseRecyclerAdapterChats.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseRecyclerAdapterChats.getItemCount();
                int lastVisiblePosition =
                        mChatLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 || (positionStart >= (friendlyMessageCount - 1) &&
                        lastVisiblePosition == (positionStart - 1))) {
                    rcvMessages.scrollToPosition(positionStart);
                }
            }
        });


        dbrefHischat.child("Time").setValue(-1*System.currentTimeMillis());
        dbrefMyChat.child("Time").setValue(-1*System.currentTimeMillis());



        DatabaseReference dbrefreviewsmsg = FirebaseDatabase.getInstance().getReference().child("messages_reviews").child(keypuch);
        dbrefreviewsmsg.child("from").setValue(myuserid);
        dbrefreviewsmsg.child("to").setValue(userHisID);
        dbrefreviewsmsg.child("message").setValue(message);
        dbrefreviewsmsg.child("time").setValue(-1*System.currentTimeMillis());

        // for notification messages
//        getrefNotificationState().child(userHisID)
//                .child(myuserid).child("state").setValue("message");
//
//
//        DatabaseReference notifRef = getdbrefnotification().child(userHisID).push();
//        notifRef.child("From").setValue(myuserid);




        edtMessage.setText("");
    }

//    public  DatabaseReference getdbrefnotification(){
//        dbrefnotification = FirebaseDatabase.getInstance().getReference().child("Notifications");
//        return dbrefnotification;
//    }
//
//    public  DatabaseReference getrefNotificationState(){
//        dbrefnotificationstate = FirebaseDatabase.getInstance().getReference().child("notifications_state");
//        return dbrefnotificationstate;
//    }


    @Override
    protected void onStart() {
        super.onStart();
        frChat();
    }

    private String newRandom(){
        int rand = random();
        String replaceDate1 = currentdate.replace(':','_');
        String replaceDate2 = replaceDate1.replace('-','_');
        String replaceDate3 = replaceDate2.replace(' ','_');
        return replaceDate3+"_"+ rand;
    }

    private int random(){
        ra  = new Random();
        int lowerBound = 0;
        int upperBound = 9999999;
        int resultRandom = ra.nextInt(upperBound-lowerBound) + lowerBound;
        return  resultRandom;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SEND_PIC && resultCode == RESULT_OK) {


            Bitmap bmp = ImagePickerr.getImageFromResult(this, resultCode, data);


            final StorageReference filePath = storageReference.child("messages_images").child(myuserid).child(userHisID)
                    .child(newRandom()+".jpg");


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 75, stream);
            byte[] datad = stream.toByteArray();

            UploadTask uploadTask = filePath.putBytes(datad);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        if (downloadUri != null) {

                            String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                            sendMessage(photoStringLink,"image");
                        }

                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });



        }

    }

}
