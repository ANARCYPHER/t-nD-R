package com.rifcode.nearheart.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rifcode.nearheart.Models.CardMatches;
import com.rifcode.nearheart.View.ProfileUserActivity;
import com.rifcode.nearheart.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.rifcode.nearheart.View.MatchsFragment.flingContainer;

public class ArrayAdapterMatches extends ArrayAdapter<CardMatches> {

    private ImageView imgvProfilz;
    private ImageView imgvLike;
    private ImageView imgvDislike;
    private DatabaseReference dbConnections;
    private String myuserID;
    private FirebaseAuth mAuth;
    private ImageView imgvHasPhotosMatches;
    private DatabaseReference dbusers;
    private Context mContext;


    public ArrayAdapterMatches(Context context, int resourceId, List<CardMatches> items){
        super(context, resourceId, items);
        mContext=context;
    }
    public View getView(final int position, View convertView, ViewGroup parent){
        final CardMatches card_item = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_matches, parent, false);
        }

        mAuth = FirebaseAuth.getInstance();
        myuserID = mAuth.getUid();
        dbConnections = FirebaseDatabase.getInstance().getReference().child("connections");
        dbusers = FirebaseDatabase.getInstance().getReference().child("users");



        TextView username =  convertView.findViewById(R.id.tvUsernameMatchs);
        final RoundedImageView image =  convertView.findViewById(R.id.imgvUserMatches);
        TextView age =  convertView.findViewById(R.id.tvAgeMatchs);
        TextView tvGoma =  convertView.findViewById(R.id.tvGoma);
        TextView tvCountImagesUser =  convertView.findViewById(R.id.tvCountImagesUser);

         imgvProfilz =  convertView.findViewById(R.id.imgvProfileMatches);
         imgvLike =  convertView.findViewById(R.id.imgvLikeMatches);
         imgvDislike =  convertView.findViewById(R.id.imgvDislikeMatches);
        imgvHasPhotosMatches =  convertView.findViewById(R.id.imgvHasPhotosMatches);

        username.setText(card_item.getUsername());
        age.setText(card_item.getAge());

        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent profileintent = new Intent(getContext(), ProfileUserActivity.class);
                profileintent.putExtra("userIDvisited",card_item.getUserId());
                getContext().startActivity(profileintent);
                Animatoo.animateZoom(getContext());



            }
        });

        imgvLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbConnections.child(card_item.getUserId()).child("like").child(myuserID).child("Time").setValue(-1*System.currentTimeMillis());
                flingContainer.getTopCardListener().selectRight();

            }
        });

        imgvDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbConnections.child(card_item.getUserId()).child("dislike").child(myuserID).child("Time").setValue(-1*System.currentTimeMillis());
                flingContainer.getTopCardListener().selectLeft();
            }
        });

        imgvProfilz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent profileintent = new Intent(getContext(), ProfileUserActivity.class);
                profileintent.putExtra("userIDvisited",card_item.getUserId());
                getContext().startActivity(profileintent);
                Animatoo.animateZoom(getContext());


            }
        });

        getImageProfile(convertView.getContext(),card_item.getGender(),card_item.getPhotoProfile(),image);



        if (card_item.getPhotoProfile().equals("none")){
            username.setTextColor(convertView.getResources().getColor(R.color.colorGrayPure));
            age.setTextColor(convertView.getResources().getColor(R.color.colorGrayPure));
            tvGoma.setTextColor(convertView.getResources().getColor(R.color.colorGrayPure));
        }


            if (card_item.getImages() > 0) {
                imgvHasPhotosMatches.setVisibility(View.VISIBLE);
                tvCountImagesUser.setVisibility(View.VISIBLE);
                tvCountImagesUser.setText(String.valueOf(card_item.getImages()));
            } else {
                imgvHasPhotosMatches.setVisibility(View.GONE);
                tvCountImagesUser.setVisibility(View.GONE);
            }



        return convertView;

    }

    public void getImageProfile(final Context ct,final String sex,final String imageUri,final RoundedImageView civProfilePhoto){

        if(!imageUri.equals("none") && sex.equals("Man")) {

            //// Offline Capabilities: networkPolicy(NetworkPolicy.OFFLINE)
            Picasso.get().load(imageUri).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_male_selected).into(civProfilePhoto, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(imageUri).placeholder(R.drawable.ic_gender_male_selected)
                            .into(civProfilePhoto);
                }
            });

        }else
        if(!imageUri.equals("none") && sex.equals("Woman")){

            Picasso.get().load(imageUri).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_female_selected).into(civProfilePhoto, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(imageUri).placeholder(R.drawable.ic_gender_female_selected)
                            .into(civProfilePhoto);
                }
            });

        }if(imageUri.equals("none") && sex.equals("Woman")){

            Picasso.get().load(R.drawable.ic_gender_female_selected).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_female_selected).into(civProfilePhoto, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(R.drawable.ic_gender_female_selected).placeholder(R.drawable.ic_gender_female_selected)
                            .into(civProfilePhoto);
                }
            });

        }
        else
        if(imageUri.equals("none") && sex.equals("Man")){
            Picasso.get().load(R.drawable.ic_gender_male_selected).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_male_selected).into(civProfilePhoto, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(R.drawable.ic_gender_male_selected).placeholder(R.drawable.ic_gender_male_selected)
                            .into(civProfilePhoto);
                }
            });
        }

    }

    

    private void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

}
