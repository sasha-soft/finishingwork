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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class AreaActivity extends AppCompatActivity implements View.OnClickListener {
    DecimalFormat m = new DecimalFormat("#.##");
    Button buttonAddArea, buttonJobchoice;
    EditText eLength, eWidth, eHeight;
    ListView listArea;
    String IDOrder;
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    private static final String ID       = "ID";
    private static final String IDORDERA = "id заказа";
    private static final String LENGTH   = "Длина";
    private static final String WIDTH    = "Ширина";
    private static final String HEIGHT   = "Высота";
    private static final String WALL     = "Стены";
    private static final String FLOOR    = "Пол";
    private static final String DISTANCE = "Дистанция";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);

        Bundle arguments = getIntent().getExtras();
        IDOrder = arguments.get("idOrder").toString();

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

        eLength         = (EditText) findViewById(R.id.eLength);
        eWidth          = (EditText) findViewById(R.id.eWidth);
        eHeight         = (EditText) findViewById(R.id.eHeight);
        buttonAddArea   = (Button) findViewById(R.id.buttonAddArea);
        buttonJobchoice = (Button) findViewById(R.id.buttonJobchoice);
        listArea        = (ListView) findViewById(R.id.listArea);

        buttonAddArea.setOnClickListener(this);
        buttonJobchoice.setOnClickListener(this);
    }

    public void onStart(){
        super.onStart();
        RefreshTable();
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
        switch (v.getId()) {
            case R.id.buttonAddArea:
                ContentValues contentValues = new ContentValues();
                String clength = eLength.getText().toString();
                String cwidth  = eWidth.getText().toString();
                String cheight  = eHeight.getText().toString();

                if(clength.length() == 0) {
                    eLength.setError("Введите длину комнаты");
                }
                else if(cwidth.length() == 0) {
                    eWidth.setError("Введите ширину комнаты");
                }
                else if(cheight.length() == 0) {
                    eHeight.setError("Введите высоту комнаты");
                }
                else
                {
                    double floor, wall, distance, lengthwall, widthwall, lengthdistance, widthdistance;
                    floor       = Float.valueOf(clength) * Float.valueOf(cwidth);  // Площадь пола и потолка
                    lengthwall  = (Float.valueOf(clength) * Float.valueOf(cheight))*2;
                    widthwall   = (Float.valueOf(cwidth) * Float.valueOf(cheight))*2;
                    wall        = lengthwall + widthwall;

                    lengthdistance = Float.valueOf(clength) * 2;
                    widthdistance  = Float.valueOf(cwidth) * 2;
                    distance       = lengthdistance + widthdistance;

                    floor = Math.round(floor*100)/100.0d;
                    wall = Math.round(wall*100)/100.0d;
                    distance = Math.round(distance*100)/100.0d;

                    contentValues.put(mDBHelper.KEY_IDORDERAREA, IDOrder);
                    contentValues.put(mDBHelper.KEY_LENGHTAREA, clength);
                    contentValues.put(mDBHelper.KEY_WIDTHAREA, cwidth);
                    contentValues.put(mDBHelper.KEY_HEIGHTAREA, cheight);
                    contentValues.put(mDBHelper.KEY_FLOORAREA, floor);
                    contentValues.put(mDBHelper.KEY_WALLTAREA, wall);
                    contentValues.put(mDBHelper.KEY_DISTANCEAREA, distance);
                    mDb.insert(mDBHelper.TABLE_AREA, null, contentValues);

                    RefreshTable();

                }
            break;


            case R.id.buttonJobchoice:
                if(CountRoom()>0)
                {
                    Intent intent = new Intent(this, JobchoiceActivity.class);
                    intent.putExtra("IDOrder", IDOrder);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Вы не добавили площадь объекта",
                            Toast.LENGTH_LONG).show();
                }
            break;
        }
    }

    void RefreshTable(){

        ArrayList<HashMap<String, Object>> laArea = new ArrayList<>();
        HashMap<String, Object> hashMap;
        Cursor cursor = mDb.rawQuery("SELECT * FROM tb_area WHERE idorder = ? ORDER BY _ID DESC", new String[]{IDOrder});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            hashMap = new HashMap<>();
            hashMap.put(ID, cursor.getString(0));
            hashMap.put(IDORDERA, cursor.getString(1));
            hashMap.put(LENGTH, "Длина: " + cursor.getString(2) + "м");
            hashMap.put(WIDTH, "Ширина: " + cursor.getString(3) + "м");
            hashMap.put(HEIGHT, "Высота: " + cursor.getString(4) + "м");
            hashMap.put(WALL, "Стены: " + cursor.getString(5) + "м2");
            hashMap.put(FLOOR, "Пол: " + cursor.getString(6) + "м2");
            hashMap.put(DISTANCE, "Периметр: " + cursor.getString(7) + "м");

            laArea.add(hashMap);
            cursor.moveToNext();
        }
        cursor.close();
        SimpleAdapter adapter = new SimpleAdapter(this, laArea,
                R.layout.larea, new String[]{LENGTH, WIDTH, HEIGHT, FLOOR, WALL, DISTANCE},
                new int[]{R.id.text_arealength, R.id.text_areawidth, R.id.text_areaheight, R.id.text_areafloor, R.id.text_areawall, R.id.text_areadistance});

        listArea.setAdapter(adapter);
    }

    int CountRoom()
    {
        Cursor cursorCR = mDb.rawQuery("SELECT * FROM tb_area WHERE idorder = ?", new String[]{IDOrder});
        return cursorCR.getCount();
    }
}
