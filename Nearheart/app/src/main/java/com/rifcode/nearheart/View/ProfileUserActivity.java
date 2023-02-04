package com.rifcode.nearheart.View;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.rifcode.nearheart.Utils.DialogUtils;
import com.rifcode.nearheart.R;
import com.rifcode.nearheart.Models.myPictures;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileUserActivity extends AppCompatActivity {

    private final String TAG = ProfileUserActivity.class.getSimpleName();
    private NativeAd nativeAd;
    private TextView tvAbout,tvjob,tvherefor,tvlivein;
    private RecyclerView rvImgs;
    private LinearLayoutManager mLayoutManager;
    private FirebaseRecyclerAdapter<myPictures, usersImagesViewHolder> firebaseRecyclerAdapter;
    private DatabaseReference dbRefUser;
    private String userHisID;
    private TextView tvusername,tvage;
    private String whyyah_text="";
    private RoundedImageView imgvPhotoUserProfile;
    private LinearLayout lyJobUserProfile,lyhereforUserProfile,lyAboutUserProfile;
    private ImageView imgvLikeProfileUser,imgvdisLikeProfileUser,imgvSendMsgProfileUser;
    private FirebaseAuth mAuth;
    private DatabaseReference dbConnections;
    private String myuserID;
    private LinearLayout lyYehOrNotProfileUser;
    private ImageView btnReportUser;
    private View mViewInflateReportUser;
    private AlertDialog alertDialog;
    private DatabaseReference dbReport;
    private ScrollView scrollVProfileUser;
    private LinearLayout lyimagestitleProfileuser;
    private InterstitialAd mInterstitialAd;
    private NativeAdLayout nativeAdLayout;
    private LinearLayout adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        wedgets();
        mAuth = FirebaseAuth.getInstance();
        myuserID = mAuth.getUid();
        dbConnections = FirebaseDatabase.getInstance().getReference().child("connections");
        dbReport = FirebaseDatabase.getInstance().getReference().child("reports");
        userHisID = getIntent().getStringExtra("userIDvisited");
        dbRefUser = FirebaseDatabase.getInstance().getReference().child("users");
        dbRefUser.keepSynced(true);

        // interstial admob
        //facebook ads interstital ad
        mInterstitialAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.Interstitial_FacebbokAds));
        // load the ad
        mInterstitialAd.loadAd();
        if(mInterstitialAd.isAdLoaded())
            // Show the ad
            mInterstitialAd.show();

        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);

        // check if matched //
        isConnectionMatch(userHisID);


        imgvLikeProfileUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // when i liked someone, set myid in his or her connections like
                dbConnections.child(userHisID).child("like").child(myuserID).child("Time").setValue(-1*System.currentTimeMillis());
                finish();

                checkConnectionMatch();
            }
        });

        imgvdisLikeProfileUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbConnections.child(userHisID).child("dislike").child(myuserID).child("Time").setValue(-1*System.currentTimeMillis());
                DatabaseReference dbCheckDislike = dbConnections.child(myuserID).child("like");
                if(dbCheckDislike.child(userHisID)!=null)
                    dbConnections.child(myuserID).child("like").child(userHisID).removeValue();
                finish();
            }
        });

        btnReportUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogReportUser();
            }
        });

        imgvSendMsgProfileUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatintent =  new Intent(ProfileUserActivity.this,ChatActivity.class);
                chatintent.putExtra("userIDvisited",userHisID);
                startActivity(chatintent);
                Animatoo.animateSlideLeft(ProfileUserActivity.this);
            }
        });

    }

    private void loadNativeAd() {
        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        nativeAd = new NativeAd(this, getString(R.string.NativeAds_Facebook));
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e("TAG", "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e("TAG", "Native ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                Log.d("TAG", "Native ad is loaded and ready to be displayed!");
                // Race condition, load() called again before last ad was displayed
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d("TAG", "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d("TAG", "Native ad impression logged!");
            }
        };


        // Request an ad
        nativeAd.loadAd(
                nativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());

        showNativeAdWithDelay();
    }

    private void showNativeAdWithDelay() {
        /**
         * Here is an example for displaying the ad with delay;
         * Please do not copy the Handler into your project
         */
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Check if nativeAd has been loaded successfully
                if(nativeAd == null || !nativeAd.isAdLoaded()) {
                    return;
                }
                // Check if ad is already expired or invalidated, and do not show ad if that is the case. You will not get paid to show an invalidated ad.
                if(nativeAd.isAdInvalidated()) {
                    return;
                }
                inflateAd(nativeAd); // Inflate NativeAd into a container, same as in previous code examples
            }
        }, 1000 * 60 * 15); // Show the ad after 15 minutes
    }

    private void inflateAd(NativeAd nativeAd) {

        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        nativeAdLayout = findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(ProfileUserActivity.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdOptionsView
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(ProfileUserActivity.this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView, nativeAdMedia, nativeAdIcon, clickableViews);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(ProfileUserActivity.this);
        finish();
    }



    private void dialogReportUser(){
        mViewInflateReportUser = getLayoutInflater().inflate(R.layout.dialog_report_user,null);

        LinearLayout lyInapproPhotos =  mViewInflateReportUser.findViewById(R.id.lyInapproPhotos);
        LinearLayout lyLikeSpam = mViewInflateReportUser.findViewById(R.id.lyLikeSpam);
        LinearLayout lyUserUnage = mViewInflateReportUser.findViewById(R.id.lyUserUnage);
        LinearLayout lyOther = mViewInflateReportUser.findViewById(R.id.lyOther);


        AlertDialog.Builder alertDialogBuilderReportUser = DialogUtils.CustomAlertDialog(mViewInflateReportUser,this);
        alertDialog = alertDialogBuilderReportUser.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationENTER; //style id
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        alertDialog.show();

        lyInapproPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //report inappropriate photos
                String keypush = dbReport.child("inappropriate_photo").push().getKey();
                dbReport.child("inappropriate_photo").child(keypush).child("userID").setValue(myuserID);
                dbReport.child("inappropriate_photo").child(keypush).child("reportUserID").setValue(userHisID);
                dbReport.child("inappropriate_photo").child(keypush).child("time").setValue(System.currentTimeMillis()*-1);


                alertDialog.cancel();
                Toast.makeText(ProfileUserActivity.this, getString(R.string.report_sent), Toast.LENGTH_SHORT).show();
            }
        });

        lyLikeSpam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keypush = dbReport.child("spam").push().getKey();
                dbReport.child("spam").child(keypush).child("userID").setValue(myuserID);
                dbReport.child("spam").child(keypush).child("reportUserID").setValue(userHisID);
                dbReport.child("spam").child(keypush).child("time").setValue(System.currentTimeMillis()*-1);

                alertDialog.cancel();
                Toast.makeText(ProfileUserActivity.this, getString(R.string.report_sent), Toast.LENGTH_SHORT).show();

            }
        });

        lyUserUnage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbReport.child(myuserID).child(userHisID).setValue("User is Underage");
                alertDialog.cancel();
                Toast.makeText(ProfileUserActivity.this, getString(R.string.report_sent), Toast.LENGTH_SHORT).show();

            }
        });

        lyOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbReport.child(myuserID).child(userHisID).setValue("other");
                alertDialog.cancel();
                Toast.makeText(ProfileUserActivity.this, getString(R.string.report_sent), Toast.LENGTH_SHORT).show();

            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
