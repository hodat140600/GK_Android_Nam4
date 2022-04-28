package com.example.myapplication.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.example.myapplication.Entities.PhongKho;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PhongKhoDatabase extends SQLiteOpenHelper {
    private static final String TAG = "SQLite";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "QLPK.db";

    // Database Path
    private static String DATABASE_PATH;
    public Context mContext;
    // Table name: Note.
    public static final String TABLE_NAME = "PHONGKHO";

    //public static final String COLUMN_ID ="ID";
    public static final String COLUMN_MAPK = "MAPK";
    public static final String COLUMN_TENPK = "TENPK";

    public PhongKhoDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        if (Build.VERSION.SDK_INT >= 17) {
            DATABASE_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DATABASE_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        this.mContext = context;
    }
    private Boolean checkDataBase(){
        File dbFile = new File( DATABASE_PATH + DATABASE_NAME);
        return dbFile.exists();
    }
    private void copyDataBase() throws Exception{
        InputStream mInput = mContext.getAssets().open(DATABASE_NAME);
        String outFileName = DATABASE_PATH + DATABASE_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0){
            mOutput.write(mBuffer,0,mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }
    public void createDataBase(){
        //if Db doesn't exist then copy it from assets.
        boolean mDataBaseExist = checkDataBase();
        if(!mDataBaseExist){
            this.getReadableDatabase();
            this.close();
            try {
                //copy Db from assets
                copyDataBase();
                Log.e("TAG", "Create DataBase");
            }catch (IOException mIOException){
                throw new Error("ErrorCopyDataBase");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private SQLiteDatabase mDataBase;
    //open DB so can query it
    public boolean openDataBase() throws Exception{
        String mPath = DATABASE_PATH + DATABASE_NAME;
        mDataBase = SQLiteDatabase.openDatabase(mPath, null, SQLiteDatabase.CREATE_IF_NECESSARY);

        return mDataBase != null;
    }

    //    public synchronized void Close(){
//        if (mDataBase != null){
//            mDataBase.close();
//        }
//        super.close();
//    }
    public void dropTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(oldVersion);
    }
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Script to create table.
        String script = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("
//                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + COLUMN_MAPK + " TEXT PRIMARY KEY NOT NULL ,"
                + COLUMN_TENPK + " TEXT NOT NULL" + ");";
        // Execute script.
        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Recreate

        onCreate(db);
    }

    public List<PhongKho> reset() {
        dropTable();
        insert(new PhongKho("PK01", "Phòng kho Thủ Đức"));
        insert(new PhongKho("PK02", "Phòng kho Bình Chánh"));
        insert(new PhongKho("PK03", "Phòng kho Tân Phú"));
        insert(new PhongKho("PK04", "Phòng kho Nhà Bè"));
        return select();
    }

    public List<PhongKho> select() {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_MAPK,
                COLUMN_TENPK
        };

        String sortOrder = null;

        Cursor cursor = db.query(
                TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        List<PhongKho> list_phongkho = new ArrayList<>();

        while (cursor.moveToNext()) {
            list_phongkho.add(new PhongKho(
                    cursor.getString(0), cursor.getString(1)));
        }
        return list_phongkho;
    }

    public long insert(PhongKho phongKho) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PhongKhoDatabase.COLUMN_MAPK, phongKho.getMapk());
        values.put(PhongKhoDatabase.COLUMN_TENPK, phongKho.getTenpk());

        return db.insert(PhongKhoDatabase.TABLE_NAME, null, values);
    }

    public long update(PhongKho phongKho) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PhongKhoDatabase.COLUMN_MAPK, phongKho.getMapk());
        values.put(PhongKhoDatabase.COLUMN_TENPK, phongKho.getTenpk());

        // db.update ( Tên bảng, tập giá trị mới, điều kiện lọc, tập giá trị cho điều kiện lọc );
        return db.update(
                PhongKhoDatabase.TABLE_NAME
                , values
                , PhongKhoDatabase.COLUMN_MAPK + "=?"
                , new String[]{String.valueOf(phongKho.getMapk())}
        );
    }

    public long delete(PhongKho phongKho) {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete ( Tên bàng, string các điều kiện lọc - dùng ? để xác định, string[] từng phần tử trong string[] sẽ nạp vào ? );
        //db.beginTransaction();
        try{
            return db.delete(
                    PhongKhoDatabase.TABLE_NAME
                    , COLUMN_MAPK + "=?"
                    , new String[]{String.valueOf(phongKho.getMapk())}
            );
        }catch (Exception e){
            Log.d(TAG, "Error while trying to delete PHONG KHO");
            return -1;
        }finally {
            //db.endTransaction();
        }
    }

    public long deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PhongKhoDatabase.TABLE_NAME, null, null);
    }

}
