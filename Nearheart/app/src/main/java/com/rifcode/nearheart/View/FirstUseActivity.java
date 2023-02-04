package com.rifcode.nearheart.View;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rifcode.nearheart.R;

import java.util.Arrays;
import java.util.HashMap;

public class FirstUseActivity extends AppCompatActivity {

    private TextView btnPrivacyPolicy;
    private Button btnSignup,btnSignin,btn_fb_login,btn_phone_login;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private ProgressDialog proDial;
    private String TAG;
    private DatabaseReference dbUsers;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstusing);

        btn_fb_login = findViewById(R.id.btn_fb_login);

        btn_phone_login = findViewById(R.id.btn_phone_login);
// progress Dialog :
        proDial = new ProgressDialog(FirstUseActivity.this);
        proDial.setMessage(getString(R.string.wsin));
        proDial.setCanceledOnTouchOutside(false);

        dbUsers = FirebaseDatabase.getInstance().getReference().child("users");
        mAuth =  FirebaseAuth.getInstance();

        // facebook login
        FacebookSdk.sdkInitialize(getApplicationContext());

        mCallbackManager = CallbackManager.Factory.create();


        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        handleFacebookAccessToken(loginResult.getAccessToken(),loginResult);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(FirstUseActivity.this, getString(R.string.lcancl), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.w("FacebookException:", exception.getMessage());
                        Toast.makeText(FirstUseActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

        btn_fb_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(FirstUseActivity.this
                        , Arrays.asList("public_profile","email"));
            }
        });

        // sign in wuth phone number
        btn_phone_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent phoneSignin = new Intent(FirstUseActivity.this,SignInPhoneActivity.class);
                startActivity(phoneSignin);
                Animatoo.animateSlideLeft(FirstUseActivity.this);
            }
        });

//       code for generete hash key
//        PackageInfo info;
//        try {
//            info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String something = new String(Base64.encode(md.digest(), 0));
//                //String something = new String(Base64.encodeBytes(md.digest()));
//                Log.e("hash key", something);
//            }
//        } catch (PackageManager.NameNotFoundException e1) {
//            Log.e("name not found", e1.toString());
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("no such an algorithm", e.toString());
//        } catch (Exception e) {
//            Log.e("exception", e.toString());
//        }




        if(mAuth.getCurrentUser() != null){
            // if user already sign up //
            Intent startIntent = new Intent(FirstUseActivity.this,MainActivity.class);
            startActivity(startIntent);
            finish();
        }

        wedgets();

        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstUseActivity.this,SignINActivity.class));
                Animatoo.animateSlideLeft(FirstUseActivity.this);
            }
        });

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(FirstUseActivity.this,SignUPActivity.class));
                Animatoo.animateSlideLeft(FirstUseActivity.this);

            }
        });

        btnPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent pp =  new Intent(FirstUseActivity.this,PrivacyPolicyActivity.class);
                pp.putExtra("file","privacy_policy");
                startActivity(pp);
                Animatoo.animateSlideLeft(FirstUseActivity.this);
            }
        });

    }




    private void wedgets() {
        btnPrivacyPolicy = findViewById(R.id.btnPrivacyPolicyFirstUsing);
        btnSignin = findViewById(R.id.btnSigninFirstUsing);
        btnSignup = findViewById(R.id.btnSignupFirstUsing);
    }

    @Override
    public void onBackPressed() {

        /////////////// that for exit app ////////////////////
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);

    }

    private void handleFacebookAccessToken(AccessToken token, final LoginResult result) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            user = FirebaseAuth.getInstance().getCurrentUser();
                            proDial.show();
                            dbUsers.child(mAuth.getUid()).child("type_account").setValue("facebook")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            Toast.makeText(FirstUseActivity.this, R.string.regsecc, Toast.LENGTH_SHORT).show();

                                            ////---------------- Token device for notification ------------------//
                                            FirebaseMessaging.getInstance().getToken()
                                                    .addOnCompleteListener(new OnCompleteListener<String>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<String> task) {
                                                            if (task.isSuccessful()) {

                                                                String token = task.getResult();
                                                                String currentUser = mAuth.getCurrentUser().getUid();

                                                                dbUsers.child(currentUser).child("device_token").setValue(token)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {

                                                                                Intent continueintent = new Intent(FirstUseActivity.this, ContinueSignInActivity.class);
                                                                                continueintent.putExtra("iduser",currentUser);
                                                                                continueintent.putExtra("type_signin","facebook");
                                                                                startActivity(continueintent);
                                                                                Animatoo.animateSlideLeft(FirstUseActivity.this);

                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    });


                                            ////--------------------- end token ------------------------//
                                        }
                                    });

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(FirstUseActivity.this, getString(R.string.dbauthf),
                                    Toast.LENGTH_SHORT).show();
                        }
                        //...
                    }
                });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //facebook  login
        if(mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }

    }




}
