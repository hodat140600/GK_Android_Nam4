package com.example.myapplication.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.example.myapplication.Entities.PhieuNhap;
import com.example.myapplication.Entities.NhanVien;
import com.example.myapplication.Entities.PhongKho;
import com.example.myapplication.Entities.VatTu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PhieuNhapDatabase extends SQLiteOpenHelper {
    private static final String TAG = "SQLite";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "QLPK.db";

    // Database Path
    private static String DATABASE_PATH;
    public Context mContext;
    // Table name: Note.
    private static final String TABLE_NAME = "CAPPHAT";

    public static final String COLUMN_SOPHIEU ="SOPHIEU";
    public static final String COLUMN_NGAYCAP = "NGAYLAP";
    public static final String COLUMN_MAK = "MAPK";


    public PhieuNhapDatabase(Context context)  {
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
    public void dropTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
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
                + COLUMN_SOPHIEU + " TEXT PRIMARY KEY,"
                + COLUMN_NGAYCAP + " TEXT NOT NULL,"
                + COLUMN_MAK + " TEXT NOT NULL,"
                + "FOREIGN KEY("+COLUMN_MAK+") REFERENCES PHONGKHO("+COLUMN_MAK+")"
                + "ON UPDATE RESTRICT "
                + "ON DELETE RESTRICT );";

        // Execute script.
        db.execSQL(script);
    }
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.setVersion(oldVersion);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        // Recreate
        onCreate(db);
    }

    public List<PhieuNhap> reset(){
        dropTable();
        insert(new PhieuNhap("PHIEU1","2018-08-25","PK01"));
        insert(new PhieuNhap("PHIEU2","2018-08-25","PK02"));
        insert(new PhieuNhap("PHIEU3","2018-08-25","PK01"));
        insert(new PhieuNhap("PHIEU4","2019-02-24","PK03"));
        insert(new PhieuNhap("PHIEU5","2018-10-30","PK04"));
        insert(new PhieuNhap("PHIEU6","2020-05-07","PK01"));
        insert(new PhieuNhap("PHIEU7","2020-05-07","PK02"));
        insert(new PhieuNhap("PHIEU8","2020-02-07","PK04"));
        insert(new PhieuNhap("PHIEU9","2018-02-09","PK01"));
        return select();
    }

    public List<PhieuNhap> select(){
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                COLUMN_SOPHIEU,
                COLUMN_NGAYCAP,
                COLUMN_MAK
        };

        // How you want the results sorted in the resulting Cursor
//        String sortOrder = PhongBanDatabase.COLUMN_ID + " DESC";
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

        List<PhieuNhap> list_capphat = new ArrayList<>();

        while(cursor.moveToNext()){
            list_capphat.add(new PhieuNhap(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2)
            ));
        }

        return list_capphat;
    }

    public List<PhieuNhap> select(PhongKho phongKho){
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                COLUMN_SOPHIEU,
                COLUMN_NGAYCAP,
                COLUMN_MAK
        };

        // How you want the results sorted in the resulting Cursor
//        String sortOrder = PhongBanDatabase.COLUMN_ID + " DESC";
        String sortOrder = null;
        String selection = "MAPK = ?";
        String[] selectionArgs = new String[]{phongKho.getMapk()};

        Cursor cursor = db.query(
                TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        List<PhieuNhap> list_capphat = new ArrayList<>();

        while(cursor.moveToNext()){
            list_capphat.add(new PhieuNhap(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2)
            ));
        }

        return list_capphat;
    }

    public long insert(PhieuNhap phieuNhap){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_SOPHIEU, phieuNhap.getSoPhieu());
        values.put(COLUMN_NGAYCAP, phieuNhap.getNgayLap());
        values.put(COLUMN_MAK, phieuNhap.getMaK());

        // Insert the new row, returning the primary key value of the new row
        return db.insert(TABLE_NAME, null, values);
    }

    public long update(PhieuNhap phieuNhap){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SOPHIEU, phieuNhap.getSoPhieu());
        values.put(COLUMN_NGAYCAP, phieuNhap.getNgayLap());
        values.put(COLUMN_MAK, phieuNhap.getMaK());

        // db.update ( Tên bảng, tập giá trị mới, điều kiện lọc, tập giá trị cho điều kiện lọc );
        return db.update(
                PhieuNhapDatabase.TABLE_NAME
                , values
                , PhieuNhapDatabase.COLUMN_SOPHIEU +"=?"
                ,  new String[] { String.valueOf(phieuNhap.getSoPhieu()) }
        );
    }
    public long delete(PhieuNhap phieuNhap){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete ( Tên bàng, string các điều kiện lọc - dùng ? để xác định, string[] từng phần tử trong string[] sẽ nạp vào ? );
        //db.beginTransaction();
        try{
            return db.delete(
                    PhieuNhapDatabase.TABLE_NAME
                    , PhieuNhapDatabase.COLUMN_SOPHIEU +"=?"
                    ,  new String[] { String.valueOf(phieuNhap.getSoPhieu()) }
            );
        }catch (Exception e){
            Log.d(TAG, "Error while trying to delete PHIEU NHAP");
            return -1;
        }finally {
            //db.endTransaction();
        }
    }
    public long deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PhieuNhapDatabase.TABLE_NAME,null,null);
    }

//    @Override
//    public void onConfigure(SQLiteDatabase db) {
//        db.setForeignKeyConstraintsEnabled(true);
//        super.onConfigure(db);
//    }

    public String formatDate(String str, boolean toSQL ){
        String[] date ;
        String result = "";
        if( toSQL ){
            date = str.split("/");
            result = date[2] +"-"+ date[1] +"-"+ date[0];
        }else{
            date = str.split("-");
            result = date[2] +"/"+ date[1] +"/"+ date[0];
        }

        return result;
    }
}
