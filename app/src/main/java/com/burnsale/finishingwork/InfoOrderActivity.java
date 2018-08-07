package com.burnsale.finishingwork;

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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class InfoOrderActivity extends AppCompatActivity implements View.OnClickListener {
    TextView text_sum;
    ListView listInfo;
    Button buttonClose;

    String IDOrder, _sum;

    private static final String IJOB   = "Работа";
    private static final String IAREA  = "Объем";
    private static final String ICOST  = "Стоимость";
    private static final String ISUM   = "Общая стоимость работы";

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_order);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle arguments = getIntent().getExtras();
        IDOrder = arguments.get("IDOrder").toString();

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

        listInfo    = (ListView) findViewById(R.id.listInfo);
        text_sum    = (TextView) findViewById(R.id.text_sum);
        buttonClose = (Button) findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(this);

        SumOrder();

        text_sum.setText("Итого: " + _sum + "р. ");
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
    void SumOrder()
    {
        float sumjob = 0;
        _sum = "";

        ArrayList<HashMap<String, Object>> laInfo = new ArrayList<>();
        HashMap<String, Object> hashMap;
        Cursor cursor = mDb.rawQuery("SELECT * FROM tb_detail WHERE idorder = ?", new String[]{IDOrder});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            sumjob += Float.valueOf(cursor.getString(7));

            hashMap = new HashMap<>();
            hashMap.put(IJOB, cursor.getString(4));
            hashMap.put(IAREA,"Объем: " + cursor.getString(5)+" м2/м");
            hashMap.put(ICOST, "Стоимость: " + cursor.getString(6) + " р.");
            hashMap.put(ISUM, "Всего: " + cursor.getString(7) + " р.");
            laInfo.add(hashMap);
            cursor.moveToNext();
        }
        cursor.close();
        _sum = Float.toString(sumjob);

        SimpleAdapter adapter = new SimpleAdapter(this, laInfo,
                R.layout.linfo, new String[]{IJOB, IAREA, ICOST, ISUM},
                new int[]{R.id.text_ijob, R.id.text_iarea, R.id.text_icost, R.id.text_isum});


        listInfo.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonClose:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
