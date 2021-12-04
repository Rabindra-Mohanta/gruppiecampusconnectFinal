package school.campusconnect.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import school.campusconnect.activities.GroupDashboardActivityNew;
import school.campusconnect.utils.AppLog;

import java.util.ArrayList;

import school.campusconnect.datamodel.PhoneContactsItems;
import school.campusconnect.utils.Constants;

public class DatabaseHandler extends SQLiteOpenHelper {

    // Table Fields
    public static final String KEY_ID = "id";
    public static final String KEY_NAME = "name";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_INVITED = "invited";

    // Database Name
    public static final String DATABASE_NAME = "gruppie";

    // Database Version
    public static final int DATABASE_VERSION = 1;

    // TABLES
    public static final String TABLE_CONTACTS = "contacts_tbl";

    // Create Tables
    private static final String CREATE_CONTACTS_TABLE = "create table "
            + TABLE_CONTACTS + "( " + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " text,"
            + KEY_PHONE + " text," + KEY_INVITED + " INTEGER DEFAULT 0" /*+ KEY_USING_GRUPPIE + " INTEGER DEFAULT 0"*/ + ")";
    private static final String TAG = "DatabaseHandler";

    SQLiteDatabase db;
    // Constructor
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    public void addContacts(String name, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cVal = new ContentValues();
        cVal.put(KEY_NAME, name);
        cVal.put(KEY_PHONE, phone);
        db.insert(TABLE_CONTACTS, null, cVal);
       AppLog.e("DATA,", name + ", " + phone);
        db.close();
    }

