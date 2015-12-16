package com.msrproduction.baseballmanager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.msrproduction.baseballmanager.Database.Contract;
import com.msrproduction.baseballmanager.Database.DatabaseAdapter;

public class MyTeamsActivity extends AppCompatActivity {

	private DatabaseAdapter databaseAdapter;
	private PlayerListAdapter cursorAdapter;
	private FloatingActionMenu fabMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkFirstRun();
		setContentView(R.layout.list_view_parallax);
		databaseAdapter = new DatabaseAdapter(this).open();
		initSetup();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit_team:
				startActivityForResult(new Intent(MyTeamsActivity.this, EditMyTeam.class), 2);
				break;
			case R.id.action_sign_in:
				boolean signedOn = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE).getBoolean("isSignedIn", false);
				if(signedOn) {
					Toast.makeText(MyTeamsActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
				} else {
					startActivityForResult(new Intent(MyTeamsActivity.this, SignInActivity.class), 3);
				}
				break;
			case R.id.action_settings:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_my_team, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean signedOn = getSharedPreferences("AppPreferences", Context.MODE_PRIVATE).getBoolean("isSignedIn", false);
		if(signedOn) {
			menu.findItem(R.id.action_sign_in).setTitle(R.string.action_sign_out);
		}
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		loadCoachData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		fabMenu.close(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case 1: //for first time running check
				if (resultCode != 1)
					finish();
				break;
			case 2: //for updating list view
				if (resultCode == 1)
					cursorAdapter.swapCursor(databaseAdapter.loadPlayersInMyTeam());
				break;
			case 3: //if synchronized
				if (resultCode == 1) {
					loadCoachData();
					cursorAdapter.swapCursor(databaseAdapter.loadPlayersInMyTeam());
				} else if (resultCode == 2) {
					finish();
				}
				break;
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.my_players_list)
			getMenuInflater().inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.edit_selected:
				startActivityForResult(new Intent(getApplicationContext(), EditPlayer.class).putExtra("edit_player", info.id + ""), 2);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	private void checkFirstRun() {
		Boolean isTeamSetup = getSharedPreferences("AppPreferences", MODE_PRIVATE).getBoolean("isTeamSetup", false);
		if (!isTeamSetup) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.dialog_setup_team_title)
					.setMessage(R.string.dialog_setup_team_message)
					.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startActivityForResult(new Intent(MyTeamsActivity.this, NewCoachForm.class), 1);
						}
					})
					.setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					})
					.setNeutralButton(R.string.sign_in, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startActivityForResult(new Intent(MyTeamsActivity.this, SignInActivity.class), 3);
						}
					}).setCancelable(false).show();
		}
	}

	private void initSetup() {
		//Instantiate list of players list view
		ListView listView = (ListView) findViewById(R.id.my_players_list);
		ViewGroup header = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_my_team, listView, false);
		listView.addHeaderView(header, null, false);
		registerForContextMenu(listView);

		//load coach information
		loadCoachData();
		//initiate floating action buttons
		initFabButtons();

		//load the players in my team
		cursorAdapter = new PlayerListAdapter(getApplicationContext(), databaseAdapter.loadPlayersInMyTeam(), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		listView.setAdapter(cursorAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startActivityForResult(new Intent(getApplicationContext(), PlayerInformation.class).putExtra("player_id", id + ""), 2);
			}
		});
	}

	private void loadCoachData() {
		SharedPreferences coachInfo = getSharedPreferences("team_info", MODE_PRIVATE);
		((TextView) findViewById(R.id.my_coach_name)).setText(coachInfo.getString("coach_name", ""));
		((TextView) findViewById(R.id.my_team_name)).setText(coachInfo.getString("team_name", ""));
		((TextView) findViewById(R.id.my_email)).setText(coachInfo.getString("coach_email", ""));
	}

	/*Instantiate floating action button*/
	private void initFabButtons() {
		fabMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
		fabMenu.setVisibility(View.VISIBLE);
		fabMenu.setClosedOnTouchOutside(true);
		FloatingActionButton addPlayer = (FloatingActionButton) findViewById(R.id.fab_add_player);
		FloatingActionButton addTeam = (FloatingActionButton) findViewById(R.id.fab_add_team);
		FloatingActionButton addFreeAgents = (FloatingActionButton) findViewById(R.id.fab_add_player_to_team);

		addPlayer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(MyTeamsActivity.this, NewPlayerForm.class), 2);
				overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
			}
		});

		addTeam.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MyTeamsActivity.this, NewTeamForm.class));
				overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
			}
		});

		addFreeAgents.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//startActivity(new Intent(MyTeamsActivity.this, AddPlayerToTeam.class));
				//overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
			}
		});
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
		public void bindView(View view, Context context, Cursor cursor) {
			String playerInfo = "#" + cursor.getInt(cursor.getColumnIndexOrThrow(Contract.MyPlayerEntry.COLUMN_NUMBER)) + " " +
					cursor.getString(cursor.getColumnIndexOrThrow(Contract.MyPlayerEntry.COLUMN_NAME));
			((TextView) view.findViewById(R.id.list_item_name)).setText(playerInfo);
			playerInfo = "(" + cursor.getString(cursor.getColumnIndexOrThrow(Contract.MyPlayerEntry.COLUMN_POSITION)) + ")";
			((TextView) view.findViewById(R.id.list_item_sub_text)).setText(playerInfo);
		}
	}
}
