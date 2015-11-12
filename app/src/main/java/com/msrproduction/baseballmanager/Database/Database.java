package com.msrproduction.baseballmanager.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

	//private final String LOG_TAG = Database.class.getSimpleName();
	private static Database sInstance;
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "baseball_manager.db";

	public static synchronized Database getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new Database(context.getApplicationContext());
		}
		return sInstance;
	}

	private Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		final String CREATE_TEAM_TABLE = "CREATE TABLE " + Contract.TeamEntry.TABLE_NAME + " (\n" +
				Contract.TeamEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
				Contract.TeamEntry.COLUMN_TEAM_NAME + " TEXT UNIQUE NOT NULL, \n" +
				Contract.TeamEntry.COLUMN_TEAM_COACH + " TEXT, \n" +
				Contract.TeamEntry.COLUMN_TEAM_WINS + " INTEGER, \n" +
				Contract.TeamEntry.COLUMN_TEAM_LOSE + " INTEGER );";

		final String CREATE_PLAYER_TABLE = "CREATE TABLE " + Contract.PlayerEntry.TABLE_NAME + " (\n" +
				Contract.PlayerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_NAME + " TEXT NOT NULL, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_NUMBER + " INTEGER NOT NULL, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_TEAM_NAME + " TEXT NOT NULL, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_POSITION + " TEXT, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_BATS + " TEXT, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_THROWS + " TEXT, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_BATTING_AVERAGE + " REAL, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_RBI + " REAL, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_RUNS + " INTEGER, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_HITS + " INTEGER, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_STRIKE_OUTS + " INTEGER, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_WALKS + " INTEGER, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_SINGLE + " INTEGER, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_DOUBLE + " INTEGER, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_TRIPLE + " INTEGER, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_HOME_RUNS + " INTEGER, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_FLY_BALL + " INTEGER, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_GROUND_BALLS + " INTEGER, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_ON_BASE_PERCENTAGE + " REAL, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_BASES_STOLEN + " INTEGER, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_CAUGHT_STEALING + " INTEGER, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_ERRORS + " INTEGER, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_FIELD_PERCENTAGE + " REAL, \n" +
				Contract.PlayerEntry.COLUMN_PLAYER_PUT_OUTS + " INTEGER );";

		final String CREATE_PITCHER_TABLE = "CREATE TABLE " + Contract.PitcherEntry.TABLE_NAME + " (\n" +
				Contract.PitcherEntry.COLUMN_PITCHER_NAME + " TEXT, \n" +
				Contract.PitcherEntry.COLUMN_PITCHER_GAMES_STARTED + " INTEGER, \n" +
				Contract.PitcherEntry.COLUMN_PITCHER_INNINGS_PITCHED + " INTEGER, \n" +
				Contract.PitcherEntry.COLUMN_PITCHER_PITCHES + " INTEGER, \n" +
				Contract.PitcherEntry.COLUMN_PITCHER_STRIKES + " INTEGER, \n" +
				Contract.PitcherEntry.COLUMN_PITCHER_BALLS + " INTEGER, \n" +
				Contract.PitcherEntry.COLUMN_PITCHER_STRIKEOUTS + " INTEGER, \n" +
				Contract.PitcherEntry.COLUMN_PITCHER_WALKS + " INTEGER, \n" +
				Contract.PitcherEntry.COLUMN_PITCHER_HIT_BATTER + " INTEGER );";

		db.execSQL(CREATE_TEAM_TABLE);
		db.execSQL(CREATE_PLAYER_TABLE);
		db.execSQL(CREATE_PITCHER_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + Contract.TeamEntry.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Contract.PlayerEntry.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + Contract.PitcherEntry.TABLE_NAME);
		onCreate(db);
	}
}