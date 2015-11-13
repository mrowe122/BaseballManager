package com.msrproduction.baseballmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.msrproduction.baseballmanager.Database.Contract;
import com.msrproduction.baseballmanager.Database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class EditMyTeam extends AppCompatActivity {

	private DatabaseAdapter databaseAdapter;
	private List<String> removedPlayers = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		databaseAdapter = new DatabaseAdapter(getApplicationContext()).open();
		setContentView(R.layout.list_view_parallax);
		initSetup();
	}

	private void initSetup() {
		ListView listView = (ListView) findViewById(R.id.my_players_list);
		ViewGroup header = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_coach_form, listView, false);
		listView.addHeaderView(header, null, false);
		registerForContextMenu(listView);

		SharedPreferences coachInfo = getSharedPreferences("coach_info", MODE_PRIVATE);
		((EditText) header.findViewById(R.id.form_coach_name)).setText(coachInfo.getString("coach_name", ""));
		((EditText) header.findViewById(R.id.form_coach_team)).setText(coachInfo.getString("team_name", ""));
		((EditText) header.findViewById(R.id.form_coach_email)).setText(coachInfo.getString("coach_email", ""));
		((EditText) header.findViewById(R.id.form_coach_phone)).setText(coachInfo.getString("coach_phone", ""));

		//hide/show appropiate views
		header.findViewById(R.id.buttons).setVisibility(View.GONE);
		header.findViewById(R.id.edit_roster).setVisibility(View.VISIBLE);
		findViewById(R.id.parallax_buttons).setVisibility(View.VISIBLE);

		//set edit roster to visible
		header.findViewById(R.id.edit_roster).setVisibility(View.VISIBLE);

		//add coach button
		findViewById(R.id.parallax_add_new_coach).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editCoach();
			}
		});

		//cancel button
		findViewById(R.id.parallax_cancel_coach).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(0);
				finish();
			}
		});

		listView.setAdapter(new PlayerListAdapter(getApplicationContext(), databaseAdapter.loadPlayersInMyTeam(coachInfo.getString("team_name", "")), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER));
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivity(new Intent(getApplicationContext(), PlayerInformation.class)
						.putExtra("player_id", id + ""));
			}
		});
	}

	private void editCoach() {
		if (removedPlayers.size() > 0) {
			new AlertDialog.Builder(EditMyTeam.this)
					.setTitle(R.string.removing_players_title)
					.setMessage(R.string.removing_players_message)
					.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							databaseAdapter.removePlayersFromTeam(removedPlayers);
							test();
						}
					})
					.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//do nothing
						}
					}).show();
		} else {
			test();
		}
	}

	private void test() {
		String name = ((EditText) findViewById(R.id.form_coach_name)).getText().toString();
		if (name.equals("")) {
			Toast.makeText(this, R.string.error_empty_name, Toast.LENGTH_SHORT).show();
			return;
		}
		String teamName = ((EditText) findViewById(R.id.form_coach_team)).getText().toString();
		if (teamName.equals("")) {
			Toast.makeText(this, R.string.error_empty_team, Toast.LENGTH_SHORT).show();
			return;
		}
		String email = ((EditText) findViewById(R.id.form_coach_email)).getText().toString();
		if (email.equals(""))
			email = "n/a";
		String phone = ((EditText) findViewById(R.id.form_coach_phone)).getText().toString();
		if (phone.equals(""))
			phone = "n/a";

		SharedPreferences sharedpreferences = getSharedPreferences("coach_info", MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putString("coach_name", name);
		editor.putString("team_name", teamName);
		editor.putString("coach_email", email);
		editor.putString("coach_phone", phone);
		editor.apply();
		getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("isCoachSetup", true).apply();
		setResult(1);
		finish();
	}

	private class PlayerListAdapter extends CursorAdapter {

		public PlayerListAdapter(Context context, Cursor c, int flags) {
			super(context, c, flags);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
		}

		@Override
		public void bindView(final View view, final Context context, Cursor cursor) {
			String playerInfo = "#" + cursor.getInt(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_NUMBER)) + " " +
					cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_NAME));
			((TextView) view.findViewById(R.id.list_item_name)).setText(playerInfo);
			playerInfo = "(" + cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_POSITION)) + ")";
			((TextView) view.findViewById(R.id.list_item_sub_text)).setText(playerInfo);

			final CheckBox remove = (CheckBox) view.findViewById(R.id.remove_field);
			final String item_id = cursor.getString(cursor.getColumnIndex(Contract.PlayerEntry._ID));
			remove.setVisibility(View.VISIBLE);
			remove.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.text_view_layout);
					if (remove.isChecked()) {
						removedPlayers.add(item_id);
						linearLayout.setBackgroundColor(context.getResources().getColor(R.color.lightRed));
					} else {
						removedPlayers.remove(item_id);
						linearLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
					}
				}
			});
		}

		@Override
		public int getViewTypeCount() {
			if (getCount() < 1)
				return 1;

			return getCount();
		}

		@Override
		public int getItemViewType(int position) {
			return position;
		}
	}
}
