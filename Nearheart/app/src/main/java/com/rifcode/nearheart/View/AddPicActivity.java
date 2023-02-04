package com.rifcode.nearheart.View;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Button;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.rifcode.nearheart.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddPicActivity extends AppCompatActivity {

    private String uripic;
    private ImageView imgvDisplay;
    private Button btnCancelAddPic,btnSaveImageAddPic;
    private ProgressBar pbLoadImageAddPic;
    private DatabaseReference dbPushImagesUser;
    private StorageReference mStorageImage;
    @SuppressLint("SimpleDateFormat")
    DateFormat dfYears = new SimpleDateFormat("dd-mm-yyyy-hh-mm-ss");
    String getDateYears = dfYears.format(Calendar.getInstance().getTime());
    private String userID;
    private DatabaseReference dbRefUser;
    private CheckBox cbPutInphotoProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pic);
        userID = FirebaseAuth.getInstance().getUid();
        mStorageImage = FirebaseStorage.getInstance().getReference();
        dbRefUser = FirebaseDatabase.getInstance().getReference().child("users")
                .child(userID);
        uripic = getIntent().getStringExtra("uri");

        widgets();
        onClickEvent();


        Picasso.get().load(uripic).networkPolicy(NetworkPolicy.OFFLINE)
                .placeholder(R.drawable.placeholder_image)
                .into(imgvDisplay, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError(Exception e) {
                        //Toast.makeText(this ,R.string.error_display_image, Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void onClickEvent() {

        btnSaveImageAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbLoadImageAddPic.setVisibility(View.VISIBLE);
                if(cbPutInphotoProfile.isChecked())
                    stockerPhotos("checked");
                else
                    stockerPhotos("nochecked");

            }
        });

        btnCancelAddPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



    }



    private void widgets() {

        imgvDisplay = findViewById(R.id.imgvdisplayAddpic);
        btnCancelAddPic = findViewById(R.id.btnCancelAddPic);
        btnSaveImageAddPic = findViewById(R.id.btnSaveImageAddPic);
        cbPutInphotoProfile = findViewById(R.id.cbPutInphotoProfile);
        pbLoadImageAddPic = findViewById(R.id.pbLoadImageAddPic);

    }

    private void stockerPhotos(final String isChecked){
        //compress image
        imgvDisplay.setDrawingCacheEnabled(true);
        imgvDisplay.buildDrawingCache();
        Bitmap bitmap = imgvDisplay.getDrawingCache();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        final byte[] data = byteArrayOutputStream.toByteArray();


        // StorageReference filePath = mStorageImage.child("all_images_user").child(userID).child(getDateYears+".jpg");
        final StorageReference thumbfilePath = mStorageImage.child("thumb_all_images_user").child(userID)
                .child(getDateYears+".jpg");

        UploadTask uploadTask = thumbfilePath.putBytes(data);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return thumbfilePath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    if (downloadUri != null) {

                        String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                        /////// send image to my profile
                        dbPushImagesUser =  dbRefUser.child("images").push();
                        dbPushImagesUser.child("thumb_picture").setValue(photoStringLink);
                        dbPushImagesUser.child("Time").setValue(-1*System.currentTimeMillis());

                        // images reviews
                        String puchKey = dbPushImagesUser.getKey();
                        HashMap<String, String> imagesReviewsMap = new HashMap<>();
                        imagesReviewsMap.put("userID", userID);
                        imagesReviewsMap.put("image", photoStringLink);
                        imagesReviewsMap.put("type", "images");
                        FirebaseDatabase.getInstance().getReference().child("images_reviews").child(puchKey).setValue(imagesReviewsMap);
                        FirebaseDatabase.getInstance().getReference().child("images_reviews").child(puchKey).child("time").setValue(-1*System.currentTimeMillis());

                        if(isChecked.equals("checked")){
                            Map updateHash_map = new HashMap<>();
                            updateHash_map.put("photoProfile", photoStringLink);

                            dbRefUser.updateChildren(updateHash_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {


                                        Toast.makeText(AddPicActivity.this, R.string.toast_image_change_succ_profile
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                        Toast.makeText(getApplicationContext(), R.string.secc_upload_photo, Toast.LENGTH_SHORT).show();
                        pbLoadImageAddPic.setVisibility(View.INVISIBLE);
                        finish();

                    }

                } else {
                    // Handle failures
                    // ...
                }
            }
        });


    }


}
