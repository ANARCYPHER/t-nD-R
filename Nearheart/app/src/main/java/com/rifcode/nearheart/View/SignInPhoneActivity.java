package com.rifcode.nearheart.View;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.rifcode.nearheart.R;
import com.rifcode.nearheart.Utils.DialogUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;

public class SignInPhoneActivity extends AppCompatActivity {

    public static EditText edtNumberPhoneSigninPhone;
    private EditText edtVerifyNumberPhoneSigninPhone;
    private Button btnSendPhoneSignInPhone,btnVerifyPhoneSignInPhone;
    private LinearLayout lyvirifyCodephone;
    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private String TAG;
    private FirebaseAuth fbAuth;
    private ProgressDialog proDialog;
    private CountryCodePicker ccpPhoneNumberSigninPhone;
    public static String codeCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_phone);

        edtNumberPhoneSigninPhone = findViewById(R.id.edtNumberPhoneSigninPhone);
        edtVerifyNumberPhoneSigninPhone = findViewById(R.id.edtVerifyNumberPhoneSigninPhone);
        btnSendPhoneSignInPhone = findViewById(R.id.btnSendPhoneSignInPhone);
        btnVerifyPhoneSignInPhone = findViewById(R.id.btnVerifyPhoneSignInPhone);
        lyvirifyCodephone = findViewById(R.id.lyvirifyCodephone);
        ccpPhoneNumberSigninPhone = findViewById(R.id.ccpPhoneNumberSigninPhone);

        fbAuth = FirebaseAuth.getInstance();

        try{
            proDialog = DialogUtils.showProgressDialog(SignInPhoneActivity.this,getString(R.string.verdff));

        }catch (Exception e){}


        mCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        proDialog.dismiss();

                        Toast.makeText(SignInPhoneActivity.this, getString(R.string.verificationcosu), Toast.LENGTH_SHORT).show();
                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        proDialog.dismiss();
                        Toast.makeText(SignInPhoneActivity.this, getString(R.string.insd), Toast.LENGTH_SHORT).show();

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Log.d(TAG, "Invalid credential: "
                                    + e.getLocalizedMessage());
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // SMS quota exceeded
                            Log.d(TAG, "SMS Quota exceeded.");
                        }
                    }

                    @Override
                    public void onCodeSent(@androidx.annotation.NonNull String verificationId
                            , PhoneAuthProvider.ForceResendingToken token) {
                        lyvirifyCodephone.setVisibility(View.VISIBLE);

                        phoneVerificationId = verificationId;
                        resendToken = token;

                    }
                };

        btnSendPhoneSignInPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edtNumberPhoneSigninPhone.getText())) {
                    String phoneNumber = ccpPhoneNumberSigninPhone.getSelectedCountryCodeWithPlus() + edtNumberPhoneSigninPhone.getText().toString();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            phoneNumber,
                            60,
                            TimeUnit.SECONDS,
                            SignInPhoneActivity.this,
                            mCallbacks
                    );
                }
            }
        });


        btnVerifyPhoneSignInPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proDialog.show();
                verifyCode();
            }
        });

        ccpPhoneNumberSigninPhone.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
               codeCountry = ccpPhoneNumberSigninPhone.getSelectedCountryCode();
            }
        });

    }

    public void verifyCode() {

        String code = edtVerifyNumberPhoneSigninPhone.getText().toString();

        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(phoneVerificationId, code);
        signInWithPhoneAuthCredential(credential);
    }




    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = task.getResult().getUser();
                            Intent continueintent = new Intent(SignInPhoneActivity.this,ContinueSignInActivity.class);
                            continueintent.putExtra("iduser",user.getUid());
                            continueintent.putExtra("type_signin","phone");
                            startActivity(continueintent);

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

}
