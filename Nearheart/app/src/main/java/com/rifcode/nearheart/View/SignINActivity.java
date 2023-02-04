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
import android.widget.Button;
import android.view.View;
import android.widget.EditText;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rifcode.nearheart.Utils.DialogUtils;
import com.rifcode.nearheart.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignINActivity extends AppCompatActivity {

    private Button btnGoToSignUp,cpBtnSignin;
    private static final String TAG = "SignInFragment";
    private FirebaseAuth mAuth;
    private EditText edtEmail,edtPass;
    private DatabaseReference dbRefUser;
    private ProgressDialog proDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        wedgets();

        mAuth = FirebaseAuth.getInstance();
        dbRefUser = FirebaseDatabase.getInstance().getReference().child("users");

        btnGoToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignINActivity.this,SignUPActivity.class));
                Animatoo.animateSlideLeft(SignINActivity.this);
            }
        });


        cpBtnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(TextUtils.isEmpty(edtEmail.getText()) || TextUtils.isEmpty(edtEmail.getText())){
                    try {
                        Snackbar snackbar_su = Snackbar
                                .make(cpBtnSignin, getString(R.string.help_isEmpty)
                                        , Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }catch (Exception e){}
                    return;
                }

                if(!isEmailValid(edtEmail.getText().toString())){

                    try {
                        Snackbar snackbar_su = Snackbar
                                .make(cpBtnSignin, getString(R.string.youremial_notvalid)
                                        , Snackbar.LENGTH_LONG);
                        snackbar_su.show();
                    }catch (Exception e){}
                    return;
                }
                signIN();

            }
        });

    }

    private void signIN(){

        proDialog = DialogUtils.showProgressDialog(SignINActivity.this,SignINActivity.this
                .getResources().getString(R.string.pb_wait_forsignin));
        proDialog.show();


        String email = edtEmail.getText().toString();
        String pass = edtPass.getText().toString();


            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                proDialog.dismiss();

                                    Snackbar snackbar_su3 = Snackbar
                                            .make(cpBtnSignin, getString(R.string.auth_success)
                                                    , Snackbar.LENGTH_LONG);
                                snackbar_su3.show();


                                Intent intentHome = new Intent(SignINActivity.this,MainActivity.class);
                                intentHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intentHome);

                                ////---------------- for notification ------------------//
//                                try{
//                                    String deviceTokenID = FirebaseInstanceId.getInstance().getToken();
//                                    dbRefUser.child(mAuth.getUid())
//                                            .child("device_token").setValue(deviceTokenID);
//                                }catch (Exception e){
//
//                                }

                            } else {
                                proDialog.dismiss();

                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());

                                        Snackbar snackbar_su1 = Snackbar
                                                .make(cpBtnSignin, getString(R.string.auth_failed)
                                                        , Snackbar.LENGTH_LONG);
                                snackbar_su1.show();


                                    Snackbar snackbar_su2 = Snackbar
                                            .make(cpBtnSignin, getString(R.string.help_auth)
                                                    , Snackbar.LENGTH_LONG);
                                snackbar_su2.show();
                            }

                            // ...
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

        /// button
        btnGoToSignUp = findViewById(R.id.btnGoToSignUp);
        cpBtnSignin = findViewById(R.id.btnSignIn);

        // edit text //
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignINActivity.this,FirstUseActivity.class));
        finish();
        Animatoo.animateSlideRight(SignINActivity.this);
    }
}
