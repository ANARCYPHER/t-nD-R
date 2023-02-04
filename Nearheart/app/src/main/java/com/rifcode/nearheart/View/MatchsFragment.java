package com.rifcode.nearheart.View;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.rifcode.nearheart.Adapter.ArrayAdapterMatches;
import com.rifcode.nearheart.Models.CardMatches;
import com.rifcode.nearheart.Utils.DialogUtils;
import com.rifcode.nearheart.Utils.GPSTracker;
import com.rifcode.nearheart.R;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class MatchsFragment extends Fragment {
    public static int REQUEST_PERMISSIONS_CODE_ACCESS_FINE_LOCATION=250;

    private final int REQUEST_CHECK_SETTINGS = 1040;
    private View mfview;
    private ArrayList<CardMatches> rowItemsMatches;
    private ArrayAdapterMatches arrAdpMatches;
    private DatabaseReference dbConnections;
    private String myuserID;
    private FirebaseAuth mAuth;
    private DatabaseReference dbUsers;
    private String oppositeGender;
    public static SwipeFlingAdapterView flingContainer;
    private static int ACCESS_FINE_LOCATION_NUM = 2;
    private String city;
    private String country_code, show_me_setting, age_setting;
    private SwipeRefreshLayout srMatches;
    private View mViewInflateEnableGPS;
    private AlertDialog alertDialogGPS,alertDialogNewMatch,alertDialogReport;
    private CircleImageView imgvUserRipBack;
    private RippleBackground rippleBackground;
    private TextView tvSearching;
    private DatabaseReference dbChat;
    private View mViewInflatedialog_new_match;
    private DatabaseReference dbReport;
    private View mViewInflateReportUser;
    private int selectPurchase;
    private AlertDialog alertDialog;
    private GPSTracker gps;
    private List<Address> addresses;
    private ProgressDialog progressDialog;

    public MatchsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        mfview = inflater.inflate(R.layout.fragment_matchs, container, false);
        mAuth = FirebaseAuth.getInstance();
        myuserID = mAuth.getUid();
        dbConnections = FirebaseDatabase.getInstance().getReference().child("connections");
        dbChat = FirebaseDatabase.getInstance().getReference().child("chat");
        dbUsers = FirebaseDatabase.getInstance().getReference().child("users");
        dbReport = FirebaseDatabase.getInstance().getReference().child("reports");

        imgvUserRipBack=mfview.findViewById(R.id.imgvUserRipBack);
        tvSearching=mfview.findViewById(R.id.tvSearching);
        rippleBackground=mfview.findViewById(R.id.ripback);
        flingContainer = mfview.findViewById(R.id.fsMatches);

        rowItemsMatches = new ArrayList<CardMatches>();
        arrAdpMatches = new ArrayAdapterMatches(getActivity(), R.layout.item_matches, rowItemsMatches);

        flingContainer.setAdapter(arrAdpMatches);

        /// progress Dialog :
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getString(R.string.str_search_y_location));
        progressDialog.setProgressStyle(R.style.MyAlertDialogStyle);
        progressDialog.setCanceledOnTouchOutside(true);

        if (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERMISSIONS_CODE_ACCESS_FINE_LOCATION);

        } else {
            Log.e("DB", "PERMISSION GRANTED");

                progressDialog.show();
                fetch_GPS();


        }

        dbUsers.child(myuserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String imageurl = String.valueOf(dataSnapshot.child("photoProfile").getValue());
                    String gender = String.valueOf(dataSnapshot.child("gender").getValue());

                    getImageProfile(gender,imageurl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imgvUserRipBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rippleBackground.startRippleAnimation();
                tvSearching.setText(getString(R.string.searching));

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        loadMatches();
                        checkMyGender();

                        if(rowItemsMatches.size()==0){
                            rippleBackground.stopRippleAnimation();
                            tvSearching.setText(getString(R.string.soory_canfind_more_match));
                        }
                    }
                }, 5000);


            }
        });





        return mfview;
    }


    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    public void getImageProfile(final String sex,final String imageUri){

        if(!imageUri.equals("none") && sex.equals("Man")) {

            //// Offline Capabilities: networkPolicy(NetworkPolicy.OFFLINE)
            Picasso.get().load(imageUri).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_male_selected).into(imgvUserRipBack, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(imageUri).placeholder(R.drawable.ic_gender_male_selected)
                            .into(imgvUserRipBack);
                }
            });

        }else
        if(!imageUri.equals("none") && sex.equals("Woman")){

            Picasso.get().load(imageUri).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_female_selected).into(imgvUserRipBack, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(imageUri).placeholder(R.drawable.ic_gender_female_selected)
                            .into(imgvUserRipBack);
                }
            });

        }if(imageUri.equals("none") && sex.equals("Woman")){

            Picasso.get().load(R.drawable.ic_gender_female_selected).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_female_selected).into(imgvUserRipBack, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(R.drawable.ic_gender_female_selected).placeholder(R.drawable.ic_gender_female_selected)
                            .into(imgvUserRipBack);
                }
            });

        }
        else
        if(imageUri.equals("none") && sex.equals("Man")){
            Picasso.get().load(R.drawable.ic_gender_male_selected).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_male_selected).into(imgvUserRipBack, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(R.drawable.ic_gender_male_selected).placeholder(R.drawable.ic_gender_male_selected)
                            .into(imgvUserRipBack);
                }
            });
        }

    }


 public void getImageProfileDialogNewMatch(final String sex,final String imageUri,ImageView imgvDialog){

        if(!imageUri.equals("none") && sex.equals("Man")) {

            //// Offline Capabilities: networkPolicy(NetworkPolicy.OFFLINE)
            Picasso.get().load(imageUri).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_male_selected).into(imgvDialog, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(imageUri).placeholder(R.drawable.ic_gender_male_selected)
                            .into(imgvDialog);
                }
            });

        }else
        if(!imageUri.equals("none") && sex.equals("Woman")){

            Picasso.get().load(imageUri).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_female_selected).into(imgvDialog, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(imageUri).placeholder(R.drawable.ic_gender_female_selected)
                            .into(imgvDialog);
                }
            });

        }if(imageUri.equals("none") && sex.equals("Woman")){

            Picasso.get().load(R.drawable.ic_gender_female_selected).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_female_selected).into(imgvDialog, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(R.drawable.ic_gender_female_selected).placeholder(R.drawable.ic_gender_female_selected)
                            .into(imgvDialog);
                }
            });

        }
        else
        if(imageUri.equals("none") && sex.equals("Man")){
            Picasso.get().load(R.drawable.ic_gender_male_selected).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_male_selected).into(imgvDialog, new Callback() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(R.drawable.ic_gender_male_selected).placeholder(R.drawable.ic_gender_male_selected)
                            .into(imgvDialog);
                }
            });
        }

    }


    private void loadMatches() {

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                Log.d("LIST", "removed object!");
                if (rowItemsMatches.size() > 0) {

                    rowItemsMatches.remove(0);
                    arrAdpMatches.notifyDataSetChanged();
                }
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                CardMatches obj = (CardMatches) dataObject;
                String userId = obj.getUserId();
                // when i disliked someone, set myid in his or her connections dislike
                dbConnections.child(userId).child("dislike").child(myuserID).child("Time").setValue(-1 * System.currentTimeMillis());

                DatabaseReference dbCheckDislike = dbConnections.child(myuserID).child("like");
                if(dbCheckDislike.child(userId)!=null)
                    dbConnections.child(myuserID).child("like").child(userId).removeValue();

                if (rowItemsMatches.size()==0) {
                    rippleBackground.setVisibility(View.VISIBLE);
                    tvSearching.setText(getString(R.string.soory_canfind_more_match));
                }

            }

            @Override
            public void onRightCardExit(Object dataObject) {
                CardMatches obj = (CardMatches) dataObject;
                String userId = obj.getUserId();

                // when i liked someone, set myid in his or her connections like
                dbConnections.child(userId).child("like").child(myuserID).child("Time")
                        .setValue(-1 * System.currentTimeMillis());


                isConnectionMatch(userId);

                if (rowItemsMatches.size()==0) {
                    rippleBackground.setVisibility(View.VISIBLE);
                    tvSearching.setText(getString(R.string.soory_canfind_more_match));
                }

                //notification like here
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_CODE_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), R.string.fine_location_perm_granted, Toast.LENGTH_LONG).show();
                gps=new GPSTracker(getActivity(),getActivity());
                if(gps.canGetLocation()){
                    fetch_GPS();
                }else{
                    buildAlertMessageNoGps();
                }
            } else {
                Toast.makeText(getActivity(), R.string.fine_location_perms_denied, Toast.LENGTH_LONG).show();
            }

        }


    }

