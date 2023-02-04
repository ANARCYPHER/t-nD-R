package com.rifcode.nearheart.View;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rifcode.nearheart.Utils.DialogUtils;
import com.rifcode.nearheart.R;
import com.rifcode.nearheart.Utils.ImageOrientation;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.BreakIterator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;


public class MyProfileFragment extends Fragment {

    private String mCurrentPhotoPath;
    private DatabaseReference dbPushImagesUser;

    private static final int MY_PERMISSION_CODE_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 5;
    private static final int CAMERA_REQUEST = 6;
    private static final int GALLERY_PICK = 2;
    private static final int GALLERY_PICK_TWO = 11;
    private static final int MY_CAMERA_PERMISSION_CODE_TWO = 12;
    private static final int CAMERA_REQUEST_TWO = 13;
    private View mfview;
    private ImageView imgvMySetting,imgvAddNewPicProfile;
    private ImageView imgvAddmediaMyprofile;
    private ImageView imgvEditMyProfile;
    private View mViewInflateAddmedia;
    private DatabaseReference dbUsers;
    private String userID;
    private FirebaseAuth mAuth;
    private ImageView civProfilePhoto;
    private TextView tvStatueMyProfile,tvUsernameMyProfile,tvAgeMyProfile;
    private StorageReference mStorageImage;
    @SuppressLint("SimpleDateFormat")
    private DateFormat dfYears = new SimpleDateFormat("dd-mm-yyyy-hh-mm-ss");
    private String getDateYears = dfYears.format(Calendar.getInstance().getTime());
    private Uri resultUri;
    private ProgressBar pbLoadImageProfile;
    private AlertDialog alertDialog;
    private static final int MY_PERMISSION_CODE_READ_EXTERNAL_STORAGE_TWO = 10;
    private View mViewInflatedialogUploadImage;
    private TextView tvJoinedNearheartMyProfile;
    private Bitmap resizedBitmapNew;

    private AdLoader adLoaderMeduim;
    private Handler handler;
    private TemplateView template;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mfview =  inflater.inflate(R.layout.fragment_my_profile, container, false);
        wedgets(mfview);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        mStorageImage = FirebaseStorage.getInstance().getReference();

        dbUsers = FirebaseDatabase.getInstance().getReference().child("users")
                .child(userID);

        imgvMySetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SettingActivity.class));
            }
        });


        imgvEditMyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditInfoActivity.class));
            }
        });

        imgvAddmediaMyprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddMedia("no_onclick_photo_profile");
            }
        });


        imgvAddNewPicProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddMedia("photo_profile");
            }
        });

        civProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileintent = new Intent(getContext(), ProfileUserActivity.class);
                profileintent.putExtra("userIDvisited",userID);
                startActivity(profileintent);
                Animatoo.animateZoom(getContext());
            }
        });



        // native ads admob :
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // native ads admob :
                adLoaderMeduim  = new AdLoader.Builder(getActivity(), getString(R.string.NativeAdmobID))
                        .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                            @Override
                            public void onNativeAdLoaded(NativeAd nativeAd) {
                                NativeTemplateStyle styles = new
                                        NativeTemplateStyle.Builder().build();
                                template.setStyles(styles);
                                template.setNativeAd(nativeAd);
                            }
                        })
                        .build();
                adLoaderMeduim.loadAd(new AdRequest.Builder().build());

            }
        }, 1000);

        return mfview;
    }


    @Override
    public void onStart() {
        super.onStart();
        retreiveDataFromFirebase();

    }

    private void dialogAddMedia(final String btnStatut){
        mViewInflateAddmedia = getLayoutInflater().inflate(R.layout.dialog_addmedia,null);

        ImageView imgvAddSelfieCamDialogAddMedia =  mViewInflateAddmedia.findViewById(R.id.imgvAddSelfieCamDialogAddMedia);
        ImageView imgvAddPhotoDialogAddMedia = mViewInflateAddmedia.findViewById(R.id.imgvAddPhotoDialogAddMedia);
        ImageView imgvExitdialogAddMedia = mViewInflateAddmedia.findViewById(R.id.imgvExitdialogAddMedia);


        AlertDialog.Builder alertDialogBuilderpost = DialogUtils.CustomAlertDialog(mViewInflateAddmedia,getActivity());
        alertDialog = alertDialogBuilderpost.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationENTER; //style id
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        alertDialog.show();

        imgvAddPhotoDialogAddMedia.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                readExterStoragePermission(btnStatut);
            }
        });

        imgvAddSelfieCamDialogAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readPermCamera(btnStatut);
            }
        });

        imgvExitdialogAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }

    private void dialogUploadCamera(int num){

        mViewInflatedialogUploadImage= getLayoutInflater().inflate(R.layout.dialog_warning_uploadimage,null);
        final CheckBox cb = mViewInflatedialogUploadImage.findViewById(R.id.cbCheckImageUpload);
        Button btnupload = mViewInflatedialogUploadImage.findViewById(R.id.btnUpload);
        final AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflatedialogUploadImage,getActivity());
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb.isChecked()){
                    /// open galery :
                    alertDialog.dismiss();

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // Ensure that there's a camera activity to handle the intent
                    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File

                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    FileProvider.getUriForFile(getActivity(), getActivity().getPackageName()+".provider",
                                            new File(mCurrentPhotoPath)));
                            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                        }
                    }

                }else{
                    Toast.makeText(getActivity(), getString(R.string.risk_deletaccount), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private File createImageFile() throws IOException {
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = String.format("JPEG_%s_", ts);
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(filename, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dialogUploadImage(int num){

        mViewInflatedialogUploadImage= getLayoutInflater().inflate(R.layout.dialog_warning_uploadimage,null);
        final CheckBox cb = mViewInflatedialogUploadImage.findViewById(R.id.cbCheckImageUpload);
        Button btnupload = mViewInflatedialogUploadImage.findViewById(R.id.btnUpload);
        final AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflatedialogUploadImage,getActivity());
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        btnupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cb.isChecked()){
                    /// open galery :
                    alertDialog.dismiss();
                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(galleryIntent,getString(R.string.select_gallery)), num);
                }else{
                    Toast.makeText(getActivity(), getString(R.string.risk_deletaccount), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void readExterStoragePermission(String btnStatut){

        // that for select photo profile withouth cropimage
        if(btnStatut.equals("photo_profile")){
            if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSION_CODE_READ_EXTERNAL_STORAGE_TWO);
            } else {
                //open gallery and chose image:
              dialogUploadImage(GALLERY_PICK_TWO);
            }
        }else
        {
            if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSION_CODE_READ_EXTERNAL_STORAGE);
            } else {
                //open gallery and chose image:
                dialogUploadImage(GALLERY_PICK);
            }
        }
        alertDialog.dismiss();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), R.string.camera_perm_granted, Toast.LENGTH_LONG).show();
                dialogUploadCamera(CAMERA_REQUEST);
            } else {
                Toast.makeText(getActivity(), R.string.camera_perms_denied, Toast.LENGTH_LONG).show();
            }

        }

        if (requestCode == MY_CAMERA_PERMISSION_CODE_TWO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), R.string.camera_perm_granted, Toast.LENGTH_LONG).show();
                dialogUploadCamera(CAMERA_REQUEST_TWO);
            } else {
                Toast.makeText(getActivity(), R.string.camera_perms_denied, Toast.LENGTH_LONG).show();
            }

        }

        if (requestCode == MY_PERMISSION_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(getActivity(), R.string.camera_perm_granted, Toast.LENGTH_LONG).show();
               dialogUploadImage(GALLERY_PICK);
            }
        }


        if (requestCode == MY_PERMISSION_CODE_READ_EXTERNAL_STORAGE_TWO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(getActivity(), R.string.camera_perm_granted, Toast.LENGTH_LONG).show();
                dialogUploadImage(GALLERY_PICK_TWO);
            }
        }

    }



    private void wedgets(View mfview) {
        imgvMySetting = mfview.findViewById(R.id.imgvMySetting);
        imgvAddmediaMyprofile = mfview.findViewById(R.id.imgvAddmediaMyprofile);
        imgvEditMyProfile = mfview.findViewById(R.id.imgvEditMyProfile);
        civProfilePhoto = mfview.findViewById(R.id.civProfilePhoto);
        tvUsernameMyProfile = mfview.findViewById(R.id.tvUsernameMyProfile);
        tvAgeMyProfile = mfview.findViewById(R.id.tvAgeMyProfile);
        tvStatueMyProfile = mfview.findViewById(R.id.tvStatueMyProfile);
        pbLoadImageProfile = mfview.findViewById(R.id.pbLoadPhoto);
        imgvAddNewPicProfile = mfview.findViewById(R.id.imgvAddNewPicProfile);
        tvJoinedNearheartMyProfile = mfview.findViewById(R.id.tvJoinedNearheartMyProfile);
        template  = mfview.findViewById(R.id.my_template_profile);

    }



    private void retreiveDataFromFirebase(){


        dbUsers.keepSynced(true);

        dbUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("username")) {
                    String username = dataSnapshot.child("username").getValue().toString();
                    tvUsernameMyProfile.setText(username);
                }


                if(dataSnapshot.hasChild("joined_nearheart")) {
                    String joined_nearheart = dataSnapshot.child("joined_nearheart").getValue().toString();
                    tvJoinedNearheartMyProfile.setText(joined_nearheart);
                }

                if(dataSnapshot.hasChild("age")) {
                    String age = dataSnapshot.child("age").getValue().toString();
                    tvAgeMyProfile.setText(age);
                }

                if(dataSnapshot.hasChild("about")) {
                    String about = dataSnapshot.child("about").getValue().toString();
                    tvStatueMyProfile.setText(about);
                }

                if(dataSnapshot.hasChild("photoProfile") && dataSnapshot.hasChild("gender")) {
                    String photoProfile = dataSnapshot.child("photoProfile").getValue().toString();
                    String sex = dataSnapshot.child("gender").getValue().toString();
                    getImageProfile(sex,photoProfile);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void readPermCamera(String btnStatut){

        if(btnStatut.equals("photo_profile")){
            if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        MY_CAMERA_PERMISSION_CODE_TWO);
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File

                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                FileProvider.getUriForFile(getActivity(), getActivity().getPackageName()+".provider",
                                        new File(mCurrentPhotoPath)));
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST_TWO);
                    }
                }

            }
        }else{
            if (ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        MY_CAMERA_PERMISSION_CODE);
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File

                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                FileProvider.getUriForFile(getActivity(), getActivity().getPackageName()+".provider",
                                        new File(mCurrentPhotoPath)));
                        startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                    }
                }
            }
        }

        alertDialog.dismiss();

    }

    public void getImageProfile(final String sex,final String image){

        if(!image.equals("none") && sex.equals("Man")) {

            //// Offline Capabilities: networkPolicy(NetworkPolicy.OFFLINE)
            Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_male_selected).into(civProfilePhoto, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(image).placeholder(R.drawable.ic_gender_male_selected)
                            .into(civProfilePhoto);
                }
            });

        }else
        if(!image.equals("none") && sex.equals("Woman")){

            Picasso.get().load(image).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.ic_gender_female_selected).into(civProfilePhoto, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(image).placeholder(R.drawable.ic_gender_female_selected)
                            .into(civProfilePhoto);
                }
            });

        }if(image.equals("none") && sex.equals("Woman")){

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
        if(image.equals("none") && sex.equals("Man")){
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageUri)
                    .start(getContext(), this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                alertDialog.dismiss();
                //Toast.makeText(getActivity(), resultUri.toString(), Toast.LENGTH_SHORT).show();
                Intent addpic = new Intent(getActivity(), AddPicActivity.class);
                addpic.putExtra("uri",result.getUri().toString());
                startActivity(addpic);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                alertDialog.dismiss();
                //Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            Uri resultUri = Uri.fromFile(new File(mCurrentPhotoPath));
            Intent addpic = new Intent(getActivity(),AddPicActivity.class);
            addpic.putExtra("uri",resultUri.toString());
            startActivity(addpic);
            alertDialog.dismiss();

        }

        // if i want just add photo profile //
        if(requestCode == GALLERY_PICK_TWO && resultCode == RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            civProfilePhoto.setImageURI(resultUri);
            pbLoadImageProfile.setVisibility(View.VISIBLE);


            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data1 = baos.toByteArray();

            final StorageReference filePath = mStorageImage
                    .child("all_images_user")
                    .child(userID)
                    .child(getDateYears + ".jpg");


            UploadTask uploadTask = filePath.putBytes(data1);

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

                            final String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                            Map updateHash_map = new HashMap<>();
                            updateHash_map.put("photoProfile", photoStringLink);

                            /////// send image to my profile
                            dbPushImagesUser =  dbUsers.child("images").push();
                            dbPushImagesUser.child("thumb_picture").setValue(photoStringLink);
                            dbPushImagesUser.child("Time").setValue(-1*System.currentTimeMillis());

                            // images reviews
                            String puchKey = dbPushImagesUser.getKey();
                            HashMap<String, String> imagesReviewsMap = new HashMap<>();
                            imagesReviewsMap.put("userID", userID);
                            imagesReviewsMap.put("image", photoStringLink);
                            imagesReviewsMap.put("type", "photo_profile");
                            FirebaseDatabase.getInstance().getReference().child("images_reviews").child(puchKey).setValue(imagesReviewsMap);
                            FirebaseDatabase.getInstance().getReference().child("images_reviews").child(puchKey).child("time").setValue(-1*System.currentTimeMillis());


                            dbUsers.updateChildren(updateHash_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), R.string.toast_image_change_succ_profile
                                                , Toast.LENGTH_SHORT).show();

                                        pbLoadImageProfile.setVisibility(View.INVISIBLE);

                                        alertDialog.dismiss();
                                        onStart();
                                    }
                                }
                            });

                        }

                    } else {
                        pbLoadImageProfile.setVisibility(View.INVISIBLE);
                        alertDialog.dismiss();
                    }
                }
            });

        }
        if (requestCode == CAMERA_REQUEST_TWO && resultCode == RESULT_OK) {

            final Uri imageUri = data.getData();
            resultUri = imageUri;
            civProfilePhoto.setImageURI(resultUri);
            pbLoadImageProfile.setVisibility(View.VISIBLE);

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = 2;
            options.inJustDecodeBounds = false;
            options.inTempStorage = new byte[16 * 1024];

            Bitmap bmp = BitmapFactory.decodeFile(mCurrentPhotoPath,options);
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bmp, 960, 730, false);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Uri uriImage = Uri.fromFile(new File(mCurrentPhotoPath));
            try {
                resizedBitmapNew = ImageOrientation.modifyOrientation(getActivity(),resizedBitmap,uriImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            resizedBitmapNew.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();

            final StorageReference filePath = mStorageImage
                    .child("all_images_user")
                    .child(userID)
                    .child(getDateYears + ".jpg");


            UploadTask uploadTask = filePath.putBytes(imageBytes);

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

                            final String photoStringLink = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                            Map updateHash_map = new HashMap<>();
                            updateHash_map.put("photoProfile", photoStringLink);

                            /////// send image to my profile
                            dbPushImagesUser =  dbUsers.child("images").push();
                            dbPushImagesUser.child("thumb_picture").setValue(photoStringLink);
                            dbPushImagesUser.child("Time").setValue(-1*System.currentTimeMillis());

                            // images reviews
                            String puchKey = dbPushImagesUser.getKey();
                            HashMap<String, String> imagesReviewsMap = new HashMap<>();
                            imagesReviewsMap.put("userID", userID);
                            imagesReviewsMap.put("image", photoStringLink);
                            imagesReviewsMap.put("type", "photo_profile");
                            FirebaseDatabase.getInstance().getReference().child("images_reviews").child(puchKey).setValue(imagesReviewsMap);
                            FirebaseDatabase.getInstance().getReference().child("images_reviews").child(puchKey).child("time").setValue(-1*System.currentTimeMillis());



                            dbUsers.updateChildren(updateHash_map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), R.string.toast_image_change_succ_profile
                                                , Toast.LENGTH_SHORT).show();

                                        pbLoadImageProfile.setVisibility(View.INVISIBLE);

                                        alertDialog.dismiss();
                                        onStart();
                                    }
                                }
                            });

                        }

                    } else {
                        pbLoadImageProfile.setVisibility(View.INVISIBLE);
                        alertDialog.dismiss();
                    }
                }
            });
        }

    }

}
