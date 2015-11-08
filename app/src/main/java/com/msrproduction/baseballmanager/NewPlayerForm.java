package com.msrproduction.baseballmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.msrproduction.baseballmanager.Database.DatabaseAdapter;

import java.util.ArrayList;

public class NewPlayerForm extends AppCompatActivity {

	private DatabaseAdapter databaseAdapter;
	final CharSequence positions[] = {"P", "C", "1B", "2B", "3B", "SS", "LF", "CF", "RF"};
	ArrayList<EditText> playerName = new ArrayList<>();
	ArrayList<EditText> playerNumber = new ArrayList<>();
	ArrayList<Button> playerPositions = new ArrayList<>();
	int numPlayers = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player_form);
		databaseAdapter = new DatabaseAdapter(getApplicationContext()).open();
		initSetup();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void initSetup() {
		addField();
		//noinspection ConstantConditions
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//set total number of players to 1
		((TextView) findViewById(R.id.number_players)).setText(String.valueOf(numPlayers));
		//add player button
		(findViewById(R.id.add_new_player)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addPlayer();
				finish();
			}
		});
		//cancel button
		findViewById(R.id.cancel_player).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		//add field button
		findViewById(R.id.add_new_field).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addField();
			}
		});
	}

	private void addPlayer() {
		//supply the string variables with the appropriate information from the NewPlayerFrom EditTexts
		String newPlayerName = ((EditText) findViewById(R.id.form_player_name)).getText().toString();
		String newPlayerNumber = ((EditText) findViewById(R.id.form_player_number)).getText().toString();

		//make sure both fields are not empty and a team is selected
		if (!newPlayerName.equals("") && !newPlayerNumber.equals("")) {
			String selectedTeam = "Yankees";
			//then check if number exists in the team
			if (!databaseAdapter.checkIfNumberExistsInTeam(newPlayerNumber, selectedTeam) || selectedTeam.equals("Free Agent")) {
				//they don't exists, add player to the database
				databaseAdapter.insertPlayer(newPlayerName, newPlayerNumber, "n/a", selectedTeam);
				finish();
				overridePendingTransition(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_bottom);
			} else {
				//toast message to show that the number already exists in the TEAM ONLY
				Toast.makeText(getApplicationContext(),
						getResources().getString(R.string.error_player_number_exists)
								+ ":\n" + newPlayerNumber,
						Toast.LENGTH_SHORT).show();
			}
		} else {
			//toast message to a field is empty or team wasn't selected
			Toast.makeText(getApplicationContext(),
					R.string.error_player_form_empty,
					Toast.LENGTH_SHORT).show();
		}
	}

	private void addField() {
		final LinearLayout containerLayout = (LinearLayout) findViewById(R.id.new_fields_created);
		final View layout = getLayoutInflater().inflate(R.layout.layout_new_player_field, null);
		final EditText name = (EditText) layout.findViewById(R.id.form_player_name);
		final EditText number = (EditText) layout.findViewById(R.id.form_player_number);
		final Button spinnerPosition = (Button) layout.findViewById(R.id.spinner_pos);
		final ArrayList<Integer> selList = new ArrayList<>();
		playerName.add(name);
		playerNumber.add(number);
		playerPositions.add(spinnerPosition);
		((TextView) findViewById(R.id.number_players)).setText(String.valueOf(++numPlayers));
		//delete button
		layout.findViewById(R.id.remove_field).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((TextView) findViewById(R.id.number_players)).setText(String.valueOf(--numPlayers));
				playerName.remove(name);
				playerNumber.remove(number);
				playerPositions.remove(spinnerPosition);
				containerLayout.removeView(layout);
			}
		});
		//configure Alert Dialog for players position
		final AlertDialog popupPosition = new AlertDialog.Builder(this).setMultiChoiceItems(positions, new boolean[positions.length], new DialogInterface.OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				if (isChecked) {
					//add position of which position was chosen
					selList.add(which);
				} else if (selList.contains(which)) {
					//if unchecked, remove the selectedList array
					selList.remove((Integer.valueOf(which)));
				}
			}
		}).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				configurePosButton(spinnerPosition, selList);
			}
		}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		}).create();
		spinnerPosition.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				popupPosition.show();
			}
		});
		containerLayout.addView(layout);
	}

	private void configurePosButton(Button positionButton, ArrayList<Integer> selList) {
		StringBuilder position = new StringBuilder();
		for (int i = 0; i < selList.size(); i++) {
			position.append(positions[selList.get(i)]);
			position.append(", ");
		}
		if (position.length() > 0) {
			positionButton.setText(position.substring(0, position.length() - 2));
			position.setLength(0);
		} else {
			positionButton.setText(R.string.spinner_position_title);
			selList.clear();
		}
	}
}
