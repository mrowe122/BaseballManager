package com.msrproduction.baseballmanager;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.msrproduction.baseballmanager.Database.DatabaseAdapter;

import org.w3c.dom.Text;

import java.util.Random;
import java.util.UUID;

public class MainActivity extends Activity implements View.OnClickListener {

	private final String LOG_TAG = MainActivity.class.getSimpleName();
	public static String userEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		checkFirstRun();
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.my_team:
				startActivity(new Intent(MainActivity.this, MyTeamsActivity.class));
				break;
			case R.id.main_games:
				startActivity(new Intent(MainActivity.this, GamesActivity.class));
				break;
			case R.id.main_teams:
				startActivity(new Intent(MainActivity.this, TeamsActivity.class));
				break;
			case R.id.main_players:
				startActivity(new Intent(MainActivity.this, PlayersActivity.class));
				break;
			case R.id.email:
				break;
		}
	}

	private void checkFirstRun() {
		Boolean isFirstRun = getSharedPreferences("FirstRunPreference", MODE_PRIVATE)
				.getBoolean("isfirstrun", true);

		if (isFirstRun) {
			getSharedPreferences("FirstRunPreference", MODE_PRIVATE).edit().putBoolean("isfirstrun", false).apply();

			new AlertDialog.Builder(this)
					.setTitle("Welcome! :)")
					.setMessage("Hope you enjoy this app!")
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//do nothing
						}
					}).setCancelable(false).show();
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

		findViewById(R.id.my_team).setOnClickListener(this);
		findViewById(R.id.main_games).setOnClickListener(this);
		findViewById(R.id.main_teams).setOnClickListener(this);
		findViewById(R.id.main_players).setOnClickListener(this);
		findViewById(R.id.email).setOnClickListener(this);
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
