package com.rifcode.nearheart.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;


public class ServiceDestroyApp extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
//
//        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
////
//            String iduser = FirebaseAuth.getInstance().getUid();
////
////                        classFirebase.getDataUsersOnline().child(iduser)
////                                .removeValue();
////
//            FirebaseDatabase.getInstance().getReference().child("users").child(iduser).child("state_app").setValue("stop");
//        }
//
//        //Toast.makeText(this, "ontask", Toast.LENGTH_SHORT).show();
//        stopSelf();
    }
}
