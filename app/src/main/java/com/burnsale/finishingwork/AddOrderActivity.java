package com.burnsale.finishingwork;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddOrderActivity extends AppCompatActivity implements View.OnClickListener {
    Button buttonAdd;
    EditText eNameOrder;

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_order);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDBHelper = new DatabaseHelper(this);
        try {
            mDBHelper.updateDataBase();
        } catch (IOException mIOException) {
            throw new Error("Не удалось обновить базу данных");
        }

        try {
            mDb = mDBHelper.getWritableDatabase();
        } catch (SQLException mSQLException) {
            throw mSQLException;
        }

        eNameOrder  = (EditText) findViewById(R.id.eNameOrder);
        buttonAdd   = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(this);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        String id, date, name = eNameOrder.getText().toString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        date = dateFormat.format(new Date()).toString();

        ContentValues contentValues = new ContentValues();

        switch (v.getId()) {
            case R.id.buttonAdd:
                if(name.length() == 0) {
                    eNameOrder.setError("Введите название заказа");
                }
                else
                {
                    contentValues.put(mDBHelper.KEY_NAMEORDER, name);
                    contentValues.put(mDBHelper.KEY_DATEORDER, date);

                    mDb.insert(mDBHelper.TABLE_ORDER, null, contentValues);

                    id = ReturnID();

                    Intent intent = new Intent(this, AreaActivity.class);
                    intent.putExtra("idOrder", id);
                    startActivity(intent);
                }
            break;
        }
    }

    public String ReturnID()
    {
        String id="";

        Cursor cursor = mDb.rawQuery("SELECT * FROM tb_order ORDER BY _ID DESC LIMIT 1", null);
        cursor.moveToFirst();
        id = cursor.getString(0);
        cursor.close();

        return id;
    }
}
