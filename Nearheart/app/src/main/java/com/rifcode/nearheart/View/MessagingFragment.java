package com.rifcode.nearheart.View;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.rifcode.nearheart.Models.CardMatches;
import com.rifcode.nearheart.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MessagingFragment extends Fragment {

    private View mfview;
    private TextView tvAllMatches,tvAllLikes;
    private RecyclerView rcNewLikes;
    private LinearLayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter<CardMatches,usersNewLikesViewHolder> firebaseRecyclerAdapterNewLikes;
    private FirebaseUser mAuth;
    private String myuserID;
    private DatabaseReference dbConnections;
    private DatabaseReference dbUsers;
    private FirebaseRecyclerAdapter<CardMatches, usersNewMatchesViewHolder> firebaseRecyclerAdapterNewMatches;
    private LinearLayoutManager mLayoutManagercNewMatchesr;
    private RecyclerView rcNewMatches,rcMessages;
    private LinearLayout lyWaitLiking,lyStartLiking,lyMessagestitle;
    private LinearLayoutManager mLayoutManagercNewMessages;
    private FirebaseRecyclerAdapter<CardMatches, messagesHolderView> firebaseRecyclerAdapterMessages;
    private AdView adView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for mvv fragment
        mfview = inflater.inflate(R.layout.fragment_messaging, container, false);
        mAuth = FirebaseAuth.getInstance().getCurrentUser();
        myuserID = mAuth.getUid();
        dbConnections = FirebaseDatabase.getInstance().getReference().child("connections");
        dbUsers = FirebaseDatabase.getInstance().getReference().child("users");
        wedgets();

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(getActivity());

        // banner ads facebook
        adView = new AdView(getActivity(), getString(R.string.Banner_ads_facebook), AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = mfview.findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        // Request an ad
        adView.loadAd();

        return mfview;
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    private void wedgets() {
        // rcv new likes
        rcNewLikes = mfview.findViewById(R.id.rvNewLikes);
        rcNewLikes.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        rcNewLikes.setLayoutManager(mLayoutManager);

        lyWaitLiking = mfview.findViewById(R.id.lyWaitLiking);
        lyStartLiking = mfview.findViewById(R.id.lyStartLiking);
        lyMessagestitle = mfview.findViewById(R.id.lyMessagestitle);

        tvAllLikes = mfview.findViewById(R.id.tvAllLikes);
        tvAllMatches = mfview.findViewById(R.id.tvAllMatches);
        // rcv new matches
        rcNewMatches = mfview.findViewById(R.id.rvNewMatches);
        rcNewMatches.setHasFixedSize(true);

        mLayoutManagercNewMatchesr = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        rcNewMatches.setLayoutManager(mLayoutManagercNewMatchesr);

        //
        // rcv new messages
        rcMessages = mfview.findViewById(R.id.rcvMessages);
        rcMessages.setHasFixedSize(true);

        mLayoutManagercNewMessages = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rcMessages.setLayoutManager(mLayoutManagercNewMessages);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getUserVisibleHint()) {
            checkMyNewLikes();
            checkMyNewMatches();
        }
        frNewMessages();

    }


    private void checkMyNewMatches() {

        DatabaseReference dbConnectionMatches = dbConnections.child(myuserID).child("matches");
        dbConnectionMatches.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                if(count>20){
                    tvAllMatches.setVisibility(View.VISIBLE);
                }else{
                    tvAllMatches.setVisibility(View.GONE);
                }
                if (dataSnapshot.exists()){
                    rcNewMatches.setVisibility(View.VISIBLE);
                    lyStartLiking.setVisibility(View.GONE);
                    frNewMatches();
                }else{
                    rcNewMatches.setVisibility(View.GONE);
                    lyStartLiking.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


    }

    private void frNewLikes(){

        final Query qrMyNewLikes = dbConnections.child(myuserID).child("like").orderByChild("Time").limitToLast(20);
        firebaseRecyclerAdapterNewLikes = new FirebaseRecyclerAdapter<CardMatches, usersNewLikesViewHolder>(
                CardMatches.class,
                R.layout.layout_new_likes,
                usersNewLikesViewHolder.class,
                qrMyNewLikes
        ) {

            @Override
            protected void populateViewHolder(final usersNewLikesViewHolder viewHolder, final CardMatches model, int position) {

                final String listPostKey = getRef(position).getKey();
                dbUsers.child(listPostKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String thumb_picture = String.valueOf(dataSnapshot.child("photoProfile").getValue());
                        String username = String.valueOf(dataSnapshot.child("username").getValue());
                        String age = String.valueOf(dataSnapshot.child("age").getValue());
                        String gender = String.valueOf(dataSnapshot.child("gender").getValue());
                        viewHolder.getImageProfileNewLikes(getContext(),gender,thumb_picture);
                        viewHolder.tvusername.setText(username);
                        viewHolder.tvage.setText(age);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.imgvUserNewLikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileintent = new Intent(getContext(), ProfileUserActivity.class);
                        profileintent.putExtra("userIDvisited",listPostKey);
                        getContext().startActivity(profileintent);
                        Animatoo.animateZoom(getContext());
                    }
                });



            }


        };

        rcNewLikes.setAdapter(firebaseRecyclerAdapterNewLikes);

    }

    private void frNewMessages(){

        final Query qrNewMessages = FirebaseDatabase.getInstance().getReference().child("chat").child(myuserID).orderByChild("Time").limitToLast(10);
        firebaseRecyclerAdapterMessages = new FirebaseRecyclerAdapter<CardMatches, messagesHolderView>(
                CardMatches.class,
                R.layout.layout_messages,
                messagesHolderView.class,
                qrNewMessages
        ) {

            @Override
            protected void populateViewHolder(final messagesHolderView viewHolder, final CardMatches model, int position) {

                final String listPostKey = getRef(position).getKey();
                dbUsers.child(listPostKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String thumb_picture = String.valueOf(dataSnapshot.child("photoProfile").getValue());
                        String username = String.valueOf(dataSnapshot.child("username").getValue());
                        String age = String.valueOf(dataSnapshot.child("age").getValue());
                        String gender = String.valueOf(dataSnapshot.child("gender").getValue());
                        viewHolder.setImageProfileNewMessages(getContext(),gender,thumb_picture);
                        viewHolder.tvusername.setText(username);
                        viewHolder.tvage.setText(age);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.imgvUserrcNewmessages.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileintent = new Intent(getContext(), ProfileUserActivity.class);
                        profileintent.putExtra("userIDvisited",listPostKey);
                        getContext().startActivity(profileintent);
                        Animatoo.animateZoom(getContext());
                    }
                });

                viewHolder.lyMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent profileintent = new Intent(getContext(), ChatActivity.class);
                        profileintent.putExtra("userIDvisited",listPostKey);
                        getContext().startActivity(profileintent);
                        Animatoo.animateSlideLeft(getContext());
                    }
                });



            }


        };

        rcMessages.setAdapter(firebaseRecyclerAdapterMessages);

    }



    //    // when i visible frag
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed() && isVisibleToUser) {
            // Refresh your fragment here
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            Log.i("IsRefresh", "Yes");

        }
    }

    private void frNewMatches(){

        final Query qrMyNewMatches = dbConnections.child(myuserID).child("matches").orderByChild("Time").limitToLast(20);
        firebaseRecyclerAdapterNewMatches = new FirebaseRecyclerAdapter<CardMatches, usersNewMatchesViewHolder>(
                CardMatches.class,
                R.layout.layout_new_matches,
                usersNewMatchesViewHolder.class,
                qrMyNewMatches
        ) {

            @Override
            protected void populateViewHolder(final usersNewMatchesViewHolder viewHolder, final CardMatches model, int position) {

                final String listPostKey = getRef(position).getKey();

                dbUsers.child(listPostKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String thumb_picture = String.valueOf(dataSnapshot.child("photoProfile").getValue());
                        String username = String.valueOf(dataSnapshot.child("username").getValue());
                        String age = String.valueOf(dataSnapshot.child("age").getValue());
                        String gender = String.valueOf(dataSnapshot.child("gender").getValue());
                        viewHolder.setImageProfileNewMatches(getContext(),gender,thumb_picture);
                        viewHolder.tvusername.setText(username);
                        viewHolder.tvage.setText(age);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.imgvUserrcNewMatches.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileintent = new Intent(getContext(), ProfileUserActivity.class);
                        profileintent.putExtra("userIDvisited",listPostKey);
                        getContext().startActivity(profileintent);
                        Animatoo.animateZoom(getContext());
                    }
                });

            }
        };

        rcNewMatches.setAdapter(firebaseRecyclerAdapterNewMatches);

    }


    private void checkMyNewLikes(){
        DatabaseReference dbConnectionAlreadyLike = dbConnections.child(myuserID).child("like");
        dbConnectionAlreadyLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();

                String dvdf= dataSnapshot.getChildren().toString();

                if(count>20){
                    tvAllLikes.setVisibility(View.VISIBLE);
                }else{
                    tvAllLikes.setVisibility(View.GONE);
                }
                if (dataSnapshot.exists()){
                    rcNewLikes.setVisibility(View.VISIBLE);
                    lyWaitLiking.setVisibility(View.GONE);
                    frNewLikes();
                }else{
                    rcNewLikes.setVisibility(View.GONE);
                    lyWaitLiking.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



    public static class messagesHolderView extends RecyclerView.ViewHolder {

        View view;
        ImageView imgvUserrcNewmessages;
        TextView tvusername;
        TextView tvage;
        LinearLayout lyMessage;

        public messagesHolderView(View itemView) {
            super(itemView);

            view = itemView;
            imgvUserrcNewmessages = view.findViewById(R.id.imgvUserMessages);
            tvage = view.findViewById(R.id.tvAgeUserMessages);
            tvusername = view.findViewById(R.id.tvUsernameMessages);
            lyMessage = view.findViewById(R.id.lyMessage);

        }


        public void setImageProfileNewMessages(final Context ct,final String sex,final String imageUri){

            if(!imageUri.equals("none") && sex.equals("Man")) {

                //// Offline Capabilities: networkPolicy(NetworkPolicy.OFFLINE)
                Picasso.get().load(imageUri).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_gender_male_selected).into(imgvUserrcNewmessages, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(imageUri).placeholder(R.drawable.ic_gender_male_selected)
                                .into(imgvUserrcNewmessages);
                    }
                });

            }else
            if(!imageUri.equals("none") && sex.equals("Woman")){

                Picasso.get().load(imageUri).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_gender_female_selected).into(imgvUserrcNewmessages, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(imageUri).placeholder(R.drawable.ic_gender_female_selected)
                                .into(imgvUserrcNewmessages);
                    }
                });

            }if(imageUri.equals("none") && sex.equals("Woman")){

                Picasso.get().load(R.drawable.ic_gender_female_selected).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_gender_female_selected).into(imgvUserrcNewmessages, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(R.drawable.ic_gender_female_selected).placeholder(R.drawable.ic_gender_female_selected)
                                .into(imgvUserrcNewmessages);
                    }
                });

            }
            else
            if(imageUri.equals("none") && sex.equals("Man")){
                Picasso.get().load(R.drawable.ic_gender_male_selected).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_gender_male_selected).into(imgvUserrcNewmessages, new Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(R.drawable.ic_gender_male_selected).placeholder(R.drawable.ic_gender_male_selected)
                                .into(imgvUserrcNewmessages);
                    }
                });
            }

        }

    }


    public static class usersNewMatchesViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imgvUserrcNewMatches;
        TextView tvusername;
        TextView tvage;

        public usersNewMatchesViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            imgvUserrcNewMatches = view.findViewById(R.id.imgvUserNewMatches);
            tvage = view.findViewById(R.id.tvAgeUserNewMatches);
            tvusername = view.findViewById(R.id.tvUsernameNewMatches);
        }


        public void setImageProfileNewMatches(final Context ct,final String sex,final String imageUri){

            if(!imageUri.equals("none") && sex.equals("Man")) {

                //// Offline Capabilities: networkPolicy(NetworkPolicy.OFFLINE)
                Picasso.get().load(imageUri).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_gender_male_selected).into(imgvUserrcNewMatches, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(imageUri).placeholder(R.drawable.ic_gender_male_selected)
                                .into(imgvUserrcNewMatches);
                    }
                });

            }else
            if(!imageUri.equals("none") && sex.equals("Woman")){

                Picasso.get().load(imageUri).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_gender_female_selected).into(imgvUserrcNewMatches, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(imageUri).placeholder(R.drawable.ic_gender_female_selected)
                                .into(imgvUserrcNewMatches);
                    }
                });

            }if(imageUri.equals("none") && sex.equals("Woman")){

                Picasso.get().load(R.drawable.ic_gender_female_selected).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_gender_female_selected).into(imgvUserrcNewMatches, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(R.drawable.ic_gender_female_selected).placeholder(R.drawable.ic_gender_female_selected)
                                .into(imgvUserrcNewMatches);
                    }
                });

            }
            else
            if(imageUri.equals("none") && sex.equals("Man")){
                Picasso.get().load(R.drawable.ic_gender_male_selected).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_gender_male_selected).into(imgvUserrcNewMatches, new Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(R.drawable.ic_gender_male_selected).placeholder(R.drawable.ic_gender_male_selected)
                                .into(imgvUserrcNewMatches);
                    }
                });
            }

        }

    }


    public static class usersNewLikesViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imgvUserNewLikes;
        TextView tvusername;
        TextView tvage;

        public usersNewLikesViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            imgvUserNewLikes = view.findViewById(R.id.imgvUserNewLikes);
            tvage = view.findViewById(R.id.tvAgeUserNewLikes);
            tvusername = view.findViewById(R.id.tvUsernameNewLikes);
        }


        public void getImageProfileNewLikes(final Context ct,final String sex, final String imageUri){

            if(!imageUri.equals("none") && sex.equals("Man")) {

                //// Offline Capabilities: networkPolicy(NetworkPolicy.OFFLINE)
                Picasso.get().load(imageUri).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_gender_male_selected).into(imgvUserNewLikes, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(imageUri).placeholder(R.drawable.ic_gender_male_selected)
                                .into(imgvUserNewLikes);
                    }
                });

            }else
            if(!imageUri.equals("none") && sex.equals("Woman")){

                Picasso.get().load(imageUri).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_gender_female_selected).into(imgvUserNewLikes, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(imageUri).placeholder(R.drawable.ic_gender_female_selected)
                                .into(imgvUserNewLikes);
                    }
                });

            }if(imageUri.equals("none") && sex.equals("Woman")){

                Picasso.get().load(R.drawable.ic_gender_female_selected).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_gender_female_selected).into(imgvUserNewLikes, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(R.drawable.ic_gender_female_selected).placeholder(R.drawable.ic_gender_female_selected)
                                .into(imgvUserNewLikes);
                    }
                });

            }
            else
            if(imageUri.equals("none") && sex.equals("Man")){
                Picasso.get().load(R.drawable.ic_gender_male_selected).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.ic_gender_male_selected).into(imgvUserNewLikes, new Callback() {
                    @Override
                    public void onSuccess() {
                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(R.drawable.ic_gender_male_selected).placeholder(R.drawable.ic_gender_male_selected)
                                .into(imgvUserNewLikes);
                    }
                });
            }

        }
    }


}
