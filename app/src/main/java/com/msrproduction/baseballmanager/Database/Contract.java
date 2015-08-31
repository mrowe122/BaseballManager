package com.msrproduction.baseballmanager.Database;

import android.provider.BaseColumns;

public class Contract {

	public Contract() {
	}

	public static abstract class TeamEntry implements BaseColumns {
		public static final String TABLE_NAME = "teams";
		public static final String COLUMN_TEAM_NAME = "name";
		public static final String COLUMN_TEAM_COACH = "coach";
		public static final String COLUMN_TEAM_WINS = "wins";
		public static final String COLUMN_TEAM_LOSE = "lose";
	}

	public static abstract class PlayerEntry implements BaseColumns {
		public static final String TABLE_NAME = "players";
		public static final String COLUMN_PLAYER_NAME = "name";
		public static final String COLUMN_PLAYER_NUMBER = "number";
		public static final String COLUMN_PLAYER_TEAM_NAME = "team_name";
		public static final String COLUMN_PLAYER_POSITION = "position";
		public static final String COLUMN_PLAYER_BATTING_AVERAGE = "batting_average";
		public static final String COLUMN_PLAYER_RBI = "rbi";
		public static final String COLUMN_PLAYER_RUNS = "runs";
		public static final String COLUMN_PLAYER_HITS = "hits";
		public static final String COLUMN_PLAYER_STRIKE_OUTS = "strike_outs";
		public static final String COLUMN_PLAYER_WALKS = "walks";
		public static final String COLUMN_PLAYER_SINGLE = "single";
		public static final String COLUMN_PLAYER_DOUBLE = "double";
		public static final String COLUMN_PLAYER_TRIPLE = "triple";
		public static final String COLUMN_PLAYER_HOME_RUNS = "home_runs";
		public static final String COLUMN_PLAYER_FLY_BALL = "fly_balls";
		public static final String COLUMN_PLAYER_GROUND_BALLS = "ground_balls";
		public static final String COLUMN_PLAYER_ON_BASE_PERCENTAGE = "on_base_percentage";
		public static final String COLUMN_PLAYER_BASES_STOLEN = "bases_stolen";
		public static final String COLUMN_PLAYER_CAUGHT_STEALING = "caught_stealing";
		public static final String COLUMN_PLAYER_ERRORS = "errors";
		public static final String COLUMN_PLAYER_FIELD_PERCENTAGE = "field_percentage";
		public static final String COLUMN_PLAYER_PUT_OUTS = "put_outs";
	}

	public static abstract class PitcherEntry implements BaseColumns {
		public static final String TABLE_NAME = "pitchers";
		public static final String COLUMN_PITCHER_NAME = "name";
		public static final String COLUMN_PITCHER_GAMES_STARTED = "games_started";
		public static final String COLUMN_PITCHER_INNINGS_PITCHED = "innings_pitched";
		public static final String COLUMN_PITCHER_PITCHES = "total_pitches";
		public static final String COLUMN_PITCHER_STRIKES = "strikes";
		public static final String COLUMN_PITCHER_BALLS = "balls";
		public static final String COLUMN_PITCHER_STRIKEOUTS = "strike_outs";
		public static final String COLUMN_PITCHER_WALKS = "walks";
		public static final String COLUMN_PITCHER_HIT_BATTER = "hit_batter";
	}
}