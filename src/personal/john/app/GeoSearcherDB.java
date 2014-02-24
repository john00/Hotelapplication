package personal.john.app;

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
	static final String DB_HOTELNAME = "HotelName";
	static final String DB_ARRIVED = "Arrived";
	static final String DB_MEMO = "memo";
	static final int DB_VERSION = 2;
	static final String CREATE_TABLE = "create table if not exists " + DB_TABLE +
										"(" + DB_HOTELNAME + " text primary key, " + DB_ARRIVED + "," + DB_MEMO + ")";
	private SQLiteDatabase mDb;

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

	// ���K�E���K�������݃��\�b�h
	public void writeArrivedData(String strHotelID, int iArraived) {
		ContentValues values = new ContentValues();
		// �������݃f�[�^�쐬
		values.put(DB_HOTELNAME, strHotelID);
		values.put(DB_ARRIVED, iArraived);
		
		// �f�[�^�������݁iUpdate�j
		int iRet = mDb.update(DB_TABLE, values, null, null);
		if (iRet == 0) {
			mDb.insert(DB_TABLE, null, values);
		}
	}
	
	// ���K�E���K�ǂݍ��݃��\�b�h
	public int readArrivedData(String strHotelID){
		final int RET_ARRIVED = 1;
		final int RET_NOT_ARRIVED = 0;
		// �J�[�\���쐬
		Cursor cursor = mDb.query(DB_TABLE, new String[]{DB_HOTELNAME, DB_ARRIVED},
				DB_HOTELNAME + "=" + strHotelID, null, null, null, null);
		
		// �q�b�g���R�[�h�� 0���̏ꍇ
		if (cursor.getCount() <= 0) {
			cursor.close();
			return RET_NOT_ARRIVED;
		}

		cursor.moveToFirst();
		
		// �q�b�g���R�[�h������ꍇ
		int iRet = cursor.getInt(1);
		cursor.close();
		
		if (iRet == RET_NOT_ARRIVED) return RET_NOT_ARRIVED;
		
		return RET_ARRIVED;
	}

	// �����������݃��\�b�h
	public void writeMemoData(String strHotelID, String strMemo) {
		ContentValues values = new ContentValues();
		// �������݃f�[�^�쐬
		values.put(DB_HOTELNAME, strHotelID);
		values.put(DB_MEMO, strMemo);
		
		// �f�[�^�������݁iUpdate�j
		int iRet = mDb.update(DB_TABLE, values, null, null);
		if (iRet == 0) {
			mDb.insert(DB_TABLE, null, values);
		}
	}
	// �����ǂݍ��݃��\�b�h
	public String readMemoData(String strHotelID){
		// �J�[�\���쐬
		Cursor cursor = mDb.query(DB_TABLE, new String[]{DB_HOTELNAME, DB_MEMO},
				DB_HOTELNAME + "=" + strHotelID, null, null, null, null);
		
		// �q�b�g���R�[�h�� 0���̏ꍇ
		if (cursor.getCount() <= 0) {
			cursor.close();
			return "";
		}

		cursor.moveToFirst();
		
		// �q�b�g���R�[�h������ꍇ
		String strRet = cursor.getString(1);
		cursor.close();
		
		return strRet;
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
			db.execSQL(CREATE_TABLE);
		}
    }
}
