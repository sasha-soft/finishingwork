package com.burnsale.finishingwork;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "db_fw.db";
    private static String DB_PATH = "";
    private static final int DB_VERSION = 3;

    private SQLiteDatabase mDataBase;
    private final Context mContext;
    private boolean mNeedUpdate = false;

    public static final String TABLE_PRICE          = "tb_price";
    public static final String TABLE_ORDER          = "tb_order";
    public static final String TABLE_DETAIL         = "tb_detail";
    public static final String TABLE_AREA           = "tb_area";

    public static final String KEY_IDWORK           = "_ID";
    public static final String KEY_TYPEWORK         = "typework";
    public static final String KEY_NAMEWORK         = "namework";
    public static final String KEY_COSTWORK         = "costwork";
    public static final String KEY_UNITWORK         = "unit";

    public static final String KEY_IDORDER          = "_ID";
    public static final String KEY_DATEORDER        = "dateorder";
    public static final String KEY_NAMEORDER        = "nameorder";

    public static final String KEY_IDAREA           = "_ID";
    public static final String KEY_IDORDERAREA      = "idorder";
    public static final String KEY_LENGHTAREA       = "length";
    public static final String KEY_WIDTHAREA        = "width";
    public static final String KEY_HEIGHTAREA       = "height";
    public static final String KEY_WALLTAREA        = "wall";
    public static final String KEY_FLOORAREA        = "floor";
    public static final String KEY_DISTANCEAREA     = "distance";

    public static final String KEY_IDDETAIL         = "_ID";
    public static final String KEY_IDORDERDETAIL    = "idorder";
    public static final String KEY_IDAREADETAIL     = "idarea";
    public static final String KEY_TYPEDETAIL       = "typew";
    public static final String KEY_NAMEDETAIL       = "namew";
    public static final String KEY_AREADETAIL       = "area";
    public static final String KEY_COSTDETAIL       = "cost";
    public static final String KEY_SUMDETAIL        = "sum";
    public static final String KEY_STATUSDETAIL     = "status";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        if (android.os.Build.VERSION.SDK_INT >= 17)
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        else
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        this.mContext = context;

        copyDataBase();

        this.getReadableDatabase();
    }

    public void updateDataBase() throws IOException {
        if (mNeedUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();

            copyDataBase();

            mNeedUpdate = false;
        }
    }

    private boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    private void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }

    private void copyDBFile() throws IOException {
        InputStream mInput = mContext.getAssets().open(DB_NAME);
        //InputStream mInput = mContext.getResources().openRawResource(R.raw.info);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }

    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            mNeedUpdate = true;
    }
}
