package basiclocationsample.sample.location.gms.android.google.com.explorer;

/**
 * Created by rafzz on 19.03.2017.
 */


    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteOpenHelper;

    public class Database extends SQLiteOpenHelper {

        private static final String TABLE_NAME = "area";
        private static final String PK_NAME = "id";
        private static final String NAME = "name";
        private static final String LATITUDE = "latitude";
        private static final String LONGITUDE = "longitude";
        private static final String ZOOM = "zoom";
        private static final String DB_NAME = "explorerData.db";



        public Database(Context context) {
            super(context, DB_NAME, null, 5);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {


            db.execSQL(
                    "create table " + TABLE_NAME + "(" +
                            PK_NAME + " integer primary key autoincrement," +
                            NAME + "text,"+
                            LONGITUDE + " double," +
                            LATITUDE + " double," +
                            ZOOM + " float " + ");" +
                            "");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // If you need to add a column
            if (newVersion > oldVersion) {
                db.execSQL("ALTER TABLE area ADD COLUMN name text");

            }
        }

        public void addData(double latitude, double longitude, float zoom, String name) {
            SQLiteDatabase dataBase = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(this.NAME, name);
            values.put(this.LATITUDE, latitude);
            values.put(this.LONGITUDE, longitude);
            values.put(this.ZOOM, zoom);
            dataBase.insertOrThrow(TABLE_NAME, null, values);
        }

        public Cursor writeAllData() {
            String[] column = {PK_NAME, NAME, LATITUDE, LONGITUDE, ZOOM};
            SQLiteDatabase dataBase = getReadableDatabase();
            Cursor cursor = dataBase.query(TABLE_NAME, column, null, null, null, null, null);
            return cursor;
        }

        @Deprecated
        public Cursor writeFirstData() {
            String[] column = {PK_NAME, LATITUDE, LONGITUDE, ZOOM};
            SQLiteDatabase dataBase = getReadableDatabase();
            //TODO
            Cursor cursor = dataBase.query(TABLE_NAME, column, "id==1", null, null, null, null);

            return cursor;
        }

        public void removeAllData() {
            SQLiteDatabase dataBase = getWritableDatabase();
            dataBase.delete(TABLE_NAME, null, null);
        }

        public void updateData(int id, double latitude, double longitude, float zoom, String name) {
            SQLiteDatabase dataBase = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(this.NAME, name);
            values.put(this.LATITUDE, latitude);
            values.put(this.LONGITUDE, longitude);
            values.put(this.ZOOM, zoom);

            String[] args = {"" + id};
            dataBase.update(TABLE_NAME, values, PK_NAME + "=?", args);
        }

        public void removeData(int id) {
            SQLiteDatabase dataBase = getWritableDatabase();
            String[] args = {"" + id};
            dataBase.delete(TABLE_NAME, PK_NAME + "=?", args);

        }

}
