package co.zenpets.location;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBResto {
	
	/** A CONTEXT INSTANCE **/
    private final Context context;
	
	/** A REFERENCE TO THE DATABASE USED BY FACEBOOK NOTIFICATIONS **/
    private SQLiteDatabase db;
	
	/** THE HELPER **/
    private DBSQLHelper helper = null;

	/** DATABASE GLOBAL NAME AND VERSION **/
	private final String DB_NAME = "zenpets.db";
	private final int DB_VERSION = 1;

	/*****	THE TABLE NAMES	*****/
	public final String LOCALITIES = "localities";

    /* RESTAURANT */
    public final String LOCALITY_ID = "localityID";
    public final String CITY_ID = "cityID";
    public final String LOCALITY_NAME = "localityName";

    /** ADD A LOCALITY TO THE DATABASE **/
    public void addLocality(String cityID, String localityName)	{

		/* OPEN THE DATABASE AGAIN */
        this.db = helper.getWritableDatabase();

        /* ADD AND CREATE KEY VALUE PAIRS FOR ADDING A NEW COUNTRY TO THE DATABASE */
        ContentValues values = new ContentValues();
        values.put(CITY_ID, cityID);
        values.put(LOCALITY_NAME, localityName);

		/* INSERT THE COLLECTED DATA TO THE LOCALITIES TABLE */
        db.insert(LOCALITIES, null, values);
    }


    /***************** CLOSE THE DATABASE FROM VARIOUS ACTIVITES WHEN DONE WITH IT ****************/
	public void close() {
		helper.close();
	}
	
	/***** CREATE OR OPEN THE DATABASE *****/
	public DBResto(Context context) {
		this.context = context;
		
		// CREATE OR OPEN THE DATABASE
		helper = new DBSQLHelper(context);
		this.db = helper.getWritableDatabase();
	}

    /*****	PRIVATE CLASS TO CREATE AND OR OPEN DATABASE	*****/
	private class DBSQLHelper extends SQLiteOpenHelper {

		public DBSQLHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {

			/* CREATE THE LOCALITIES TABLE */
            createLocalitiesTable(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}

    /** CREATE LOCALITIES TABLE **/
    private void createLocalitiesTable(SQLiteDatabase db) {

        String s = "create table " + LOCALITIES +
                " (" +
                LOCALITY_ID + " integer primary key autoincrement, " +
                CITY_ID + " text, " +
                LOCALITY_NAME + " text, " +
                "UNIQUE" + " (" + LOCALITY_ID + " )" + ");";
        db.execSQL(s);
    }
}