package com.rifcode.nearheart.View;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.rifcode.nearheart.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private TextView tvPrivacyP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        tvPrivacyP= findViewById(R.id.tvPrivacyP);
        StringBuilder text = new StringBuilder();




        String file  = getIntent().getStringExtra("file");
        InputStream input = null;

        if(Locale.getDefault().getLanguage().equals("ar")){

            if (file.equals("privacy_policy")) {
                try {
                    input = getAssets().open("privacy_policy_ar");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    input = getAssets().open("safety_tips_ar");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }else {

            if (file.equals("privacy_policy")) {
                try {
                    input = getAssets().open("privacy_policy");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    input = getAssets().open("safety_tips");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



        }

        try {

            InputStreamReader isr = new InputStreamReader(input);

            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        tvPrivacyP.setText(text);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(PrivacyPolicyActivity.this);
    }
}
