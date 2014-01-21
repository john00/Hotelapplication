package personal.john.GeoSearcher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GeoSearcherDB {
	GeoOpenHelper dbGeoOpenHelper;

	/* DB */
	static final String DB_NAME = "GeoSearcherDB.db";
	static final String DB_TABLE = "GeoTable";
	static final String DB_GEONAME = "GeoName";
	static final int DB_VERSION = 1;
	static final String CREATE_TABLE = "create table " + DB_TABLE +
										"(" + DB_GEONAME + " text primary key)";
	public SQLiteDatabase mDb;

	public GeoSearcherDB(Context context) {
		dbGeoOpenHelper = new GeoOpenHelper(context);
	}

	public void GeoSearcherDBOpen() {
		try {
			mDb = dbGeoOpenHelper.getWritableDatabase();
		} catch (SQLException e) {
			Log.e("app", e.toString());
			mDb = dbGeoOpenHelper.getReadableDatabase();
		}
	}

	public void GeoSearcherDBClose() {
		mDb.close();
	}

	public SQLiteDatabase getSQLiteDatabase() {
		return mDb;
	}

//	public GeoOpenHelper getGeoSearcherDB() {
//		return dbGeoOpenHelper;
//	}

	private void addGeoName(String name) {
		ContentValues values = new ContentValues();
		values.put(DB_GEONAME, name);
		mDb.insert(DB_TABLE, null, values);
	}

	public boolean checkGeoName(String name) {
		final String[] projection = new String[] { DB_GEONAME };
//		final int NAME_INDEX = 1;

		String where = DB_GEONAME + " = ?";
//		String param = name.substring(0, 1) + "%";

		Cursor cursor = mDb.query(DB_TABLE, projection, where, new String[] { name }, null, null, null);
		if (cursor.getCount() == 0) {
			addGeoName(name);
			return false;
		}

//		if (cursor.moveToFirst()) {
//			do {
//				String geoname = cursor.getString(NAME_INDEX);
//				if (name == geoname) {
//					cursor.close();
//					return true;
//				}
//			} while (cursor.moveToNext());
//		}
//		addGeoName(name);
		cursor.close();
		return true;
	}

	public SQLiteDatabase getGSDB() {
		return mDb;
	}

    public static class GeoOpenHelper extends SQLiteOpenHelper {

		public GeoOpenHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
    }
}