    public void addContacts(ArrayList<PhoneContactsItems> conatacs)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        for (PhoneContactsItems items :
                conatacs) {
            ContentValues cVal = new ContentValues();
            cVal.put(KEY_NAME, items.getName());
            cVal.put(KEY_PHONE, items.getPhone());
            db.insert(TABLE_CONTACTS, null, cVal);
           AppLog.e("DATA,", items.getName() + ", " + items.getPhone());
        }
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from " + TABLE_CONTACTS);
        db.close();
    }

    public ArrayList<PhoneContactsItems> getContacts() {
        ArrayList<PhoneContactsItems> list = new ArrayList<PhoneContactsItems>();
        String selectQuery = "SELECT  * FROM contacts_tbl WHERE invited=0 ORDER BY " + KEY_NAME + " COLLATE NOCASE ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PhoneContactsItems item = new PhoneContactsItems();
                item.setName(cursor.getString(1));
                item.setPhone(cursor.getString(2));
                list.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return list;
    }

    public ArrayList<PhoneContactsItems> getAllContacts() {
        ArrayList<PhoneContactsItems> list = new ArrayList<PhoneContactsItems>();
        String selectQuery = "SELECT  * FROM contacts_tbl ORDER BY " + KEY_NAME + " COLLATE NOCASE ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PhoneContactsItems item = new PhoneContactsItems();
                item.setName(cursor.getString(1));
                item.setPhone(cursor.getString(2));
                list.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<String> getPhone() {
        ArrayList<String> list = new ArrayList<>();
        String selectQuery = "SELECT phone FROM contacts_tbl WHERE invited=0";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public ArrayList<PhoneContactsItems> getSearchedList(String search) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<PhoneContactsItems> list = new ArrayList<PhoneContactsItems>();
        try {
            // Cursor mCursor = db.query(TABLE_CONTACTS, new String[]{KEY_NAME, KEY_PHONE}, KEY_NAME + " LIKE ?",
            // new String[]{search}, null, null, null, null);
            String query = "select " + KEY_NAME + " , " + KEY_PHONE + " from " + TABLE_CONTACTS + " where " + KEY_NAME + " like \"%" + search + "%\""
                    + " AND " + KEY_INVITED + "=0" +/* " AND " + KEY_USING_GRUPPIE +*/ " ORDER BY " + KEY_NAME + " COLLATE NOCASE ASC";
//           AppLog.e("DatabaseHandler", " query : " + query);

            Cursor mCursor = db.rawQuery(query, null);
            if (mCursor != null) {
                mCursor.moveToFirst();
                for (int i = 0; i < mCursor.getCount(); i++) {
                    PhoneContactsItems item = new PhoneContactsItems();
                    item.setName(mCursor.getString(0));
                    item.setPhone(mCursor.getString(1));
                    list.add(item);
                    mCursor.moveToNext();
                }
                mCursor.close();
                return list;
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public void updateContact(String number) {

        if (number.length() > 3) {
            String desiredString1 = number.substring(0, 3);
            if (desiredString1.equals("+91"))
                number = number.substring(3);
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_INVITED, 1);

        String query = "UPDATE " + TABLE_CONTACTS + " SET " + KEY_INVITED + " = 1  WHERE " + KEY_PHONE + " LIKE \"%" + number + "%\"";
        db.execSQL(query);
       AppLog.e("DatabaseHandler", "query : " + query);
        // db.rawQuery(query ,null);
//       AppLog.e("DatabaseHandler", "Rows Updtaed " + db.update(TABLE_CONTACTS, values, KEY_PHONE + " = ?", new String[]{phone}));
    }

    public int getCount() {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            long cnt = DatabaseUtils.queryNumEntries(db, TABLE_CONTACTS);
            db.close();
            return (int) cnt;
        } catch (Exception e) {
           AppLog.e("DB", "getCount: exception called " + e.toString());
            return 0;
        }
        /*String countQuery = "SELECT  * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;*/
    }

    public String getNameFromNum(String num) {
        AppLog.e(TAG,"getNameFromNum :"+num);
        if (num.equals("") || GroupDashboardActivityNew.groupCategory.equalsIgnoreCase(Constants.CATEGORY_SCHOOL) || GroupDashboardActivityNew.groupCategory.equalsIgnoreCase(Constants.CATEGORY_CONSTITUENCY))
            return "";

        if (num.length() > 3) {
            String desiredString1 = num.substring(0, 3);
            if (desiredString1.equals("+91"))
                num = num.substring(3);
        }

        String query = "select " + KEY_NAME + " from " + TABLE_CONTACTS + " where REPLACE(phone, ' ', '')" + " like \"%" + num + "\"";
        String name = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, new String[]{});
        cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            do {
                name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            } while (cursor.moveToNext());
        }
        cursor.close();
        AppLog.e(TAG,"name in DB :"+name);
        return name;
    }

    public String getNameFromNumFirstTime(String num) {

        if (num.equals(""))
            return "";

        if (num.length() > 3) {
            String desiredString1 = num.substring(0, 3);
            if (desiredString1.equals("+91"))
                num = num.substring(3);
        }

        SQLiteDatabase db = this.getWritableDatabase();

        String queryUpdate = "UPDATE " + TABLE_CONTACTS + " SET " + KEY_INVITED + " = 1 WHERE REPLACE(phone, ' ', '')" + " LIKE \"%" + num + "\"";

        db.execSQL(queryUpdate);

       AppLog.e("DatabaseHandler", "query : " + queryUpdate);
          String query = "select " + KEY_NAME + " from " + TABLE_CONTACTS + " where REPLACE(phone, ' ', '')" + " like \"%" + num + "\"";

        String name = "";

        Cursor cursor = db.rawQuery(query, new String[]{});
        cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            do {
                name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return name;

    }
    public String getNameFromNumFirstTime2(String num) {

        if (num.equals(""))
            return "";

        if (num.length() > 3) {
            String desiredString1 = num.substring(0, 3);
            if (desiredString1.equals("+91"))
                num = num.substring(3);
        }

        String queryUpdate = "UPDATE " + TABLE_CONTACTS + " SET " + KEY_INVITED + " = 1 WHERE REPLACE(phone, ' ', '')" + " LIKE \"%" + num + "\"";

        db.execSQL(queryUpdate);

       AppLog.e("DatabaseHandler", "query : " + queryUpdate);
        String query = "select " + KEY_NAME + " from " + TABLE_CONTACTS + " where REPLACE(phone, ' ', '')" + " like \"%" + num + "\"";

        String name = "";

        Cursor cursor = db.rawQuery(query, new String[]{});
        cursor.moveToFirst();

        if (cursor.moveToFirst()) {
            do {
                name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return name;

    }

    public void updateYourNum(String num) {
        try {
            if (num.equals(""))
                return;

            if (num.length() > 3) {
                String desiredString1 = num.substring(0, 3);
                if (desiredString1.equals("+91"))
                    num = num.substring(3);
            }
            SQLiteDatabase db = this.getWritableDatabase();
        /*ContentValues values = new ContentValues();
        values.put(KEY_USING_GRUPPIE, using);*/

            String queryUpdate = "UPDATE " + TABLE_CONTACTS + " SET " + KEY_INVITED + " = " + 1 + "  WHERE REPLACE(phone, ' ', '')" + " LIKE \"%" + num + "%\"";

            db.execSQL(queryUpdate);
        } catch (NullPointerException e) {
           AppLog.e("updateYourNum", "error is " + e.toString());
        }

    }

    public void storeWritableDBObject() {
            db = this.getWritableDatabase();
            db.beginTransaction();
    }
    public void stopTransaction()
    {
        db.setTransactionSuccessful();
        db.endTransaction();

    }

    public ArrayList<PhoneContactsItems> getSearchedListTeam(String search) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<PhoneContactsItems> list = new ArrayList<PhoneContactsItems>();
        try {
            // Cursor mCursor = db.query(TABLE_CONTACTS, new String[]{KEY_NAME, KEY_PHONE}, KEY_NAME + " LIKE ?",
            // new String[]{search}, null, null, null, null);
            String query = "select " + KEY_NAME + " , " + KEY_PHONE + " from " + TABLE_CONTACTS + " where " + KEY_NAME + " like \"%" + search + "%\""
                    +/* " AND " + KEY_USING_GRUPPIE +*/ " ORDER BY " + KEY_NAME + " COLLATE NOCASE ASC";
//           AppLog.e("DatabaseHandler", " query : " + query);

            Cursor mCursor = db.rawQuery(query, null);
            if (mCursor != null) {
                mCursor.moveToFirst();
                for (int i = 0; i < mCursor.getCount(); i++) {
                    PhoneContactsItems item = new PhoneContactsItems();
                    item.setName(mCursor.getString(0));
                    item.setPhone(mCursor.getString(1));
                    list.add(item);
                    mCursor.moveToNext();
                }
                mCursor.close();
                return list;
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
