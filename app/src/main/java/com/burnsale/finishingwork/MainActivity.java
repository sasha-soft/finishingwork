package com.burnsale.finishingwork;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String idorder;
    Button AddOrder;
    ListView listOrder;
    AlertDialog.Builder ad;
    Context context;

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;

    private static final String IDORDER   = "ID";
    private static final String NAMEORDER = "Комментарий";
    private static final String DATEORDER = "Дата";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenPrice();
            }
        });

        listOrder = (ListView) findViewById(R.id.listOrder);

        AddOrder = (Button) findViewById(R.id.buttonAddOrder);
        AddOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenOrder();
            }
        });

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



        listOrder.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                HashMap<String, Object> itemHashMap =
                        (HashMap<String, Object>) parent.getItemAtPosition(position);
                idorder   = itemHashMap.get(IDORDER).toString();
                ad.show();
            }
        });

        context = MainActivity.this;
        String title = "Какие действия к заказу?";
        String buttonSee = "Просмотр";
        String buttonDel = "Удалить";

        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);

        ad.setPositiveButton(buttonSee, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, InfoOrderActivity.class);
                intent.putExtra("IDOrder", idorder);
                startActivity(intent);
            }
        });
        ad.setNegativeButton(buttonDel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (idorder.equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),
                            "Запись не найдена", Toast.LENGTH_SHORT)
                            .show();
                }
                else
                {
                    mDb.delete(mDBHelper.TABLE_ORDER, "_ID = ?", new String[] {idorder});
                    mDb.delete(mDBHelper.TABLE_AREA, "idorder = ?", new String[] {idorder});
                    mDb.delete(mDBHelper.TABLE_DETAIL, "idorder = ?", new String[] {idorder});
                    Toast.makeText(getApplicationContext(),
                            "Запись удалена", Toast.LENGTH_SHORT)
                            .show();

                    RefreshTable();
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

    public void onStart(){
        super.onStart();
        RefreshTable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_order:
                OpenOrder();
                return true;
            case R.id.action_price:
                OpenPrice();
                return true;
            case R.id.action_help:
                OpenHelp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void OpenPrice()
    {
        Intent intentprice = new Intent(this, PriceActivity.class);
        startActivityForResult(intentprice, 1);
    }

    private void OpenOrder()
    {
        if(CountPrice()>0)
        {
            Intent intentorder = new Intent(this, AddOrderActivity.class);
            startActivityForResult(intentorder, 1);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Заполните прайс-лист, выполняемых работ",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void OpenHelp()
    {
        Intent intenthelp = new Intent(this, HelpActivity.class);
        startActivityForResult(intenthelp, 1);
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    void RefreshTable(){
        ArrayList<HashMap<String, Object>> laOrder = new ArrayList<>();
        HashMap<String, Object> hashMap;
        Cursor cursor = mDb.rawQuery("SELECT * FROM tb_order ORDER BY _ID DESC", null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            hashMap = new HashMap<>();
            hashMap.put(IDORDER, cursor.getString(0));
            hashMap.put(DATEORDER, cursor.getString(1));
            hashMap.put(NAMEORDER, cursor.getString(2));
            laOrder.add(hashMap);

            cursor.moveToNext();
        }
        cursor.close();
        SimpleAdapter adapter = new SimpleAdapter(this, laOrder,
                R.layout.lorder, new String[]{IDORDER, DATEORDER, NAMEORDER},
                new int[]{R.id.text_idorder, R.id.text_dateorder, R.id.text_nameorder});


        listOrder.setAdapter(adapter);
    }

    int CountPrice()
    {
        Cursor cursorCP = mDb.rawQuery("SELECT * FROM tb_price", null);
        return cursorCP.getCount();
    }
}
