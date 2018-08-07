package com.burnsale.finishingwork;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JobchoiceActivity extends AppCompatActivity implements View.OnClickListener {
    String IDOrder;
    SparseBooleanArray sparseBooleanArray;

    Button buttonJC;
    ListView listJC;
    String[] ListViewItems = null;
    ArrayList jcList = new ArrayList();

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jobchoice);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle arguments = getIntent().getExtras();
        IDOrder = arguments.get("IDOrder").toString();

        listJC      = (ListView) findViewById(R.id.listJC);
        buttonJC    = (Button) findViewById(R.id.buttonJC);

        buttonJC.setOnClickListener(this);

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

        CreateJC();


        listJC.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sparseBooleanArray = listJC.getCheckedItemPositions();
                jcList.clear();
                int i = 0 ;

                while (i < sparseBooleanArray.size()) {
                    if (sparseBooleanArray.valueAt(i)) {
                        jcList.add(ListViewItems [ sparseBooleanArray.keyAt(i) ]);
                    }
                    i++ ;
                }
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
    void CreateJC()
    {
        List<String> ListJob = new ArrayList<String>();

        Cursor cursor = mDb.rawQuery("SELECT * FROM tb_price ORDER BY typework DESC", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ListJob.add(cursor.getString(2));
            cursor.moveToNext();
        }
        cursor.close();

        ListViewItems = ListJob.toArray(new String[ListJob.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,
                        android.R.layout.simple_list_item_multiple_choice,
                        android.R.id.text1, ListViewItems );


        listJC.setAdapter(adapter);
    }


    void TotalArea()
    {
        ContentValues contentValues = new ContentValues();
        double _floor=0, _wall = 0, _distance = 0, _area = 0, _sum = 0;
        int _price = 0;
        String _name = "", _type = "", _unit = "";

        Cursor cursor = mDb.rawQuery("SELECT * FROM tb_area WHERE idorder = ?", new String[]{IDOrder});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            _wall += cursor.getFloat(5);
            _floor += cursor.getFloat(6);
            _distance += cursor.getFloat(7);
            cursor.moveToNext();
        }
        cursor.close();

        Iterator ir=jcList.iterator();
        while(ir.hasNext()){
            Cursor cPrice = mDb.rawQuery("SELECT * FROM tb_price WHERE namework = ?", new String[]{ir.next().toString()});
            cPrice.moveToFirst();
            while (!cPrice.isAfterLast()) {
                _type = cPrice.getString(1);
                _name = cPrice.getString(2);
                _price= cPrice.getInt(3);
                _unit = cPrice.getString(4);
                if(_unit.equals("Единица измерения (м2)"))
                {
                    if(_type.equals("Стены"))
                    {
                        _area = _wall;
                        _sum = _wall * _price;
                    }
                    else
                    {
                        _area = _floor;
                        _sum = _floor * _price;
                    }
                }
                else
                {
                    _area = _distance;
                    _sum  = _distance * _price;
                }

                _area = Math.round(_area*100)/100.0d;
                _sum = Math.round(_sum*100)/100.0d;

                contentValues.put(mDBHelper.KEY_IDORDERDETAIL, IDOrder);
                contentValues.put(mDBHelper.KEY_IDAREADETAIL, "1");
                contentValues.put(mDBHelper.KEY_TYPEDETAIL, _type);
                contentValues.put(mDBHelper.KEY_NAMEDETAIL, _name);
                contentValues.put(mDBHelper.KEY_AREADETAIL, _area);
                contentValues.put(mDBHelper.KEY_COSTDETAIL, _price);
                contentValues.put(mDBHelper.KEY_SUMDETAIL, _sum);
                contentValues.put(mDBHelper.KEY_STATUSDETAIL, "true");
                mDb.insert(mDBHelper.TABLE_DETAIL, null, contentValues);

                cPrice.moveToNext();
            }
            cPrice.close();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonJC:
                if(jcList.size()>0){
                    if(CountDetail()>0){
                        DelDetail();
                    }

                    TotalArea();

                    Intent intent = new Intent(this, InfoOrderActivity.class);
                    intent.putExtra("IDOrder", IDOrder);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(JobchoiceActivity.this, "Выберите работы к этому объекту", Toast.LENGTH_LONG).show();
                }
            break;
        }
    }

    int CountDetail()
    {
        Cursor cursorCP = mDb.rawQuery("SELECT * FROM tb_detail WHERE idorder = ?", new String[]{IDOrder});
        return cursorCP.getCount();
    }

    void DelDetail()
    {
        mDb.delete(mDBHelper.TABLE_DETAIL, "idorder = ?", new String[] {IDOrder});
    }
}
