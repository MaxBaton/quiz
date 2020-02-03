package com.nadershamma.apps.quiz;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nadershamma.apps.database.DataBaseHelper;
import com.nadershamma.apps.eventhandlers.PreferenceChangeListener;
import com.nadershamma.apps.lifecyclehelpers.QuizViewModel;

import java.util.Objects;

public class GameActivity extends AppCompatActivity {
   public static final String CHOICES = "pref_numberOfChoices";
   public static final String TIMER = "pref_timeToAnswer";
   public static final String CATEGORIES = "pref_categoriesToInclude";
   private boolean preferencesChanged = true;
   private GameActivityFragment gameActivityFragment;
   private QuizViewModel quizViewModel;
   private SharedPreferences.OnSharedPreferenceChangeListener preferencesChangeListener;
   private static DataBaseHelper dataBaseHelper;

    private void setSharedPreferences() {
        // set default settings
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // listener registration to change settings
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(preferencesChangeListener);
    }

    private void ifPreferenceChanged(){
        if (preferencesChanged) {
            gameActivityFragment = (GameActivityFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.quizFragment);
            quizViewModel.setGuessRows(PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(CHOICES, null));
            quizViewModel.setCategoriesSet(PreferenceManager.getDefaultSharedPreferences(this)
                    .getStringSet(CATEGORIES, null));
            quizViewModel.setTimeToAnswer(PreferenceManager.getDefaultSharedPreferences(this)
                    .getString(TIMER, null));
            gameActivityFragment.resetQuiz();
            preferencesChanged = false;
        }else {
            gameActivityFragment.resumeTimer();
        }
    }

    @Override
 protected void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     quizViewModel = ViewModelProviders.of(this).get(QuizViewModel.class);
     preferencesChangeListener = new PreferenceChangeListener(this);
     setContentView(R.layout.fragment_game);
     Toolbar toolbar = findViewById(R.id.toolbar);
     setSupportActionBar(toolbar);
     Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
     setSharedPreferences();
     dataBaseHelper = new DataBaseHelper(this);
    }

    @Override
  protected void onStart() {
      super.onStart();
      ifPreferenceChanged();
  }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
       switch (id) {
           case R.id.reference:
               gameActivityFragment.stopTimer();
               ReferenceFragment referenceFragment = new ReferenceFragment();
               referenceFragment.show(getSupportFragmentManager(),"reference");break;
           case R.id.settings:
               gameActivityFragment.stopTimer();
               Intent settingsIntent = new Intent(this, SettingsActivity.class);
               startActivity(settingsIntent);break;
       }
      return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(gameActivityFragment.getCountDownTimer()!=null){
            gameActivityFragment.stopTimer();
        }
    }

    public QuizViewModel getQuizViewModel() { return quizViewModel; }

    public static String getCHOICES() { return CHOICES; }

    public static String getTimer(){return TIMER;}

    public static String getCategories() { return CATEGORIES; }

    public void setPreferencesChanged(boolean preferencesChanged) { this.preferencesChanged = preferencesChanged; }

    public static DataBaseHelper getDataBaseHelper(){ return dataBaseHelper; }
}