//load so show the ads InterstitialAd
        if(mInterstitialAd.isAdLoaded())
            // Show the ad
            mInterstitialAd.show();
    }

    private void wedgets() {

        // rcv
        rvImgs = findViewById(R.id.rvImagesProfUser);
        rvImgs.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        rvImgs.setLayoutManager(mLayoutManager);

        tvAbout =findViewById(R.id.tvAboutUserProfile);
        tvherefor =findViewById(R.id.tvHerforUserProfile);
        tvjob =findViewById(R.id.tvJobUserProfile);
        tvlivein =findViewById(R.id.tvLiveinUserProfile);
        tvusername =findViewById(R.id.tvUsernameProfileUser);
        tvage =findViewById(R.id.tvAgeProfileUser);
        imgvPhotoUserProfile =findViewById(R.id.imgvPhotoUserProfile);
        imgvSendMsgProfileUser =findViewById(R.id.imgvSendMsgProfileUser);
        lyJobUserProfile =findViewById(R.id.lyJobUserProfile);
        lyhereforUserProfile =findViewById(R.id.lyhereforUserProfile);
        lyAboutUserProfile =findViewById(R.id.lyAboutUserProfile);
        imgvLikeProfileUser =findViewById(R.id.imgvLikeProfileUser);
        imgvdisLikeProfileUser =findViewById(R.id.imgvdisLikeProfileUser);
        lyYehOrNotProfileUser = findViewById(R.id.lyYehOrNotProfileUser);
        btnReportUser = findViewById(R.id.btnReportUser);
        scrollVProfileUser = findViewById(R.id.scrollVProfileUser);
        lyimagestitleProfileuser = findViewById(R.id.lyimagestitleProfileuser);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        whyyah_text="";
        scrollVProfileUser.post(new Runnable() {
            @Override
            public void run() {
                scrollVProfileUser.scrollTo(0, 0);
                scrollVProfileUser.pageScroll(View.FOCUS_UP);
                scrollVProfileUser.smoothScrollTo(0,0);
            }
        });
        retreiveData();
        frImages();
        loadNativeAd();
    }

    private void isConnectionMatch(String userID) {
        // check if already she or he like
        DatabaseReference dbConnectionAlreadyLike = dbConnections.child(myuserID).child("matches").child(userID);
        dbConnectionAlreadyLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                   imgvdisLikeProfileUser.setVisibility(View.GONE);
                   imgvLikeProfileUser.setVisibility(View.GONE);
                   imgvSendMsgProfileUser.setVisibility(View.VISIBLE);
                   btnReportUser.setVisibility(View.VISIBLE);
                }else{
                    imgvdisLikeProfileUser.setVisibility(View.VISIBLE);
                    imgvLikeProfileUser.setVisibility(View.VISIBLE);
                    imgvSendMsgProfileUser.setVisibility(View.GONE);
                    btnReportUser.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void checkConnectionMatch() {
        // check if already she or he like
        DatabaseReference dbConnectionAlreadyLike = dbConnections.child(myuserID).child("like").child(userHisID);
        dbConnectionAlreadyLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    
                    dbConnections.child(myuserID).child("matches").child(userHisID).child("Time").setValue(-1*System.currentTimeMillis());
                    dbConnections.child(userHisID).child("matches").child(myuserID).child("Time").setValue(-1*System.currentTimeMillis());
                    dbConnections.child(myuserID).child("like").child(userHisID).removeValue();
                    dbConnections.child(userHisID).child("like").child(myuserID).removeValue();

                        dbConnections.child(userHisID).child("dislike").child(myuserID).removeValue();
                        dbConnections.child(myuserID).child("dislike").child(userHisID).removeValue();

                    // notification match here
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void retreiveData(){



        if(userHisID.equals(myuserID)){
            lyYehOrNotProfileUser.setVisibility(View.GONE);
            btnReportUser.setVisibility(View.GONE);
        }else{
            lyYehOrNotProfileUser.setVisibility(View.VISIBLE);
            btnReportUser.setVisibility(View.VISIBLE);
        }

        dbRefUser.child(userHisID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(userHisID!=null) {

                    String about = dataSnapshot.child("about").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    String work = dataSnapshot.child("work").getValue().toString();
                    String age = dataSnapshot.child("age").getValue().toString();

                    if(dataSnapshot.hasChild("city")) {
                        if(dataSnapshot.hasChild("country"))
                        {
                            String city = dataSnapshot.child("city").getValue().toString();
                            String country = dataSnapshot.child("country").getValue().toString();
                            tvlivein.setText(city+", "+country);
                        }
                    }

                    if(dataSnapshot.hasChild("photoProfile")) {
                        String gender = dataSnapshot.child("gender").getValue().toString();
                        String photo = dataSnapshot.child("photoProfile").getValue().toString();
                        getImageProfile(gender, photo);
                    }


                    if(about.equals("---")){
                        lyAboutUserProfile.setVisibility(View.GONE);
                    }else{
                        lyAboutUserProfile.setVisibility(View.VISIBLE);
                    }

                    if(work.equals("---")){
                        lyJobUserProfile.setVisibility(View.GONE);
                    }else{
                        lyJobUserProfile.setVisibility(View.VISIBLE);
                    }

                    if(dataSnapshot.hasChild("images")){
                        lyimagestitleProfileuser.setVisibility(View.VISIBLE);
                    }else{
                        lyimagestitleProfileuser.setVisibility(View.GONE);
                    }


                    tvAbout.setText(about);
                    tvjob.setText(work);
                    tvusername.setText(username);
                    tvage.setText(age);


                    if (!dataSnapshot.hasChild("whyyah") || dataSnapshot.child("whyyah").getValue().toString().equals("")){
                        lyhereforUserProfile.setVisibility(View.GONE);
                    }
                    else{

                        lyhereforUserProfile.setVisibility(View.VISIBLE);

                        String whyyah = dataSnapshot.child("whyyah").getValue().toString();


                        String[] ary = whyyah.split("");

                        if(whyyah.equals("12")){
                            whyyah_text = getString(R.string.date)+", "+getString(R.string.make_new_friends);
                        }else {

                            for (int i = 0; i < ary.length; i++) {
                                if (ary.length > 2) {
                                    if (ary[i].equals("0"))
                                        whyyah_text = whyyah_text + getString(R.string.chat);
                                    if (ary[i].equals("1"))
                                        whyyah_text = whyyah_text + ", " + getString(R.string.date);
                                    if (ary[i].equals("2"))
                                        whyyah_text = whyyah_text + ", " + getString(R.string.make_new_friends);
                                } else {
                                    if (ary[i].equals("0"))
                                        whyyah_text = whyyah_text + getString(R.string.chat);
                                    if (ary[i].equals("1"))
                                        whyyah_text = whyyah_text + getString(R.string.date);
                                    if (ary[i].equals("2"))
                                        whyyah_text = whyyah_text + getString(R.string.make_new_friends);
                                }
                            }

                        }
                        tvherefor.setText(whyyah_text);

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void frImages(){

        //pbPostLoad.setVisibility(View.VISIBLE);  // To show ProgressBar
        final Query qrMyimages = dbRefUser.child(userHisID).child("images").orderByChild("Time");
        final DatabaseReference dbMyimages = dbRefUser.child(userHisID).child("images");
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<myPictures, usersImagesViewHolder>(
                myPictures.class,
                R.layout.layout_userimages,
                usersImagesViewHolder.class,
                qrMyimages
        ) {

            @Override
            protected void populateViewHolder(final usersImagesViewHolder viewHolder, final myPictures model, int position) {

                String listPostKey = getRef(position).getKey();

                dbMyimages.child(listPostKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       String thumb_picture = String.valueOf(dataSnapshot.child("thumb_picture").getValue());
                       viewHolder.setmyImage(thumb_picture,ProfileUserActivity.this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.imgvUserImages.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent photopostIntent = new Intent(ProfileUserActivity.this,PhotoPostActivity.class);
//                        photopostIntent.putExtra("myimage_click", idimageclicked);
//                        startActivity(photopostIntent);
                    }
                });

            }


        };

        rvImgs.setAdapter(firebaseRecyclerAdapter);

    }

    public void getImageProfile(final String sex,final String imageUri){

        if(!imageUri.equals("none") && sex.equals("Man")) {

            //// Offline Capabilities: networkPolicy(NetworkPolicy.OFFLINE)
            Picasso.get().load(imageUri).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_male_selected).into(imgvPhotoUserProfile, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(imageUri).placeholder(R.drawable.ic_gender_male_selected)
                            .into(imgvPhotoUserProfile);
                }
            });

        }else
        if(!imageUri.equals("none") && sex.equals("Woman")){

            Picasso.get().load(imageUri).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_female_selected).into(imgvPhotoUserProfile, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(imageUri).placeholder(R.drawable.ic_gender_female_selected)
                            .into(imgvPhotoUserProfile);
                }
            });

        }if(imageUri.equals("none") && sex.equals("Woman")){

            Picasso.get().load(R.drawable.ic_gender_female_selected).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_female_selected).into(imgvPhotoUserProfile, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(R.drawable.ic_gender_female_selected).placeholder(R.drawable.ic_gender_female_selected)
                            .into(imgvPhotoUserProfile);
                }
            });

        }
        else
        if(imageUri.equals("none") && sex.equals("Man")){
            Picasso.get().load(R.drawable.ic_gender_male_selected).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_male_selected).into(imgvPhotoUserProfile, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(R.drawable.ic_gender_male_selected).placeholder(R.drawable.ic_gender_male_selected)
                            .into(imgvPhotoUserProfile);
                }
            });
        }

    }

    public static class usersImagesViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imgvUserImages;

        public usersImagesViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            imgvUserImages = view.findViewById(R.id.imgvUsersImage);
        }


        public void setmyImage(final String ThumbImage, final Context ctx) {
            Picasso.get().load(ThumbImage).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.portrait_placeholder).into(imgvUserImages, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(ThumbImage).placeholder(R.drawable.placeholder_image).into(imgvUserImages);
                }
            });
        }
    }



}
