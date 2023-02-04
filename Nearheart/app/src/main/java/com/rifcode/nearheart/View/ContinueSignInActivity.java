package com.rifcode.nearheart.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.facebook.Profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rifcode.nearheart.R;
import com.rifcode.nearheart.Utils.DialogUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ContinueSignInActivity extends AppCompatActivity {

    private String iduser;
    private ImageView imgvWoman,imgvMan;
    private TextView tvWoman,tvMan;
    private Button btnNextSignUp;
    private String gender;
    private EditText edtAge;
    private TextView tvPrivacyPolicySignUP;
    private String age;
    private CheckBox cbConditionsContinue;
    private DatabaseReference dbUsers;
    private ProgressDialog proDialog;
    private FirebaseUser user;
    private String type_signin;
    private LinearLayout lyusername;

    ////// get date of mobile ////////
    @SuppressLint("SimpleDateFormat")
    DateFormat df = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
    String date = df.format(Calendar.getInstance().getTime());
    private String username;
    private EditText edtusername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_continue_sign_in);

        iduser = getIntent().getStringExtra("iduser");
        type_signin= getIntent().getStringExtra("type_signin");

        dbUsers = FirebaseDatabase.getInstance().getReference().child("users").child(iduser);
        imgvMan = findViewById(R.id.imgvManSignupContinue);
        imgvWoman= findViewById(R.id.imgvWomanSignupContinue);
        cbConditionsContinue= findViewById(R.id.cbConditionsContinue);
        lyusername= findViewById(R.id.lyusername);
        edtusername= findViewById(R.id.edtUsernameSignUpContinue);

        tvWoman= findViewById(R.id.tvWomanContinue);
        tvMan= findViewById(R.id.tvMenContinue);
        imgvWoman= findViewById(R.id.imgvWomanSignupContinue);
        btnNextSignUp= findViewById(R.id.btnNextSignUp);
        tvPrivacyPolicySignUP= findViewById(R.id.tvPrivacyPolicySignUPContinue);
        // edit text //
        edtAge = findViewById(R.id.edtAgeSignUpContinue);

        if(type_signin.equals("phone")){
            lyusername.setVisibility(View.VISIBLE);
        }else{
            lyusername.setVisibility(View.GONE);
        }

        imgvMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgvMan.setImageResource(R.drawable.ic_gender_male_selected);
                imgvWoman.setImageResource(R.drawable.ic_gender_female_not_selected);
                gender = "Man";
            }
        });

        tvPrivacyPolicySignUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pp =  new Intent(ContinueSignInActivity.this,PrivacyPolicyActivity.class);
                pp.putExtra("file","privacy_policy");
                startActivity(pp);
            }
        });

        imgvWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgvWoman.setImageResource(R.drawable.ic_gender_female_selected);
                imgvMan.setImageResource(R.drawable.ic_gender_male_not_selected);
                gender = "Woman";
            }
        });

        tvMan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgvMan.setImageResource(R.drawable.ic_gender_male_selected);
                imgvWoman.setImageResource(R.drawable.ic_gender_female_not_selected);
                gender = "Man";
            }
        });

        tvWoman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgvWoman.setImageResource(R.drawable.ic_gender_female_selected);
                imgvMan.setImageResource(R.drawable.ic_gender_male_not_selected);
                gender = "Woman";
            }
        });


        btnNextSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                age=edtAge.getText().toString();

                if(type_signin.equals("phone")){
                    username=edtusername.getText().toString();
                    if(TextUtils.isEmpty(username)) {

                        try {
                            Snackbar snackbar_su = Snackbar
                                    .make(btnNextSignUp,getString(R.string.help_isEmpty)
                                            , Snackbar.LENGTH_LONG);
                            snackbar_su.show();
                        }catch (Exception e){

                        }
                        return;
                    }

                }

                // Disabled visibility layout //
                if(TextUtils.isEmpty(age) || TextUtils.isEmpty(gender)) {

                    try {
                        Snackbar snackbar_su = Snackbar
                                .make(btnNextSignUp,getString(R.string.help_isEmpty)
                                        , Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }catch (Exception e){

                    }
                    return;
                }

                if(Integer.parseInt(edtAge.getText().toString()) < 18){

                    try {

                        Snackbar snackbar_su = Snackbar
                                .make(btnNextSignUp, getString(R.string.un18)
                                        , Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }catch (Exception e){}
                    return;
                }

                if(Integer.parseInt(edtAge.getText().toString()) > 70){

                    try {

                        Snackbar snackbar_su = Snackbar
                                .make(btnNextSignUp, getString(R.string.up70)
                                        , Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }catch (Exception e){}
                    return;
                }

                if(!cbConditionsContinue.isChecked()){
                    try{
                        Snackbar snackbar_su = Snackbar
                                .make(btnNextSignUp,getString(R.string.help_conditions)
                                        , Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }catch (Exception e){}

                    return;
                }

                // update
                try{
                    proDialog = DialogUtils.showProgressDialog(ContinueSignInActivity.this,ContinueSignInActivity.this
                            .getResources().getString(R.string.pb_wait_forsignup));
                    proDialog.show();

                }catch (Exception e){}

                signUp();
            }
        });



}

    private void signUp() {

        HashMap<String, String> userMap = new HashMap<>();

        ////---------------- for notification request friend ------------------//
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {

                            String token = task.getResult();
                            userMap.put("device_token", token);


                        }
                    }
                });
        /////////////////////////////////////
        if(type_signin.equals("phone")){
            lyusername.setVisibility(View.VISIBLE);
            userMap.put("type_account", "phone");
            userMap.put("numberPhone", SignInPhoneActivity.edtNumberPhoneSigninPhone.getText().toString());
            userMap.put("username", username);
            userMap.put("email", "---");
            userMap.put("codeCountryPhone", SignInPhoneActivity.codeCountry);

        }else{
            lyusername.setVisibility(View.GONE);
            userMap.put("type_account", "facebook");
            userMap.put("numberPhone", "---");
            userMap.put("codeCountryPhone", "---");
        }
        userMap.put("online","false");
        userMap.put("rateApp","false");
        userMap.put("age",age);
        userMap.put("gender",gender);
        userMap.put("about", "---");
        userMap.put("work", "---");
        userMap.put("joined_nearheart", date);
        userMap.put("school", "---");
        userMap.put("discovery", "false");
        userMap.put("purchase", "false");

        dbUsers.setValue(userMap);
        proDialog.dismiss();

        int intage = Integer.parseInt(age);
        if (intage < 18)
            age = "18-24";
        else if (intage <= 24)
            age = "18-24";
        else if (intage <= 35)
            age = "25-35";
        else if (intage <= 46)
            age = "36-46";
        else
            age = "47-";

        dbUsers.child("age_range").setValue(age);
        dbUsers.child("number").setValue(-1*System.currentTimeMillis());


        getDataFromFacebook();
        try {
            Toast.makeText(ContinueSignInActivity.this, getString(R.string.auth_success), Toast.LENGTH_SHORT).show();
            Intent intentHome = new Intent(ContinueSignInActivity.this, MainActivity.class);
            //intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentHome);
            ContinueSignInActivity.this.finish();
        }catch (Exception e) {}

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(ContinueSignInActivity.this);
    }

    private void getDataFromFacebook(){
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(Profile.getCurrentProfile()!=null) {
            // Name, email address, and profile photo Url
            Profile profile1 = Profile.getCurrentProfile();
            for (UserInfo profiloo : user.getProviderData()) {

                final String email = profiloo.getEmail();
                String name = profile1.getFirstName();
                final String photoUrl = profile1.getProfilePictureUri(200, 200).toString();


                dbUsers.child("username").setValue(name);
                dbUsers.child("thumbPhotoProfile").setValue(photoUrl);
                dbUsers.child("purchase").setValue("false");
                dbUsers.child("email").setValue(email);
                dbUsers.child("photoProfile").setValue(photoUrl);
                proDialog.dismiss();

            }
        }

    }

}
