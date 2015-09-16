package com.msrproduction.baseballmanager;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;

import java.lang.ref.WeakReference;
import java.util.Random;

public class MainActivity extends Activity {

	private final String LOG_TAG = MainActivity.class.getSimpleName();
	public static String userEmail;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initSetup();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == RESULT_OK) {
			String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
			userEmail = accountName;
			((TextView) findViewById(R.id.email)).setText(accountName);
		}
	}

	private void initSetup() {
		ImageView iv = (ImageView) findViewById(R.id.baseball_icon);
		switch (new Random().nextInt(6)) {
			case 0:
				iv.setImageResource(R.drawable.icon_a);
				break;
			case 1:
				iv.setImageResource(R.drawable.icon_b);
				break;
			case 2:
				iv.setImageResource(R.drawable.icon_c);
				break;
			case 3:
				iv.setImageResource(R.drawable.icon_d);
				break;
			case 4:
				iv.setImageResource(R.drawable.icon_e);
				break;
			case 5:
				iv.setImageResource(R.drawable.icon_f);
				break;
			default:
				iv.setImageResource(R.drawable.icon_e);
				break;
		}

		findViewById(R.id.main_games).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, GamesActivity.class));
			}
		});

		findViewById(R.id.main_leagues).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, LeagueActivity.class));
			}
		});

		findViewById(R.id.main_teams).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, TeamsActivity.class));
			}
		});

		findViewById(R.id.main_players).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, PlayersActivity.class));
			}
		});

		findViewById(R.id.email).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getUsersEmail();
			}
		});
	}

	private void getUsersEmail() {
		try {
			Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE}, false, null, null, null, null);
			startActivityForResult(intent, 1);
		} catch (ActivityNotFoundException e) {
			Log.e(LOG_TAG, "Exception: " + e);
		}
	}
}
