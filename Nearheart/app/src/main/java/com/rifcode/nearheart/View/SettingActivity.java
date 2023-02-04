package com.rifcode.nearheart.View;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.rifcode.nearheart.Utils.DialogUtils;
import com.rifcode.nearheart.R;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingActivity extends AppCompatActivity {

    private TextView dd;
    private Button btnlogout,btnShareApp,btnVoteApp,btnHelpAndSupport,btnSafetyTipsSetting,btnPrivacyPolicySettingAcc
            ,btnClearDataApp;
    private FirebaseAuth mAuth;
    private DatabaseReference dbUsers;
    private String userID;
    private EditText edtPhoneAccSetting,edtEmailAccSetting;
    private CountryCodePicker ccpCodeCountryPhone;
    private Button btn18_24,btn25_35,btn36_46,btn47;
    private TextView tvSelectedRangeAge;
    private Switch swSetting;
    private RadioButton rbWomanSetting,rbManSetting;
    private CountryCodePicker ccpChangelocSetting;
    private String show_me;
    private LinearLayout ly_discovery_setting;
    private View mViewInflateEnableSeetingDiscovery;
    private AlertDialog alertDialog;
    private View mViewInflatelogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        wedgets();
        getSupportActionBar().setTitle(getString(R.string.settings));
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        dbUsers = FirebaseDatabase.getInstance().getReference().child("users")
                .child(userID);

        getInfoDiscoverySetting();
        retreiveDataFromFirebase();


        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               dialog_logout();
            }
        });

        btnClearDataApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCache(getApplicationContext());
            }
        });

        btnShareApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareApp();
            }
        });

        btnVoteApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchMarket();
            }
        });

        btnHelpAndSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendEmail("rifcode39@gmail.com");
            }
        });

        btnPrivacyPolicySettingAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pp =  new Intent(SettingActivity.this,PrivacyPolicyActivity.class);
                pp.putExtra("file","privacy_policy");
                startActivity(pp);

            }
        });

        btnSafetyTipsSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pp =  new Intent(SettingActivity.this,PrivacyPolicyActivity.class);
                pp.putExtra("file","safety_tips");
                startActivity(pp);
                Animatoo.animateSlideLeft(SettingActivity.this);
            }
        });

        ccpCodeCountryPhone.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                dbUsers.child("codeCountryPhone").setValue(ccpCodeCountryPhone.getSelectedCountryCode());
            }
        });


            btn18_24.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvSelectedRangeAge.setText("18-24");

                }
            });


            btn25_35.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvSelectedRangeAge.setText("25-35");


                }
            });

            btn36_46.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvSelectedRangeAge.setText("36-46");

                }
            });

            btn47.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tvSelectedRangeAge.setText("47");
                }
            });

        swSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    dbUsers.child("discovery").setValue("true");


                }else{
                    dbUsers.child("discovery").setValue("false");
                }
            }
        });





    }
    private void showToast(String message) {
        Toast.makeText(SettingActivity.this, message, Toast.LENGTH_LONG).show();
    }
    @Override
    protected void onStart() {
        super.onStart();
    }


    private String checkRbShowMe(){

        if(!swSetting.isChecked()){
            dialog_enable_discsetting();
        }

        if(rbWomanSetting.isChecked())
            show_me = "Woman";
        if(rbManSetting.isChecked())
            show_me = "Man";

        return show_me;
    }


    private void getInfoDiscoverySetting(){

        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild("discovery") && dataSnapshot.child("discovery").getValue().toString().equals("true")){
                    swSetting.setChecked(true);
                }else
                if(dataSnapshot.hasChild("discovery") && dataSnapshot.child("discovery").getValue().toString().equals("false")){
                    swSetting.setChecked(false);
                }



                if(dataSnapshot.hasChild("discovery_setting")) {

                    String showme = String.valueOf(dataSnapshot.child("discovery_setting").child("show_me").getValue());
                    String age_range = String.valueOf(dataSnapshot.child("discovery_setting").child("age_range").getValue());
                    String country_code  = String.valueOf(dataSnapshot.child("country_code").getValue());

                    tvSelectedRangeAge.setText(age_range);
                    if(showme.equals("Woman")){
                        rbWomanSetting.setChecked(true);
                    }else{
                        rbManSetting.setChecked(true);
                    }
                    ccpChangelocSetting.setCountryForNameCode(country_code);

                }else {
                    String gender = String.valueOf(dataSnapshot.child("gender").getValue());
                    String agerande_user = String.valueOf(dataSnapshot.child("age_range").getValue());
                    String country_code = String.valueOf(dataSnapshot.child("country_code").getValue());
                    tvSelectedRangeAge.setText(agerande_user);
                    if (gender.equals("Man")) {
                        rbWomanSetting.setChecked(true);
                    } else {
                        rbManSetting.setChecked(true);
                    }
                    ccpChangelocSetting.setCountryForNameCode(country_code);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendEmail(String email){

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{email});
        try {
            startActivity(Intent.createChooser(i, getString(R.string.send_mail)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, R.string.ther_no_email_send, Toast.LENGTH_SHORT).show();
        }
    }

    private void wedgets() {

        btnlogout = findViewById(R.id.btnlogout);
        btnClearDataApp = findViewById(R.id.btnClearDataApp);
        btnShareApp = findViewById(R.id.btnShareApp);
        btnVoteApp = findViewById(R.id.btnVoteApp);
        btnHelpAndSupport = findViewById(R.id.btnHelpAndSupport);
        btnPrivacyPolicySettingAcc = findViewById(R.id.btnPrivacyPolicySettingAcc);
        btnSafetyTipsSetting = findViewById(R.id.btnSafetyTipsSetting);
        ly_discovery_setting = findViewById(R.id.ly_discovery_setting);

        edtEmailAccSetting = findViewById(R.id.edtEmailAccSetting);
        edtPhoneAccSetting = findViewById(R.id.edtPhoneAccSetting);
        tvSelectedRangeAge = findViewById(R.id.tvShowRangeAgeSelected);
        swSetting = findViewById(R.id.swSiscovrySetting);

        ccpCodeCountryPhone = findViewById(R.id.ccpPhoneNumberSetting);

        btn18_24 = findViewById(R.id.btn18_24);
        btn25_35 = findViewById(R.id.btn25_35);
        btn36_46 = findViewById(R.id.btn36_46);
        btn47 = findViewById(R.id.btn47);


        
        rbManSetting = findViewById(R.id.rbManSetting);
        rbWomanSetting = findViewById(R.id.rbWomanSetting);

        ccpChangelocSetting = findViewById(R.id.ccpChangelocSetting);

        ly_discovery_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!swSetting.isChecked()){
                    dialog_enable_discsetting();
                }
            }
        });

    }


    private void dialog_enable_discsetting(){

        mViewInflateEnableSeetingDiscovery = getLayoutInflater().inflate(R.layout.dialog_enable_discovrysetting,null);

        ImageView imgvExitdialogAddMedia = mViewInflateEnableSeetingDiscovery.findViewById(R.id.imgvExitdialogEnable_discsetting);

        AlertDialog.Builder alertDialogBuilderpost = DialogUtils.CustomAlertDialog(mViewInflateEnableSeetingDiscovery,SettingActivity.this);
        alertDialog = alertDialogBuilderpost.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationENTER; //style id
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        alertDialog.show();

        imgvExitdialogAddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
    }


    private void dialog_logout(){

        mViewInflatelogout = getLayoutInflater().inflate(R.layout.dialog_logout,null);

        android.widget.Button btnlogoutDialog = mViewInflatelogout.findViewById(R.id.btnlogoutDialog);

        AlertDialog.Builder alertDialogBuilderpost = DialogUtils.CustomAlertDialog(mViewInflatelogout,SettingActivity.this);
        alertDialog = alertDialogBuilderpost.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimationENTER; //style id
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.setCancelable(true);
        alertDialog.show();

        btnlogoutDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getUid() != null){

                    alertDialog.dismiss();
                    mAuth.signOut();
                    startActivity(new Intent(SettingActivity.this,FirstUseActivity.class));
                    Animatoo.animateInAndOut(SettingActivity.this);
                    finish();

//                    dbUsers.child("state_app").setValue("stop").addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//
//                        }
//                    });
                }
            }
        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_account_settings,menu);
        return super.onCreateOptionsMenu(menu);
    }

    /// selected items menu:
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int check = item.getItemId();
        switch(check) {

            case R.id.mnSaveSetting:
                saveSetting();
                break;
            case android.R.id.home:
                finish();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);

    }

    private void saveSetting() {

        if(swSetting.isChecked()){
            dbUsers.child("discovery_setting").child("age_range").setValue(tvSelectedRangeAge.getText());
            dbUsers.child("discovery_setting").child("show_me").setValue(checkRbShowMe());
            dbUsers.child("country_code").setValue(ccpChangelocSetting.getSelectedCountryNameCode());
        }

        if(TextUtils.isEmpty(edtEmailAccSetting.getText()) || TextUtils.isEmpty(edtPhoneAccSetting.getText())){
            Toast.makeText(this, getString(R.string.help_isEmpty), Toast.LENGTH_SHORT).show();
            return;
        }


        if(!edtPhoneAccSetting.getText().toString().equals("---") && edtPhoneAccSetting.getText().length() < 9){
            Toast.makeText(this, getString(R.string.numberphone_incorrect), Toast.LENGTH_SHORT).show();
            return;
        }

        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String type_account = String.valueOf(dataSnapshot.child("type_account").getValue());
                if(!type_account.equals("phone")){
                    if(!isEmailValid(edtEmailAccSetting.getText().toString())){
                        Toast.makeText(SettingActivity.this, getString(R.string.youremial_notvalid), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dbUsers.child("email").setValue(String.valueOf(edtEmailAccSetting.getText()));
        dbUsers.child("numberPhone").setValue(String.valueOf(edtPhoneAccSetting.getText()));
        dbUsers.child("codeCountryPhone").setValue(String.valueOf(ccpCodeCountryPhone.getSelectedCountryCode()));

        Toast.makeText(this, getString(R.string.upda_successfully), Toast.LENGTH_SHORT).show();

    }

    /**
     * method is used for checking valid email id format.
     *
     * @param email
     * @return boolean true for valid false for invalid
     */
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    private void retreiveDataFromFirebase(){


        dbUsers.keepSynced(true);

        dbUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String numberPhone = dataSnapshot.child("numberPhone").getValue().toString();
//                String codeCountryPhone = dataSnapshot.child("codeCountryPhone").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
//                String gender = dataSnapshot.child("gender").getValue().toString();

                edtEmailAccSetting.setText(email);
                edtPhoneAccSetting.setText(numberPhone);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    public void shareApp(){
        Intent sharePst = new Intent(Intent.ACTION_SEND);
        sharePst.setType("text/plain");
        sharePst.putExtra(Intent.EXTRA_TEXT, getString(R.string.check_out_share)+" "+"https://play.google.com/store/apps/details?id="+getPackageName());
        startActivity(Intent.createChooser(sharePst,getString(R.string.share_to)));
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            //Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
//        saveSetting();

        if(swSetting.isChecked()){
            dbUsers.child("discovery_setting").child("age_range").setValue(tvSelectedRangeAge.getText());
            dbUsers.child("discovery_setting").child("show_me").setValue(checkRbShowMe());
            dbUsers.child("country_code").setValue(ccpChangelocSetting.getSelectedCountryNameCode());
        }
        finish();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if(swSetting.isChecked()){
            dbUsers.child("discovery_setting").child("age_range").setValue(tvSelectedRangeAge.getText());
            dbUsers.child("discovery_setting").child("show_me").setValue(checkRbShowMe());
            dbUsers.child("country_code").setValue(ccpChangelocSetting.getSelectedCountryNameCode());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(swSetting.isChecked()){
            dbUsers.child("discovery_setting").child("age_range").setValue(tvSelectedRangeAge.getText());
            dbUsers.child("discovery_setting").child("show_me").setValue(checkRbShowMe());
            dbUsers.child("country_code").setValue(ccpChangelocSetting.getSelectedCountryNameCode());
        }
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if(deleteDir(dir)){
                Toast.makeText(context.getApplicationContext(), context.getString(R.string.clsucc), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context.getApplicationContext(), context.getString(R.string.cldataf), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

}
