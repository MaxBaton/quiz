package com.nadershamma.apps.quiz;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.nadershamma.apps.database.DataBaseHelper;
import com.nadershamma.apps.database.Fields;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnPlay;
    private Button btnSettings;
    private Button btnResultsTable;
    private int number;
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private int num = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        btnPlay = findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);
        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);
        btnResultsTable = findViewById(R.id.btnResultsTable);
        btnResultsTable.setOnClickListener(this);
        dataBaseHelper = new DataBaseHelper(this);
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
    }

    private void readData() {
       Cursor res = dataBaseHelper.getAllData();
       number = 1;
       if(res.getCount() == 0){
           showMessage("Ошибка","Нет результатов");
           return;
       }

       StringBuffer stringBuffer = new StringBuffer();
       String time;
       String points;
       while (res.moveToNext()){
           stringBuffer.append("№"  + number + "\n");
           points = getResources().getString(R.string.points,1,Double.parseDouble(res.getString(4)));
           stringBuffer.append("Очки : " + points  + "\n");
           stringBuffer.append("Попытки : " + res.getString(3) + "\n");
           stringBuffer.append("Кнопки : " + res.getString(2) + "\n");
           stringBuffer.append("Категории : " + res.getString(1) + "\n");
           time = res.getString(5);
           if(time.equals("100")){
               time = "Без таймера";
           }
           stringBuffer.append("Время на ответ : " + time + "\n\n");
           number++;
       }
       showMessage("Топ 10 лучших результатов",stringBuffer.toString());
   }

    private void deleteData(){ sqLiteDatabase.delete(Fields.ResultTable.NAME,null,null); }

   public void showMessage(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("ok",null);
        builder.setNegativeButton("Стереть все данные", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               deleteData();
              dataBaseHelper.close();
           }
       });
       builder.show();
   }

    @Override
   public void onClick(View v) {
       switch (v.getId()){
       case R.id.btnPlay:
       Intent intentPlay = new Intent(this, GameActivity.class);
       intentPlay.putExtra("number",num);
       startActivity(intentPlay);
       break;
       case R.id.btnSettings:
       Intent intentSettings = new Intent(this,SettingsActivity.class);
       startActivity(intentSettings);
       break;
       case R.id.btnResultsTable:
       readData();
       break;
       }
   }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_main,menu);
            menu.removeItem(R.id.settings);
            return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ReferenceFragment referenceFragment = new ReferenceFragment();
        referenceFragment.show(getSupportFragmentManager(),"reference");
        return super.onOptionsItemSelected(item);
    }
}
