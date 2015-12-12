package com.msrproduction.baseballmanager;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.msrproduction.baseballmanager.plugins.ServerSynchronization;

public class SignInActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Intent intent = AccountPicker.zza(null, null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false, null, null, null, null, false, 1, 0);
            startActivityForResult(intent, 1);
        } catch (ActivityNotFoundException e) {
            System.out.println(e.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            final String email = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            new AlertDialog.Builder(this)
                    .setTitle(R.string.sign_in_title)
                    .setMessage(R.string.sign_in_message)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ServerSynchronization serverSyncronization = new ServerSynchronization(SignInActivity.this);
                            serverSyncronization.syncAllData(email);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(2);
                            finish();
                        }
                    }).setCancelable(false).show();
        }
    }
}