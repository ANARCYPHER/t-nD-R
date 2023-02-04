package com.rifcode.nearheart.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.view.View;

import com.rifcode.nearheart.R;

/**
 * Created by ibra_ on 31/01/2018.
 */

public class DialogUtils {


        public static ProgressDialog showProgressDialog(Activity activity, String message) {
            ProgressDialog m_Dialog = new ProgressDialog(activity, R.style.AppCompatAlertDialogStyle);
            m_Dialog.setMessage(message);
            m_Dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            m_Dialog.setCancelable(false);

            return m_Dialog;

        }

        public static AlertDialog.Builder CustomAlertDialog(View view,Activity activity){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
            alertDialogBuilder.setView(view);


            return alertDialogBuilder;
        }


}
