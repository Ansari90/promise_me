package aaa.promise_me;

import android.provider.BaseColumns;

public final class PromiseContract {
	
	public PromiseContract() {}
	
	public static abstract class PromiseLookup implements BaseColumns {
		public static final String TABLE_NAME = "lookup";
		public static final String COLUMN_NAME = "name";
	}
	
	public static abstract class PromiseData implements BaseColumns {
		public static final String TABLE_NAME = "promise";
		public static final String COLUMN_DAY_WEEK = "day";
		public static final String COLUMN_DAY_MONTH = "dayNum";
		public static final String COLUMN_MONTH = "month";
		public static final String COLUMN_YEAR = "year";
		public static final String COLUMN_YORN = "yorn";
	}
}