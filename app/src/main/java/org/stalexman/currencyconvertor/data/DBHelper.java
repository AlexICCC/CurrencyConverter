package org.stalexman.currencyconvertor.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.stalexman.currencyconvertor.data.model.Currency;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "db";

    public static final String TABLE_CURRENCIES = "currencies";

    public static final String COL_CHAR_CODE = "char_code";
    public static final String COL_NOMINAL = "nominal";
    public static final String COL_NAME = "name";
    public static final String COL_VALUE = "value";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_CURRENCIES + " (" +
                "id integer primary key autoincrement," +
                COL_CHAR_CODE + " text," +
                COL_NOMINAL + " integer," +
                COL_NAME + " text," +
                COL_VALUE + " real" + ");");


        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CHAR_CODE, "RUB");
        contentValues.put(COL_NOMINAL, 1);
        contentValues.put(COL_NAME, "Российский рубль");
        contentValues.put(COL_VALUE, 1.0);
        db.insert(TABLE_CURRENCIES, null, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertOrUpdateCurrency (Currency currency) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CHAR_CODE, currency.getCharCode());
        contentValues.put(COL_NOMINAL, currency.getNominal());
        contentValues.put(COL_NAME, currency.getName());
        contentValues.put(COL_VALUE, currency.getValue());

        int id = db.update(TABLE_CURRENCIES, contentValues, "char_code=?", new String[]{currency.getCharCode()});
        if (id == 0) {
            db.insert(TABLE_CURRENCIES, null, contentValues);
        }
    }

    public List<Currency> getCurrencyAll(){
        ArrayList<Currency> currencies = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_CURRENCIES, null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            currencies.add(new Currency(
                    res.getString(res.getColumnIndex(COL_CHAR_CODE)),
                    res.getInt(res.getColumnIndex(COL_NOMINAL)),
                    res.getString(res.getColumnIndex(COL_NAME)),
                    res.getDouble(res.getColumnIndex(COL_VALUE))
            ));
            res.moveToNext();
        }
        res.close();
        return currencies;
    }

    public boolean insertCurrency (Currency currency) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CHAR_CODE, currency.getCharCode());
        contentValues.put(COL_NOMINAL, currency.getNominal());
        contentValues.put(COL_NAME, currency.getName());
        contentValues.put(COL_VALUE, currency.getValue());
        db.insert(TABLE_CURRENCIES, null, contentValues);
        return true;
    }

    public boolean updateCurrency (Currency currency) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_CHAR_CODE, currency.getCharCode());
        contentValues.put(COL_NOMINAL, currency.getNominal());
        contentValues.put(COL_NAME, currency.getName());
        contentValues.put(COL_VALUE, currency.getValue());
        db.update(
                TABLE_CURRENCIES,
                contentValues,
                "char_code = ? ",
                new String[] { currency.getCharCode() } );
        return true;
    }

    public TreeMap<String, String> getCurrNameToCharCode(){
        TreeMap<String, String> currencies = new TreeMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + TABLE_CURRENCIES, null );
        res.moveToFirst();
        while(!res.isAfterLast()){
            currencies.put(
                    res.getString(res.getColumnIndex(COL_NAME)),
                    res.getString(res.getColumnIndex(COL_CHAR_CODE))
            );
            res.moveToNext();
        }
        res.close();
        return currencies;
    }
}
