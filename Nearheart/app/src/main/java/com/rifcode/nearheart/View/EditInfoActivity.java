package com.rifcode.nearheart.View;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rifcode.nearheart.Utils.DialogUtils;
import com.rifcode.nearheart.R;
import com.rifcode.nearheart.Models.myPictures;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditInfoActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference dbRefUser;
    private ImageView imgvMan,imgvWoman,imgvAddPhotoEditInfo,imgvAddSelfieCamEditInfo;
    private String gender;
    private TextView tvMan,tvWoman;
    private StorageReference mStorageImage;
    @SuppressLint("SimpleDateFormat")
    private DateFormat dfYears = new SimpleDateFormat("dd-mm-yyyy-hh-mm-ss");
    private String userID;
    private FirebaseRecyclerAdapter<myPictures, myImagesViewHolder> firebaseRecyclerAdapter;
    private RecyclerView recyclerViewMyImgs;
    private GridLayoutManager mLayoutManager;
    private int CAMERA_REQUEST = 2;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private int MY_CAMERA_PERMISSION_CODE_Exter=200;
    private static int GALLERY_PICK=111;
    private ScrollView svEditProfile;
    private EditText edtJobEditInfo,edtAboutyouEditInfo,edtUsernameEditProfile;
    private String whyyah="";
    private CheckBox[] chkBoxs;
    private Integer[] chkBoxIds = {R.id.cbChatEditProfile, R.id.cbDateEditProfile, R.id.cbMakeNFEditProfile};
    private TextView edtLivingInEditInfo;
    private View mViewInflatedialogUploadImage;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        getSupportActionBar().setTitle(getString(R.string.edit_profile));
        wedgets();

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        mStorageImage = FirebaseStorage.getInstance().getReference();

        dbRefUser = FirebaseDatabase.getInstance().getReference().child("users");
        dbRefUser.keepSynced(true);

        imgvMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgvMan.setImageResource(R.drawable.ic_gender_male_selected);
                imgvWoman.setImageResource(R.drawable.ic_gender_female_not_selected);
                gender = "Man";
                dbRefUser.child(userID).child("gender").setValue(gender);
            }
        });

        imgvWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgvWoman.setImageResource(R.drawable.ic_gender_female_selected);
                imgvMan.setImageResource(R.drawable.ic_gender_male_not_selected);
                gender = "Woman";
                dbRefUser.child(userID).child("gender").setValue(gender);

            }
        });

        tvMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgvMan.setImageResource(R.drawable.ic_gender_male_selected);
                imgvWoman.setImageResource(R.drawable.ic_gender_female_not_selected);
                gender = "Man";
                dbRefUser.child(userID).child("gender").setValue(gender);

            }
        });

        tvWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgvWoman.setImageResource(R.drawable.ic_gender_female_selected);
                imgvMan.setImageResource(R.drawable.ic_gender_male_not_selected);
                gender = "Woman";
                dbRefUser.child(userID).child("gender").setValue(gender);
            }
        });

        imgvAddPhotoEditInfo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                readExterStoragePermission();
            }
        });

        imgvAddSelfieCamEditInfo.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                readPermCamera();
            }
        });



    }

    private void dialogUploadCamera(int num){

        mViewInflatedialogUploadImage= getLayoutInflater().inflate(R.layout.dialog_warning_uploadimage,null);
        final CheckBox cb = mViewInflatedialogUploadImage.findViewById(R.id.cbCheckImageUpload);
        Button btnupload = mViewInflatedialogUploadImage.findViewById(R.id.btnUpload);
        final AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflatedialogUploadImage,EditInfoActivity.this);
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
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
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
                                    FileProvider.getUriForFile(EditInfoActivity.this, getPackageName()+".provider",
                                            new File(mCurrentPhotoPath)));
                            startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                        }
                    }
                }else{
                    Toast.makeText(EditInfoActivity.this, getString(R.string.risk_deletaccount), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private File createImageFile() throws IOException {
        String ts = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = String.format("JPEG_%s_", ts);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(filename, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dialogUploadImage(int num){

        mViewInflatedialogUploadImage= getLayoutInflater().inflate(R.layout.dialog_warning_uploadimage,null);
        final CheckBox cb = mViewInflatedialogUploadImage.findViewById(R.id.cbCheckImageUpload);
        Button btnupload = mViewInflatedialogUploadImage.findViewById(R.id.btnUpload);
        final AlertDialog.Builder alertDialogBuilder = DialogUtils.CustomAlertDialog(mViewInflatedialogUploadImage,EditInfoActivity.this);
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
                    Toast.makeText(EditInfoActivity.this, getString(R.string.risk_deletaccount), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    private void wedgets() {

        imgvMan = findViewById(R.id.imgvManEditInfo);
        imgvWoman= findViewById(R.id.imgvWomanEditInfo);
        tvWoman= findViewById(R.id.tvWomanEditInfo);
        tvMan= findViewById(R.id.tvManEditInfo);
        svEditProfile = findViewById(R.id.svEditProfile);

        edtAboutyouEditInfo = findViewById(R.id.edtAboutyouEditInfo);
        edtJobEditInfo = findViewById(R.id.edtJobEditInfo);
        edtUsernameEditProfile = findViewById(R.id.edtUsernameEditProfile);
        edtLivingInEditInfo = findViewById(R.id.edtLivingInEditInfo);

        chkBoxs = new CheckBox[chkBoxIds.length];


        imgvAddPhotoEditInfo  = findViewById(R.id.btnAddPhotoEditInfo);
        imgvAddSelfieCamEditInfo  = findViewById(R.id.imgvAddSelfieCamEditInfo);

        // rcv
        recyclerViewMyImgs = findViewById(R.id.rvMyImagesEditInfo);
        recyclerViewMyImgs.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this,3
                , LinearLayoutManager.VERTICAL,false);
        recyclerViewMyImgs.setLayoutManager(mLayoutManager);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void readPermCamera(){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                    MY_CAMERA_PERMISSION_CODE);
        } else {
            dialogUploadCamera(CAMERA_REQUEST);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void readExterStoragePermission(){
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_CAMERA_PERMISSION_CODE_Exter);
        } else {
            //open gallery and chose image:
            dialogUploadImage(GALLERY_PICK);
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(EditInfoActivity.this, R.string.camera_perm_granted, Toast.LENGTH_LONG).show();
                dialogUploadCamera(CAMERA_REQUEST);
            } else {
                Toast.makeText(EditInfoActivity.this, R.string.camera_perms_denied, Toast.LENGTH_LONG).show();
            }

        }
        if (requestCode == MY_CAMERA_PERMISSION_CODE_Exter) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(getActivity(), R.string.camera_perm_granted, Toast.LENGTH_LONG).show();
                dialogUploadImage(GALLERY_PICK);
            }
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageUri = data.getData();
            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(imageUri)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();
                //Toast.makeText(getActivity(), resultUri.toString(), Toast.LENGTH_SHORT).show();

                Intent addpic = new Intent(this, AddPicActivity.class);
                addpic.putExtra("uri", resultUri.toString());
                startActivity(addpic);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                //Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

            Uri resultUri = Uri.fromFile(new File(mCurrentPhotoPath));
            Intent addpic = new Intent(this, AddPicActivity.class);
            addpic.putExtra("uri", resultUri.toString());
            startActivity(addpic);

        }
    }


    private void firebaseRecyclerMyImages(){

        //pbPostLoad.setVisibility(View.VISIBLE);  // To show ProgressBar
        final Query qrMyimages = dbRefUser.child(userID).child("images").orderByChild("Time");
        final DatabaseReference dbMyimages = dbRefUser.child(userID).child("images");
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<myPictures, myImagesViewHolder>(
                myPictures.class,
                R.layout.layout_myimages,
                myImagesViewHolder.class,
                qrMyimages
        ) {

            @Override
            protected void populateViewHolder(final myImagesViewHolder viewHolder, final myPictures model, int position) {

                String listPostKey = getRef(position).getKey();


                dbMyimages.child(listPostKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String thumb_picture = String.valueOf(dataSnapshot.child("thumb_picture").getValue());
                        viewHolder.setmyImage(thumb_picture,EditInfoActivity.this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                viewHolder.imgvMyImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        Intent photopostIntent = new Intent(getActivity(),PhotoPostActivity.class);
//                        photopostIntent.putExtra("myimage_click", idimageclicked);
//                        startActivity(photopostIntent);
                    }
                });

            }


        };

        recyclerViewMyImgs.setAdapter(firebaseRecyclerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /// selected items menu:
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int check = item.getItemId();
        switch(check) {

            case R.id.mnSaveProfile:
                saveInfo();
                break;
            case android.R.id.home:
                finish();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);

    }

    private void saveInfo() {
        if(TextUtils.isEmpty(edtAboutyouEditInfo.getText()) || TextUtils.isEmpty(edtJobEditInfo.getText()) || TextUtils.isEmpty(edtUsernameEditProfile.getText())){
            Toast.makeText(this, getString(R.string.help_isEmpty), Toast.LENGTH_SHORT).show();
            return;
        }

        if(edtUsernameEditProfile.getText().length()<6){
            Toast.makeText(this, getString(R.string.help_usernameShould), Toast.LENGTH_SHORT).show();
            return;
        }

        dbRefUser.child(userID).child("work").setValue(String.valueOf(edtJobEditInfo.getText()));
        dbRefUser.child(userID).child("about").setValue(String.valueOf(edtAboutyouEditInfo.getText()));
        dbRefUser.child(userID).child("username").setValue(String.valueOf(edtUsernameEditProfile.getText()));



        //StringBuilder sb = new StringBuilder();
        for(int i = 0; i < chkBoxIds.length; i++) {
            chkBoxs[i] = findViewById(chkBoxIds[i]);
            if(chkBoxs[i].isChecked()){
                whyyah = whyyah+ i;
            }
        }
        dbRefUser.child(userID).child("whyyah").setValue(whyyah).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                whyyah = "";
            }
        });

        Toast.makeText(this, getString(R.string.upda_successfully), Toast.LENGTH_SHORT).show();
    }

    private void retreiveData(){


        dbRefUser.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(userID!=null) {

                    String about = dataSnapshot.child("about").getValue().toString();
                    String username = dataSnapshot.child("username").getValue().toString();
                    String work = dataSnapshot.child("work").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    if(dataSnapshot.hasChild("city")) {
                        if(dataSnapshot.hasChild("country"))
                        {
                            String city = dataSnapshot.child("city").getValue().toString();
                            String country = dataSnapshot.child("country").getValue().toString();
                            edtLivingInEditInfo.setText(city+", "+country);
                        }
                    }

                    edtAboutyouEditInfo.setText(about);
                    edtJobEditInfo.setText(work);
                    edtUsernameEditProfile.setText(username);


                    if(dataSnapshot.hasChild("whyyah")) {

                        String whyyah = dataSnapshot.child("whyyah").getValue().toString();

                        for (int i = 0; i < chkBoxIds.length; i++) {
                            chkBoxs[i] = findViewById(chkBoxIds[i]);
                        }
                        String[] ary = whyyah.split("");
                        for (int i = 0; i < ary.length; i++) {
                            if (ary[i].equals("0"))
                                chkBoxs[0].setChecked(true);
                            if (ary[i].equals("1"))
                                chkBoxs[1].setChecked(true);
                            if (ary[i].equals("2"))
                                chkBoxs[2].setChecked(true);
                        }

                    }


                    if (gender.equals("Man")) {
                        imgvMan.setImageResource(R.drawable.ic_gender_male_selected);
                        imgvWoman.setImageResource(R.drawable.ic_gender_female_not_selected);
                    } else {
                        imgvWoman.setImageResource(R.drawable.ic_gender_female_selected);
                        imgvMan.setImageResource(R.drawable.ic_gender_male_not_selected);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static class myImagesViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imgvMyImage;

        public myImagesViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            imgvMyImage = view.findViewById(R.id.imgvMyImage);
        }


        public void setmyImage(final String ThumbImage, final Context ctx) {
            Picasso.get().load(ThumbImage).networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.portrait_placeholder).into(imgvMyImage, new Callback() {
                @Override
                public void onSuccess() {

                }
                @Override
                public void onError(Exception e) {
                    Picasso.get().load(ThumbImage).placeholder(R.drawable.placeholder_image).into(imgvMyImage);
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        svEditProfile.post(new Runnable() {
            @Override
            public void run() {
                svEditProfile.scrollTo(0, 0);
                svEditProfile.pageScroll(View.FOCUS_UP);
                svEditProfile.smoothScrollTo(0,0);
            }
        });
        retreiveData();
        firebaseRecyclerMyImages();

    }
}
