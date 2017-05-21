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
        private static final String PATH = "path";
        private static final String MARKERS = "markers";
        private static final String DB_NAME = "explorerData.db";

        //NEED TO INCREMENT BEFORE NEW INSTALATION OF APP
        private static final int VERSION = 29;

        public Database(Context context) {
            super(context, DB_NAME, null,VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(
                    "create table " + TABLE_NAME + "(" +
                            PK_NAME + " integer primary key autoincrement," +
                            NAME + "text,"+
                            LONGITUDE + " double," +
                            LATITUDE + " double," +
                            ZOOM + " float " +
                            PATH + " text " +
                            MARKERS + " text " +");" +
                            "");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // If you need to add a column
            if (newVersion > oldVersion) {
                //db.execSQL("ALTER TABLE area ADD COLUMN name text");
                //db.execSQL("ALTER TABLE area ADD COLUMN path text");
                //db.execSQL("ALTER TABLE area ADD COLUMN markers text");
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
            String[] column = {PK_NAME, NAME, LATITUDE, LONGITUDE, ZOOM, PATH, MARKERS};
            SQLiteDatabase dataBase = getReadableDatabase();
            Cursor cursor = dataBase.query(TABLE_NAME, column, null, null, null, null, null);
            return cursor;
        }

        public void updateData(int id, String path ) {
            SQLiteDatabase dataBase = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(this.PATH, path);
            String[] args = {"" + id};
            dataBase.update(TABLE_NAME, values, PK_NAME + "=?", args);
        }

        public void updateMarkers(int id, String markers ) {
            SQLiteDatabase dataBase = getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MARKERS, markers);
            String[] args = {"" + id};
            dataBase.update(TABLE_NAME, values, PK_NAME + "=?", args);
        }

        public void removeData(int id) {
            SQLiteDatabase dataBase = getWritableDatabase();
            String[] args = {"" + id};
            dataBase.delete(TABLE_NAME, PK_NAME + "=?", args);
        }

}
