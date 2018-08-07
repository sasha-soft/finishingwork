package com.burnsale.finishingwork;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.io.IOException;

public class AddPriceActivity extends AppCompatActivity implements View.OnClickListener{

    Button buttonAdd;
    Spinner sType, sUnit;
    EditText eName, eCost;
    String[] type = {"Стены", "Пол", "Потолок"};
    String[] unit = {"Единица измерения (м2)", "Единица измерения (м)"};

    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_price);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Spinner spinner = (Spinner) findViewById(R.id.sType);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, type);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Spinner spinnerUnit = (Spinner) findViewById(R.id.sUnit);
        ArrayAdapter<String> adapterUnit = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, unit);
        adapterUnit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapterUnit);

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

        sType       = (Spinner) findViewById(R.id.sType);
        eName       = (EditText) findViewById(R.id.eName);
        eCost       = (EditText) findViewById(R.id.eCost);
        sUnit       = (Spinner) findViewById(R.id.sUnit);
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
        String _type = sType.getSelectedItem().toString();
        String _name = eName.getText().toString();
        String _cost = eCost.getText().toString();
        String _unit = sUnit.getSelectedItem().toString();

        ContentValues contentValues = new ContentValues();

        switch (v.getId()) {
            case R.id.buttonAdd:
                if(_name.length() == 0) {
                    eName.setError("Введите наименование работы");
                }
                else if(_cost.length() == 0)
                {
                    eCost.setError("Введите цену");
                }
                else
                {
                    contentValues.put(mDBHelper.KEY_TYPEWORK, _type);
                    contentValues.put(mDBHelper.KEY_NAMEWORK, _name);
                    contentValues.put(mDBHelper.KEY_COSTWORK, _cost);
                    contentValues.put(mDBHelper.KEY_UNITWORK, _unit);

                    mDb.insert(mDBHelper.TABLE_PRICE, null, contentValues);
                    finish();
                }
            break;
        }

    }
}
