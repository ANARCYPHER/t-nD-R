package com.rifcode.nearheart.View;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rifcode.nearheart.Utils.DialogUtils;
import com.rifcode.nearheart.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUPActivity extends AppCompatActivity {
    private static final String TAG = "SignUPActivity";

    private ImageView imgvWoman,imgvMan;
    private String gender;
    private Button btnSignUp;
    private String username,email,pass,age;
    private FirebaseAuth mAuth;
    private DatabaseReference dbRefUser;
    private CheckBox cbCondition;
    ProgressDialog proDialog;

    ////// get date of mobile ////////
    @SuppressLint("SimpleDateFormat")
    DateFormat df = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
    String date = df.format(Calendar.getInstance().getTime());

    //edit text //
    private EditText edtAge,edtUsername,edtemail,edtpass;
    private String isNotMatch = "true";
    private TextView tvWoman,tvMan,tvPrivacyPolicySignUP;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        wedgets();
        mAuth = FirebaseAuth.getInstance();
        dbRefUser = FirebaseDatabase.getInstance().getReference().child("users");
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
                Intent pp =  new Intent(SignUPActivity.this,PrivacyPolicyActivity.class);
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


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                declareEditTextAndSpinner();
                isNotMatch = "true";
                // Disabled visibility layout //
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(age)
                         || TextUtils.isEmpty(username) || TextUtils.isEmpty(gender)
                ) {

                    try {
                        Snackbar snackbar_su = Snackbar
                                .make(btnSignUp,getString(R.string.help_isEmpty)
                                        , Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }catch (Exception e){

                    }
                    return;
                }

                if(Integer.parseInt(edtAge.getText().toString()) < 18){

                    try {

                        Snackbar snackbar_su = Snackbar
                                .make(btnSignUp, getString(R.string.un18)
                                        , Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }catch (Exception e){}
                    return;
                }

                if(Integer.parseInt(edtAge.getText().toString()) > 70){

                    try {

                        Snackbar snackbar_su = Snackbar
                                .make(btnSignUp, getString(R.string.up70)
                                        , Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }catch (Exception e){}
                    return;
                }

                if(checkNumbers(edtUsername.getText().toString())){

                    try {

                        Snackbar snackbar_su = Snackbar
                                .make(btnSignUp, getString(R.string.check_username_digi)
                                        , Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }catch (Exception e){}
                    return;
                }

                if(checkSpecialCharacters(edtUsername.getText().toString())){
                    try {
                        Snackbar snackbar_su = Snackbar
                                .make(btnSignUp, getString(R.string.check_dymbol_username)
                                        , Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }catch (Exception e){}
                    return;
                }

                if(edtpass.getText().length() < 6){

                    try {

                        Snackbar snackbar_su = Snackbar
                                .make(btnSignUp,getString(R.string.help_passwordShould)
                                        , Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }catch (Exception e){}
                    return;
                }

                if(edtUsername.getText().length() < 6){

                    try {

                        Snackbar snackbar_su = Snackbar
                                .make(btnSignUp,getString(R.string.help_usernameShould)
                                        , Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }catch (Exception e){}
                    return;
                }

                if(!cbCondition.isChecked()){

                    try{
                        Snackbar snackbar_su = Snackbar
                                .make(btnSignUp,getString(R.string.help_conditions)
                                        , Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }catch (Exception e){}
                    return;
                }


                if(!isEmailValid(edtemail.getText().toString()))
                {
                    Toast.makeText(SignUPActivity.this, getString(R.string.youremial_notvalid), Toast.LENGTH_SHORT).show();
                    return;
                }
                signUp();
            }
        });
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private void wedgets() {

        imgvMan = findViewById(R.id.imgvManSignup);
        imgvWoman= findViewById(R.id.imgvWomanSignup);
        tvWoman= findViewById(R.id.tvWoman);
        tvMan= findViewById(R.id.tvMen);
        imgvWoman= findViewById(R.id.imgvWomanSignup);
        btnSignUp= findViewById(R.id.btnSignUp);
        cbCondition= findViewById(R.id.cbConditions);
        tvPrivacyPolicySignUP= findViewById(R.id.tvPrivacyPolicySignUP);
        // edit text //
        edtAge = findViewById(R.id.edtAgeSignUp);
        edtUsername = findViewById(R.id.edtUsernameSignUp);
        edtemail = findViewById(R.id.edtEmailSignUp);
        edtpass = findViewById(R.id.edtPassSignUp);
    }

    private boolean checkSpecialCharacters(String username){
        Pattern regex = Pattern.compile("[$&+,:;=\\\\?@#|/'<>.^*()%!-]");

        if (regex.matcher(username).find()) {
            Log.d("checkSpecialCharacters", "SPECIAL CHARS FOUND");
            return true;
        }else{
            return false;
        }
    }

    private boolean checkNumbers(String username){
        Pattern regex = Pattern.compile("[0123456789]");

        if (regex.matcher(username).find()) {
            Log.d("checkSpecialCharacters", "Numbers FOUND");
            return true;
        }else{
            return false;
        }
    }

    private void declareEditTextAndSpinner(){
        /////// declare widgets ///

        email = edtemail.getText().toString();
        pass = edtpass.getText().toString();
        age = edtAge.getText().toString();
        username = edtUsername.getText().toString();
    }


    ////////////// sign up ///////////////////
    private void signUp(){
        declareEditTextAndSpinner();

        // update
        try{
            proDialog = DialogUtils.showProgressDialog(SignUPActivity.this,SignUPActivity.this
                    .getResources().getString(R.string.pb_wait_forsignup));
            proDialog.show();

        }catch (Exception e){}

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String userID = user.getUid();

                            dbRefUser = FirebaseDatabase.getInstance().getReference().child("users")
                                    .child(userID);


                            HashMap<String, String> userMap = new HashMap<>();

                            ////---------------- for notification request friend ------------------//
//                            String deviceTokenID = FirebaseInstanceId.getInstance().getToken();
//                            userMap.put("device_token", deviceTokenID);
                            /////////////////////////////////////
                            userMap.put("email", email);
                            userMap.put("username", username);
                            userMap.put("password", pass);
                            userMap.put("numberPhone", "---");
                            userMap.put("codeCountryPhone", "---");
                            userMap.put("online","false");
                            userMap.put("rateApp","false");
                            userMap.put("age",age);
                            userMap.put("gender",gender);
                            userMap.put("about", "---");
                            userMap.put("thumbPhotoProfile", "none");
                            userMap.put("photoProfile", "none");
                            userMap.put("work", "---");
                            userMap.put("joined_nearheart", date);
                            userMap.put("school", "---");
                            userMap.put("discovery", "false");
                            userMap.put("type_account", "email");
                            userMap.put("purchase", "false");
//                            userMap.put("language_app", Locale.getDefault().getLanguage());
//                            userMap.put("state_app", "start");

                            dbRefUser.setValue(userMap);
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

                            dbRefUser.child("age_range").setValue(age);
                            dbRefUser.child("number").setValue(-1*System.currentTimeMillis());


                            try {
                                Toast.makeText(SignUPActivity.this, getString(R.string.auth_success), Toast.LENGTH_SHORT).show();
                                Intent intentHome = new Intent(SignUPActivity.this, MainActivity.class);
                                //intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intentHome);
                                SignUPActivity.this.finish();
                            }catch (Exception e) {}


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            try {
                                Toast.makeText(SignUPActivity.this,R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();
                            }catch (Exception e) {}

                            proDialog.dismiss();
                        }

                        // ...
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(SignUPActivity.this);
    }
}
