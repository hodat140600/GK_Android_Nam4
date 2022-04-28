package com.example.myapplication.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import com.example.myapplication.Entities.VatTu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class VatTuDatabase extends SQLiteOpenHelper {
    private static final String TAG = "SQLite";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "QLPK.db";

    // Database Path
    private static String DATABASE_PATH;
    public Context mContext;
    // Table name: Note.
    public static final String TABLE_NAME = "VATTU";

    public static final String text = "text";
    public static final String COLUMN_MAVT ="MAVT";
    public static final String COLUMN_TENVT = "TENVT";
    public static final String COLUMN_DVT = "DVT";
    public static final String COLUMN_GIANHAP = "GIANHAP";
    public static final String COLUMN_HINH = "HINH";

    public VatTuDatabase(Context context)  {
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
    //    @Override
//    public synchronized void Close(){
//        if (mDataBase != null){
//            mDataBase.close();
//        }
//        super.close();
//    }
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
                + COLUMN_MAVT + " TEXT PRIMARY KEY,"
                + COLUMN_TENVT + " TEXT NOT NULL,"
                + COLUMN_DVT + " TEXT NOT NULL,"
                + COLUMN_GIANHAP + " TEXT NOT NULL,"
                + COLUMN_HINH + " BLOB);";
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
    public void dropTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public List<VatTu> reset(){
        dropTable();
        insert( new VatTu("VT1","Gạch ống","Viên","1000",null));
        insert( new VatTu("VT2","Gạch thẻ"   ,"Viên" ,"1200",null));
        insert( new VatTu("VT3","Sắt tròn"    ,"Tấn" ,"50000" ,null));
        insert( new VatTu("VT4","Sơn dầu","Thùng" ,"20000" ,null));
        insert( new VatTu("VT5","Xi măng","Bao" ,"18000",null));
        insert( new VatTu("VT6","Thép","Bao","17000" ,null));
        return select();
    }

    public List<VatTu> select(){
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                COLUMN_MAVT,
                COLUMN_TENVT,
                COLUMN_DVT,
                COLUMN_GIANHAP,
                COLUMN_HINH
        };

        // How you want the results sorted in the resulting Cursor
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

        List<VatTu> list_vattu = new ArrayList<>();

        while(cursor.moveToNext()){
            list_vattu.add(new VatTu(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getBlob(4)
            ));
        }

        return list_vattu;
    }

    public long insert(VatTu vatTu){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_MAVT, vatTu.getMaVt());
        values.put(COLUMN_TENVT, vatTu.getTenVt());
        values.put(COLUMN_DVT, vatTu.getDvt());
        values.put(COLUMN_GIANHAP, vatTu.getGiaNhap());
        values.put(COLUMN_HINH, vatTu.getHinh());

        // Insert the new row, returning the primary key value of the new row
        return db.insert(TABLE_NAME, null, values);
    }

    public long update(VatTu vatTu){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MAVT, vatTu.getMaVt());
        values.put(COLUMN_TENVT, vatTu.getTenVt());
        values.put(COLUMN_DVT, vatTu.getDvt());
        values.put(COLUMN_GIANHAP, vatTu.getGiaNhap());
        values.put(COLUMN_HINH, vatTu.getHinh());

        // db.update ( Tên bảng, tập giá trị mới, điều kiện lọc, tập giá trị cho điều kiện lọc );
        return db.update(
                VatTuDatabase.TABLE_NAME
                , values
                , VatTuDatabase.COLUMN_MAVT +"=?"
                ,  new String[] { String.valueOf(vatTu.getMaVt()) }
        );
    }
    public long delete(VatTu vatTu){
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();
        // db.delete ( Tên bàng, string các điều kiện lọc - dùng ? để xác định, string[] từng phần tử trong string[] sẽ nạp vào ? );
        //db.beginTransaction();
        try{
            return db.delete(
                    VatTuDatabase.TABLE_NAME
                    ,VatTuDatabase.COLUMN_MAVT +"=?"
                    ,  new String[] { String.valueOf(vatTu.getMaVt()) }
            );
        }catch (Exception e){
            Log.d(TAG, "Error while trying to delete VAT TU");
            return -1;
        }finally {
            //db.endTransaction();
        }
    }
    public long deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(VatTuDatabase.TABLE_NAME,null,null);
    }

}

