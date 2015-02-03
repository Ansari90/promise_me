package aaa.promise_me;

import java.util.ArrayList;
import java.util.Calendar;

import aaa.promise_me.helpers.MonthAndDays;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PromiseDbHelper extends SQLiteOpenHelper {

	private SQLiteDatabase promiseDb;
	private int currentPromiseId;		//The most important variable in the entire program!!1!
	
	public static final int INT_NULL = -1;
	
	public static final String CREATE_IF_NOT_EXISTS = "CREATE TABLE IF NOT EXISTS ";
	
	public static final String CREATE_LOOKUP = PromiseContract.PromiseLookup.TABLE_NAME
									  + " (" + PromiseContract.PromiseLookup._ID + " INTEGER PRIMARY KEY,"
									  		 + PromiseContract.PromiseLookup.COLUMN_NAME + " TEXT UNIQUE ON CONFLICT ROLLBACK)";
	
	public static final String CREATE_DATA = " (" + PromiseContract.PromiseLookup._ID + " INTEGER PRIMARY KEY,"
												  + PromiseContract.PromiseData.COLUMN_DAY_WEEK + " INTEGER,"
												  + PromiseContract.PromiseData.COLUMN_DAY_MONTH + " INTEGER,"
												  + PromiseContract.PromiseData.COLUMN_MONTH + " INTEGER,"
												  + PromiseContract.PromiseData.COLUMN_YEAR + " INTEGER,"
												  + PromiseContract.PromiseData.COLUMN_YORN + " TEXT)";
	
	public static final String DELETE_PROMISE = "DROP TABLE IF EXISTS " + PromiseContract.PromiseLookup.TABLE_NAME;
	
	public static final String[] lookup_projection = { PromiseContract.PromiseLookup._ID,
													   PromiseContract.PromiseLookup.COLUMN_NAME };
	
	public static final String[] data_projection = { PromiseContract.PromiseData._ID,
													 PromiseContract.PromiseData.COLUMN_DAY_WEEK,
													 PromiseContract.PromiseData.COLUMN_DAY_MONTH,
													 PromiseContract.PromiseData.COLUMN_MONTH,
													 PromiseContract.PromiseData.COLUMN_YEAR,
													 PromiseContract.PromiseData.COLUMN_YORN };
	
	public PromiseDbHelper(Context context, String dbName, int version) {
		
		super(context, dbName, null, version);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL(CREATE_IF_NOT_EXISTS + CREATE_LOOKUP);		
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) { super.onOpen(db); }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
	
	public void initialise() {
		
		promiseDb = getWritableDatabase();
		currentPromiseId = INT_NULL;
	}
	
	public void close() {
		
		promiseDb.close();
	}
	
	public Cursor getLookupCursor() {

		return promiseDb.query( PromiseContract.PromiseLookup.TABLE_NAME,
								lookup_projection,
								null,
								null,
								null,
								null,
								null );
	}
	
	public Cursor getDataCursor() {

		Cursor dataCursor = null;
		if(currentPromiseId != INT_NULL) {
			dataCursor = promiseDb.query( PromiseContract.PromiseData.TABLE_NAME + currentPromiseId,
										  data_projection,
										  null,
										  null,
										  null,
										  null,
										  null );
		}
		
		return dataCursor;
	}
	
	public int getLookupId(Cursor lookupCursor) {
		
		return lookupCursor.getInt(lookupCursor.getColumnIndex(PromiseContract.PromiseLookup._ID));
	}
	
	public boolean isValid() {
		
		boolean validity = false;
		Cursor lookupCursor = getLookupCursor();
		
		if(lookupCursor.moveToFirst()) {
			do {
				if(currentPromiseId == getLookupId(lookupCursor)) {
					validity = true;
					break;
				}
			} while(lookupCursor.moveToNext());
		}
		
		lookupCursor.close();
		return validity;
	}
	
	public void setPromiseId(String promiseText) {
		
		Cursor lookupCursor = getLookupCursor();
		currentPromiseId = INT_NULL;
		
		if(lookupCursor.moveToFirst()) {
			do {
				if(lookupCursor.getString(lookupCursor.getColumnIndex(PromiseContract.PromiseLookup.COLUMN_NAME)).equals(promiseText)) {
					currentPromiseId = getLookupId(lookupCursor);
				}
			} while(lookupCursor.moveToNext());
		}
		
		lookupCursor.close();
	}
	
	public void setPromiseId() {
		
		Cursor lookupCursor = getLookupCursor();
		boolean matched = false;
		
		if(lookupCursor.moveToFirst()) {			
			do {
				currentPromiseId = getLookupId(lookupCursor);
				
				if(checkAnswer() == null) {
					matched = true;
					break; 
				}
			} while(lookupCursor.moveToNext());
		}
		
		if(!matched) {
			currentPromiseId = INT_NULL;
		}
		lookupCursor.close();
	}
	
	public boolean changePromise(String direction) {
		
		boolean idChanged = false;
		Cursor lookupCursor = getLookupCursor();
		
		if(lookupCursor.moveToFirst()) {
			do {
				if(getLookupId(lookupCursor) == currentPromiseId) {
					int moveAmount = 0, previousId = currentPromiseId;
					
					if(direction.equals(MainActivity.NEXT)) {
						moveAmount = 1;
					}
					else {
						moveAmount = -1;
					}
					
					while(lookupCursor.move(moveAmount)) {
						currentPromiseId = getLookupId(lookupCursor);
						
						if(checkAnswer() == null) {
							idChanged = true;
							break;
						}
					}
					
					if(!idChanged) { currentPromiseId = previousId; };
					break;
				}
			} while(lookupCursor.moveToNext());
		}		
		
		lookupCursor.close();
		return idChanged;
	}
	
	public String checkAnswer() {
		
		String yorn = null;		
		Cursor dataCursor = getDataCursor();
		
		if(dataCursor != null && dataCursor.moveToFirst()) {
			Calendar date = Calendar.getInstance();
			do {			
				if(dataCursor.getInt(dataCursor.getColumnIndex(PromiseContract.PromiseData.COLUMN_DAY_MONTH)) == date.get(Calendar.DAY_OF_MONTH)
				&& dataCursor.getInt(dataCursor.getColumnIndex(PromiseContract.PromiseData.COLUMN_MONTH)) == date.get(Calendar.MONTH)
				&& dataCursor.getInt(dataCursor.getColumnIndex(PromiseContract.PromiseData.COLUMN_YEAR)) == date.get(Calendar.YEAR)) {
					yorn = dataCursor.getString(dataCursor.getColumnIndex(PromiseContract.PromiseData.COLUMN_YORN));
					break;
				}
			} while(dataCursor.moveToNext());
			
			dataCursor.close();
		}	
		
		return yorn;
	}
	
	public String getPromiseText() {
		
		Cursor lookupCursor = getLookupCursor();
		String responseString = null;
		
		if(lookupCursor.moveToFirst()) {
			do {
				if((getLookupId(lookupCursor) == currentPromiseId)) {
					responseString = lookupCursor.getString(lookupCursor.getColumnIndex(PromiseContract.PromiseLookup.COLUMN_NAME));
					break;
				}
			} while(lookupCursor.moveToNext());		
		}
		
		lookupCursor.close();
		return responseString;
	}

	
	public void createPromise(String promiseText) {
		
		ContentValues promiseLookupValues = new ContentValues();
		promiseLookupValues.put(PromiseContract.PromiseLookup.COLUMN_NAME, promiseText);
		
		currentPromiseId = (int)promiseDb.insert(PromiseContract.PromiseLookup.TABLE_NAME, null, promiseLookupValues);
		promiseDb.execSQL(PromiseDbHelper.CREATE_IF_NOT_EXISTS 
						+ PromiseContract.PromiseData.TABLE_NAME + currentPromiseId
						+ PromiseDbHelper.CREATE_DATA);
	}
	
	public void insertPromiseAnswer(String yorn) {
		
		if(currentPromiseId != INT_NULL && checkAnswer() == null) {
			ContentValues promiseAnswerValues = new ContentValues();
			Calendar currentDate = Calendar.getInstance();
			
			promiseAnswerValues.put(PromiseContract.PromiseData.COLUMN_DAY_WEEK, currentDate.get(Calendar.DAY_OF_WEEK));
			promiseAnswerValues.put(PromiseContract.PromiseData.COLUMN_DAY_MONTH, currentDate.get(Calendar.DAY_OF_MONTH));
			promiseAnswerValues.put(PromiseContract.PromiseData.COLUMN_MONTH, currentDate.get(Calendar.MONTH));
			promiseAnswerValues.put(PromiseContract.PromiseData.COLUMN_YEAR, currentDate.get(Calendar.YEAR));
			promiseAnswerValues.put(PromiseContract.PromiseData.COLUMN_YORN, yorn);
			
			promiseDb.insert(PromiseContract.PromiseData.TABLE_NAME + currentPromiseId, null, promiseAnswerValues);
		}
	}
	
	public void deletePromise() {
		
		String selection = PromiseContract.PromiseLookup._ID + " = ?";
		String[] selectionArgs = { String.valueOf(currentPromiseId) };
		
		promiseDb.delete(PromiseContract.PromiseLookup.TABLE_NAME, selection, selectionArgs);
		promiseDb.execSQL(PromiseDbHelper.DELETE_PROMISE + currentPromiseId);
		
		setPromiseId();
	}
	
	public ArrayList<MonthAndDays> getPromiseData() {
		
		Cursor dataCursor = getDataCursor();
		ArrayList<MonthAndDays> promiseData = new ArrayList<MonthAndDays>();
		promiseData.add(new MonthAndDays(getPromiseText()));
		
		if(dataCursor != null && dataCursor.moveToFirst()) {
			MonthAndDays tempMaD = getPreparedMonthAndDays(dataCursor);
					
			do {
				if(!tempMaD.getIdCombo().equals(getPreparedMonthAndDays(dataCursor).getIdCombo())) {
					tempMaD = getPreparedMonthAndDays(dataCursor);
				}
				if(!promiseData.contains(tempMaD)) {
					promiseData.add(tempMaD);
				}
				
				if(dataCursor.getString(dataCursor.getColumnIndex(PromiseContract.PromiseData.COLUMN_YORN)).equals(MainActivity.YES) ? true : false) {
					tempMaD.addYesDay(dataCursor.getInt(dataCursor.getColumnIndex(PromiseContract.PromiseData.COLUMN_DAY_MONTH)));
				}
				else {
					tempMaD.addNoDay(dataCursor.getInt(dataCursor.getColumnIndex(PromiseContract.PromiseData.COLUMN_DAY_MONTH)));
				}
			} while(dataCursor.moveToNext());
			
			dataCursor.close();
		}
		
		return promiseData;
	}
	
	public MonthAndDays getPreparedMonthAndDays(Cursor dataCursor) {
		
		return new MonthAndDays(dataCursor.getInt(dataCursor.getColumnIndex(PromiseContract.PromiseData.COLUMN_MONTH)),
								dataCursor.getInt(dataCursor.getColumnIndex(PromiseContract.PromiseData.COLUMN_YEAR)));
	}
}
