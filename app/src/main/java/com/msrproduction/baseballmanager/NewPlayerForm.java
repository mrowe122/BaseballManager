package com.msrproduction.baseballmanager;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.msrproduction.baseballmanager.plugins.PlayerSchema;

public class NewPlayerForm extends AppCompatActivity {

	private PlayerSchema playerSchema;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player_form);
		initSetup();
	}

	private void initSetup() {
		playerSchema = new PlayerSchema(this);
		playerSchema.addField();

		//add player button
		(findViewById(R.id.add_new_player)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (playerSchema.addPlayers()) {
					setResult(1);
					finish();
				}
			}
		});

		//cancel button
		findViewById(R.id.cancel_player).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(0);
				finish();
			}
		});

		//add field button
		findViewById(R.id.add_new_field).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				playerSchema.addField();
			}
		});
	}
}
