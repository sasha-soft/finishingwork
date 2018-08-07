package com.burnsale.finishingwork;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class PriceActivity extends AppCompatActivity implements View.OnClickListener{
    ListView listPrice;
    Button buttonAddPrice;
    AlertDialog.Builder ad;
    Context context;

    String idwork, namework;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    private static final String IDWORK   = "ID";
    private static final String TYPEWORK = "Типа работы";
    private static final String NAMEWORK = "Наименование работа";
    private static final String COSTWORK = "Стоимость";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        buttonAddPrice = (Button) findViewById(R.id.buttonAddPrice);
        buttonAddPrice.setOnClickListener(this);

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

        listPrice = (ListView) findViewById(R.id.listPrice);

        listPrice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                HashMap<String, Object> itemHashMap =
                        (HashMap<String, Object>) parent.getItemAtPosition(position);
                idwork   = itemHashMap.get(IDWORK).toString();
                namework = itemHashMap.get(NAMEWORK).toString();
                ad.show();
            }
        });


        context = PriceActivity.this;
        String title = "Удалить запись?";
        String buttonDel = "Удалить";

        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setNegativeButton(buttonDel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (idwork.equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),
                            "Запись не найдена", Toast.LENGTH_SHORT)
                            .show();
                }
                else
                {
                    mDb.delete(mDBHelper.TABLE_PRICE, mDBHelper.KEY_IDWORK + "=" + idwork, null);

                    RefreshTable();

                    Toast.makeText(getApplicationContext(),
                            "Запись удалена", Toast.LENGTH_SHORT)
                            .show();
                }

            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(context, "Вы ничего не выбрали",
                        Toast.LENGTH_LONG).show();
            }
        });

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

    public void onStart(){
        super.onStart();
        RefreshTable();
    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonAddPrice:
                Intent intentAdd = new Intent(this, AddPriceActivity.class);
                startActivityForResult(intentAdd, 1);
            break;
        }
    }

    void RefreshTable()
    {
        ArrayList<HashMap<String, Object>> laPrice = new ArrayList<>();
        HashMap<String, Object> hashMap;
        Cursor cursor = mDb.rawQuery("SELECT * FROM tb_price ORDER BY _ID DESC", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            hashMap = new HashMap<>();
            hashMap.put(IDWORK, cursor.getString(0));
            hashMap.put(TYPEWORK, cursor.getString(1));
            hashMap.put(NAMEWORK, cursor.getString(2));
            hashMap.put(COSTWORK, cursor.getString(3));
            laPrice.add(hashMap);

            cursor.moveToNext();
        }
        cursor.close();
        SimpleAdapter adapter = new SimpleAdapter(this, laPrice,
                R.layout.lprice, new String[]{IDWORK, TYPEWORK, NAMEWORK, COSTWORK},
                new int[]{R.id.text_id, R.id.text_type, R.id.text_name, R.id.text_cost});


        listPrice.setAdapter(adapter);
    }
}