//    public boolean statusCheckGPS() {
//        final LocationManager manager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
//
//        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
//            return false;
//        else
//            return true;
//    }

    private void buildAlertMessageNoGps() {

        mViewInflateEnableGPS = getLayoutInflater().inflate(R.layout.dialog_enable_mylocation, null);

        Button btnYesDMylocation = mViewInflateEnableGPS.findViewById(R.id.btnYesDMylocation);

        final AlertDialog.Builder adenableGPS = DialogUtils.CustomAlertDialog(mViewInflateEnableGPS, getActivity());
        alertDialogGPS = adenableGPS.create();
        alertDialogGPS.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationENTER; //style id
        alertDialogGPS.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialogGPS.setCancelable(false);
        alertDialogGPS.show();


        btnYesDMylocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                GPSTracker gps1 = new GPSTracker(getActivity(),getActivity());
                // Check if GPS enabled
                if (gps1.canGetLocation()) {
                    fetch_GPS();
                    getFragmentManager().beginTransaction().detach(MatchsFragment.this).attach(MatchsFragment.this).commit();
                    alertDialogGPS.dismiss();
                    alertDialogGPS.cancel();
                } else {
                    buildAlertMessageNoGps();
                }
            }
        });

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



    public void fetch_GPS(){

        final LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this displays dialog box like Google Maps with two buttons - OK and NO,THANKS

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        gps = new GPSTracker(getActivity(),getActivity());
        if(gps.canGetLocation()){


            if(locationRequest!=null) {
                LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        if (this!=null) {
                            if (LocationServices.getFusedLocationProviderClient(getActivity()) != null) {
                                LocationServices.getFusedLocationProviderClient(getActivity()).removeLocationUpdates(this);
                            }
                        }
                        double result = 0;
                        if (locationResult != null && locationResult.getLocations().size() > 0) {

                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            if(this!=null) {
                                Geocoder geocoder = new Geocoder(getActivity(), Locale.ENGLISH);
                                addresses = null;
                                try {
                                    addresses = geocoder.getFromLocation(latitude, longitude, 1);

                                    if (addresses != null && addresses.size() != 0) {


                                        String cityName = addresses.get(0).getLocality();
                                        dbUsers.child(myuserID).child("city").setValue(cityName);
                                        String countryName = addresses.get(0).getCountryName();
                                        dbUsers.child(myuserID).child("country").setValue(countryName);
                                        final List<Address> finalAddresses = addresses;
                                        dbUsers.child(myuserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshodiscovery) {
                                                String discovery = String.valueOf(dataSnapshodiscovery.child("discovery").getValue());
                                                if (discovery.equals("false")) {
                                                    String countryCode = finalAddresses.get(0).getCountryCode();
                                                    dbUsers.child(myuserID).child("country_code").setValue(countryCode);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            // getDataFromLocation(latitude, longitude);

                        }
                        if(progressDialog!=null)
                            progressDialog.dismiss();
                    }
                }, Looper.getMainLooper());

            }

            Task<LocationSettingsResponse> task =
                    LocationServices.getSettingsClient(getActivity()).checkLocationSettings(builder.build());

            task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                @Override
                public void onComplete(Task<LocationSettingsResponse> task) {
                    try {
                        LocationSettingsResponse response = task.getResult(ApiException.class);
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                    } catch (ApiException exception) {
                        switch (exception.getStatusCode()) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied. But could be fixed by showing the
                                // user a dialog.
                                try {
                                    // Cast to a resolvable exception.
                                    ResolvableApiException resolvable = (ResolvableApiException) exception;
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    resolvable.startResolutionForResult(
                                            getActivity(),
                                            REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                } catch (ClassCastException e) {
                                    // Ignore, should be an impossible error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied. However, we have no way to fix the
                                // settings so we won't show the dialog.
                                break;
                        }
                    }
                }
            });

        }
        else{

            if(progressDialog!=null)
                progressDialog.dismiss();
            buildAlertMessageNoGps();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);

        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        //Toast.makeText(getActivity(),"User has clicked on OK - So GPS is on", Toast.LENGTH_SHORT).show();
                        fetch_GPS();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        //Toast.makeText(getActivity(),"User has clicked on NO, THANKS - So GPS is still off.", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), getString(R.string.must_enable_location), Toast.LENGTH_LONG).show();

                        break;
                    default:
                        break;
                }
                break;
        }


    }



    @Override
    public void onStart() {
        super.onStart();


        if (getUserVisibleHint()) {
            rippleBackground.setVisibility(View.VISIBLE);
            rippleBackground.startRippleAnimation();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(rowItemsMatches.size()==0){
                        rippleBackground.stopRippleAnimation();
                        tvSearching.setText(getActivity().getString(R.string.soory_canfind_more_match));
                    }

                }
            }, 5000);



            loadMatches();
            checkMyGender();


        }


    }



    private void isConnectionMatch(final String userId) {
        // check if already she or he like
        DatabaseReference dbConnectionAlreadyLike = dbConnections.child(myuserID).child("like").child(userId);
        dbConnectionAlreadyLike.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    
                    
                    dbConnections.child(myuserID).child("matches").child(userId).child("Time").setValue(-1*System.currentTimeMillis());
                    dbConnections.child(userId).child("matches").child(myuserID).child("Time").setValue(-1*System.currentTimeMillis());
                    dbConnections.child(myuserID).child("like").child(userId).removeValue();
                    dbConnections.child(userId).child("like").child(myuserID).removeValue();
                        dbConnections.child(userId).child("dislike").child(myuserID).removeValue();
                        dbConnections.child(myuserID).child("dislike").child(userId).removeValue();

                    
                    dialog_new_match(userId);
                    
                    // notification match here
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }



    private void dialog_new_match(String userID){

        mViewInflatedialog_new_match = getLayoutInflater().inflate(R.layout.dialog_new_match,null);

        android.widget.ImageView imgv_ic_warning_userDialogNewMatch = mViewInflatedialog_new_match.findViewById(R.id.imgv_ic_warning_userDialogNewMatch);
        android.widget.ImageView imgv_ic_send_message_DialogNewMatch = mViewInflatedialog_new_match.findViewById(R.id.imgv_ic_send_message_DialogNewMatch);
        android.widget.ImageView imgvExitDialogNewMatch = mViewInflatedialog_new_match.findViewById(R.id.imgvExitDialogNewMatch);
        android.widget.TextView tvUsernameDialogNewMatch = mViewInflatedialog_new_match.findViewById(R.id.tvUsernameDialogNewMatch);
        android.widget.TextView tvAgeDialogNewMatch = mViewInflatedialog_new_match.findViewById(R.id.tvAgeDialogNewMatch);
        android.widget.ImageView civProfilePhotoDialogNewMatch = mViewInflatedialog_new_match.findViewById(R.id.civProfilePhotoDialogNewMatch);

        AlertDialog.Builder alertDialogBuilderpost = DialogUtils.CustomAlertDialog(mViewInflatedialog_new_match,getActivity());
        alertDialogNewMatch = alertDialogBuilderpost.create();
        alertDialogNewMatch.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationENTER; //style id
        alertDialogNewMatch.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialogNewMatch.setCancelable(true);
        alertDialogNewMatch.show();

        imgv_ic_send_message_DialogNewMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pp =  new Intent(getActivity(), ChatActivity.class);
                pp.putExtra("userIDvisited",userID);
                startActivity(pp);
                Animatoo.animateSlideLeft(getActivity());
                alertDialogNewMatch.dismiss();
            }
        });

        imgv_ic_warning_userDialogNewMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogReportUser(userID);
                alertDialogNewMatch.dismiss();
            }
        });

        imgvExitDialogNewMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogNewMatch.dismiss();
            }
        });


        dbUsers.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String imageurl = String.valueOf(dataSnapshot.child("photoProfile").getValue());
                String age = String.valueOf(dataSnapshot.child("age").getValue());
                String gender = String.valueOf(dataSnapshot.child("gender").getValue());
                String username = String.valueOf(dataSnapshot.child("username").getValue());

                tvAgeDialogNewMatch.setText(age);
                tvUsernameDialogNewMatch.setText(username);

                getImageProfileDialogNewMatch(gender,imageurl,civProfilePhotoDialogNewMatch);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void dialogReportUser(String userHisID){
        mViewInflateReportUser = getLayoutInflater().inflate(R.layout.dialog_report_user,null);

        LinearLayout lyInapproPhotos =  mViewInflateReportUser.findViewById(R.id.lyInapproPhotos);
        LinearLayout lyLikeSpam = mViewInflateReportUser.findViewById(R.id.lyLikeSpam);
        LinearLayout lyUserUnage = mViewInflateReportUser.findViewById(R.id.lyUserUnage);
        LinearLayout lyOther = mViewInflateReportUser.findViewById(R.id.lyOther);


        AlertDialog.Builder alertDialogBuilderReportUser = DialogUtils.CustomAlertDialog(mViewInflateReportUser,getActivity());
        alertDialogReport = alertDialogBuilderReportUser.create();
        alertDialogReport.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationENTER; //style id
        alertDialogReport.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialogReport.setCancelable(true);
        alertDialogReport.show();

        lyInapproPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbReport.child(myuserID).child(userHisID).setValue("Inappropriate Photos");
                alertDialogReport.cancel();
                Toast.makeText(getActivity(), getString(R.string.report_sent), Toast.LENGTH_SHORT).show();
            }
        });

        lyLikeSpam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbReport.child(myuserID).child(userHisID).setValue("Spam");
                alertDialogReport.cancel();
                Toast.makeText(getActivity(), getString(R.string.report_sent), Toast.LENGTH_SHORT).show();

            }
        });

        lyUserUnage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbReport.child(myuserID).child(userHisID).setValue("User is Underage");
                alertDialogReport.cancel();
                Toast.makeText(getActivity(), getString(R.string.report_sent), Toast.LENGTH_SHORT).show();

            }
        });

        lyOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbReport.child(myuserID).child(userHisID).setValue("other");
                alertDialogReport.cancel();
                Toast.makeText(getActivity(), getString(R.string.report_sent), Toast.LENGTH_SHORT).show();

            }
        });


    }


    private void checkMyGender(){
        DatabaseReference dbMyIDusers = dbUsers.child(myuserID);
        dbMyIDusers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                       if(dataSnapshot.hasChild("discovery") &&
                               dataSnapshot.child("discovery").getValue().toString().equals("true")) {


                               String age_range = dataSnapshot.child("discovery_setting").child("age_range").getValue().toString();
                               String show_me = dataSnapshot.child("discovery_setting").child("show_me").getValue().toString();

                               age_setting = age_range;
                               show_me_setting = show_me;

                               if (dataSnapshot.hasChild("gender")){
                                   if(dataSnapshot.hasChild("city") && dataSnapshot.hasChild("country_code")) {
                                       String gender = dataSnapshot.child("gender").getValue().toString();
                                       switch (gender){
                                           case "Man":
                                               oppositeGender = "Woman";
                                               break;
                                           case "Woman":
                                               oppositeGender = "Man";
                                               break;
                                       }
                                       String newcity = dataSnapshot.child("city").getValue().toString();
                                       String newcountry_code = dataSnapshot.child("country_code").getValue().toString();

                                       city = newcity;
                                       country_code = newcountry_code;

                                       getMatches();
                                   }
                           }
                       }else
                           {

                               if (dataSnapshot.hasChild("gender")){
                                   if(dataSnapshot.hasChild("city") && dataSnapshot.hasChild("country_code")) {
                                       String gender = dataSnapshot.child("gender").getValue().toString();
                                       switch (gender){
                                           case "Man":
                                               oppositeGender = "Woman";
                                               break;
                                           case "Woman":
                                               oppositeGender = "Man";
                                               break;
                                       }
                                       String newcity = dataSnapshot.child("city").getValue().toString();
                                       String newcountry_code = dataSnapshot.child("country_code").getValue().toString();

                                       city = newcity;
                                       country_code = newcountry_code;

                                       getMatches();
                                   }
                               }
                       }




            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getMatches(){

        dbUsers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshotusers, String s) {

                if (!dataSnapshotusers.getKey().equals(myuserID) && dataSnapshotusers.hasChild("gender")) {

                    dbUsers.child(myuserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshodiscovery) {
                            String discovery = String.valueOf(dataSnapshodiscovery.child("discovery").getValue());


                            if(discovery.equals("false")){
                                dbConnections.child(String.valueOf(dataSnapshotusers.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshotConnection) {

                                        if (!dataSnapshotConnection.child("dislike").hasChild(myuserID) && !dataSnapshotConnection.child("like").hasChild(myuserID) && !dataSnapshotConnection.child("matches").hasChild(myuserID)) {

                                            if(dataSnapshotusers.hasChild("city") && dataSnapshotusers.hasChild("country_code")) {

                                                if (dataSnapshotusers.child("gender").getValue().toString().equals(oppositeGender)
                                                        && (dataSnapshotusers.child("city").getValue().toString().equals(city) || dataSnapshotusers.child("country_code").getValue().toString().equals(country_code))
                                                ) {


                                                    String gender = dataSnapshotusers.child("gender").getValue().toString();
                                                    String age = dataSnapshotusers.child("age").getValue().toString();

                                                    if(dataSnapshotusers.hasChild("images"))
                                                    {
                                                        if(dataSnapshotusers.hasChild("username")&&dataSnapshotusers.hasChild("photoProfile")) {
                                                            String username = dataSnapshotusers.child("username").getValue().toString();
                                                            String profileImageUrl = dataSnapshotusers.child("photoProfile").getValue().toString();

                                                            long countImages = dataSnapshotusers.child("images").getChildrenCount();
                                                            CardMatches item = new CardMatches(dataSnapshotusers.getKey(), username, profileImageUrl, age, gender, countImages);
                                                            rowItemsMatches.add(item);
                                                            arrAdpMatches.notifyDataSetChanged();
                                                        }
                                                    }
                                                    else {

                                                        if(dataSnapshotusers.hasChild("username")&&dataSnapshotusers.hasChild("photoProfile")) {
                                                            String username = dataSnapshotusers.child("username").getValue().toString();
                                                            String profileImageUrl = dataSnapshotusers.child("photoProfile").getValue().toString();

                                                            CardMatches item = new CardMatches(dataSnapshotusers.getKey(), username, profileImageUrl, age, gender);
                                                            rowItemsMatches.add(item);
                                                            arrAdpMatches.notifyDataSetChanged();
                                                        }
                                                    }

                                                    if(rowItemsMatches.size()>0){
                                                        rippleBackground.stopRippleAnimation();
                                                        rippleBackground.setVisibility(View.GONE);
//                                                       loadMatches();
                                                    }

                                                    if (rowItemsMatches.size()==0) {
                                                        rippleBackground.stopRippleAnimation();
                                                        tvSearching.setText(getString(R.string.soory_canfind_more_match));
                                                    }
                                                }
                                            }
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }else
                                if(discovery.equals("true")){

                                    dbConnections.child(String.valueOf(dataSnapshotusers.getKey())).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshotConnection) {

                                            if (!dataSnapshotConnection.child("dislike").hasChild(myuserID) && !dataSnapshotConnection.child("like").hasChild(myuserID) && !dataSnapshotConnection.child("matches").hasChild(myuserID) ) {

                                                if(dataSnapshotusers.hasChild("city") && dataSnapshotusers.hasChild("country_code")) {

                                                    if (dataSnapshotusers.child("gender").getValue().toString().equals(show_me_setting) && dataSnapshotusers.child("age_range").getValue().toString().equals(age_setting)
                                                            && (dataSnapshotusers.child("city").getValue().toString().equals(city) || dataSnapshotusers.child("country_code").getValue().toString().equals(country_code))
                                                    ) {

                                                        String profileImageUrl = String.valueOf(dataSnapshotusers.child("photoProfile").getValue());
                                                        String username = String.valueOf(dataSnapshotusers.child("username").getValue());
                                                        String gender = String.valueOf(dataSnapshotusers.child("gender").getValue());
                                                        String age = String.valueOf(dataSnapshotusers.child("age").getValue());


                                                        if(dataSnapshotusers.hasChild("images"))
                                                        {
                                                            long countImages = dataSnapshotusers.child("images").getChildrenCount();
                                                            CardMatches item = new CardMatches(dataSnapshotusers.getKey(), username, profileImageUrl, age, gender,countImages);
                                                            rowItemsMatches.add(item);
                                                            arrAdpMatches.notifyDataSetChanged();
                                                        }
                                                        else {
                                                            CardMatches item = new CardMatches(dataSnapshotusers.getKey(), username, profileImageUrl, age, gender);
                                                            rowItemsMatches.add(item);
                                                            arrAdpMatches.notifyDataSetChanged();
                                                        }

                                                        if(rowItemsMatches.size()>0){
                                                            rippleBackground.stopRippleAnimation();
                                                            rippleBackground.setVisibility(View.GONE);
//                                                            loadMatches();
                                                        }
                                                        if (rowItemsMatches.size()==0) {
                                                            rippleBackground.stopRippleAnimation();
                                                            tvSearching.setText(getString(R.string.soory_canfind_more_match));
                                                        }
                                                    }
                                                }
                                            }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

}
