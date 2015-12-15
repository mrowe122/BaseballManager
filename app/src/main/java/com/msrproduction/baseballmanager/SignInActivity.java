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
			ServerSynchronization serverSynchronization = new ServerSynchronization(SignInActivity.this);
			serverSynchronization.syncData(email);
		}
	}
}