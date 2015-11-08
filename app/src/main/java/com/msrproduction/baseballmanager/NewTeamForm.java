package com.msrproduction.baseballmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.msrproduction.baseballmanager.Database.DatabaseAdapter;

import java.util.ArrayList;

public class NewTeamForm extends AppCompatActivity {

	private DatabaseAdapter databaseAdapter;
	final CharSequence positions[] = {"P", "C", "1B", "2B", "3B", "SS", "LF", "CF", "RF"};
	ArrayList<EditText> playerName = new ArrayList<>();
	ArrayList<EditText> playerNumber = new ArrayList<>();
	ArrayList<Button> playerPositions = new ArrayList<>();
	int numPlayers = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_team_form);
		initSetup();
	}

	private void initSetup() {
		//add team button
		(findViewById(R.id.add_new_team)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//addTeam();
			}
		});

		//cancel button
		findViewById(R.id.cancel_team).setOnClickListener(new View.OnClickListener() {
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
