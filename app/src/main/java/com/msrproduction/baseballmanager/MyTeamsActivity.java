package com.msrproduction.baseballmanager;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.AccountPicker;
import com.msrproduction.baseballmanager.Database.Contract;
import com.msrproduction.baseballmanager.Database.DatabaseAdapter;

public class MyTeamsActivity extends AppCompatActivity {

	private DatabaseAdapter databaseAdapter;
	private PlayerListAdapter cursorAdapter;
    FloatingActionMenu fabMenu;

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
                startActivityForResult(new Intent(MyTeamsActivity.this, SignInActivity.class), 3);
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
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
			//for first time running check
			case 1:
				if (resultCode != 1) {
					finish();
				}
				break;
			//for updating list view
			case 2:
				if (resultCode == 1) {
					cursorAdapter.swapCursor(
							databaseAdapter.loadPlayersInMyTeam(
									getSharedPreferences("coach_info", MODE_PRIVATE)
											.getString("team_name", "")));
				}
                break;
            //if synchronized
            case 3:
                if(resultCode == 1) {
                    loadCoachData();
                }
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.my_players_list) {
			getMenuInflater().inflate(R.menu.context_menu, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
			case R.id.edit_selected:
				startActivityForResult(new Intent(getApplicationContext(), EditPlayer.class)
						.putExtra("edit_player", info.id + ""), 2);
				return true;

			default:
				return super.onContextItemSelected(item);
		}
	}

	private void checkFirstRun() {
		Boolean isCoachSetup = getSharedPreferences("FirstRunPreference", MODE_PRIVATE).getBoolean("isCoachSetup", false);
		if (!isCoachSetup) {
			new AlertDialog.Builder(this)
					.setTitle(R.string.dialog_setup_team_title)
					.setMessage(R.string.dialog_setup_team_message)
					.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							startActivityForResult(new Intent(getApplicationContext(), NewCoachForm.class), 1);
						}
					}).setNegativeButton(R.string.later, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
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
		String teamName = getSharedPreferences("coach_info", MODE_PRIVATE).getString("team_name", "");
		cursorAdapter = new PlayerListAdapter(getApplicationContext(), databaseAdapter.loadPlayersInMyTeam(teamName), CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        listView.setAdapter(cursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivityForResult(new Intent(getApplicationContext(), PlayerInformation.class)
                        .putExtra("player_id", id + ""), 2);
            }
        });
	}

	private void loadCoachData() {
		SharedPreferences coachInfo = getSharedPreferences("coach_info", MODE_PRIVATE);
		((TextView) findViewById(R.id.my_coach_name)).setText(coachInfo.getString("coach_name", ""));
		((TextView) findViewById(R.id.my_team_name)).setText(coachInfo.getString("team_name", ""));
		((TextView) findViewById(R.id.my_email)).setText(coachInfo.getString("coach_email", ""));
		((TextView) findViewById(R.id.my_phone)).setText(coachInfo.getString("coach_phone", ""));
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
			String playerInfo = "#" + cursor.getInt(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_NUMBER)) + " " +
					cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_NAME));
			((TextView) view.findViewById(R.id.list_item_name)).setText(playerInfo);
			playerInfo = "(" + cursor.getString(cursor.getColumnIndexOrThrow(Contract.PlayerEntry.COLUMN_PLAYER_POSITION)) + ")";
			((TextView) view.findViewById(R.id.list_item_sub_text)).setText(playerInfo);
		}
	}
}
