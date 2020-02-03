package com.nadershamma.apps.eventhandlers;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.widget.Toast;

import com.nadershamma.apps.quiz.GameActivity;
import com.nadershamma.apps.quiz.R;
import com.nadershamma.apps.quiz.SettingsActivity;
import com.nadershamma.apps.quiz.WarningCategories;

import java.util.Set;

public class PreferenceChangeListener implements OnSharedPreferenceChangeListener {
    private GameActivity gameActivity;

    public PreferenceChangeListener(GameActivity gameActivity) { this.gameActivity = gameActivity; }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            gameActivity.setPreferencesChanged(true);//user changed settings
        if (key.equals(gameActivity.getCHOICES())) {//changed the number of answer options
            gameActivity.getQuizViewModel().setGuessRows(sharedPreferences.getString(
                    GameActivity.CHOICES, null));
        } else if(key.equals(gameActivity.getTimer())){//changed time to answer
                gameActivity.getQuizViewModel().setTimeToAnswer(sharedPreferences.getString(
                        GameActivity.TIMER, null));
    }else if (key.equals(gameActivity.getCategories())){//changed categories
            Set<String> categories = sharedPreferences.getStringSet(GameActivity.CATEGORIES,
                    null);
            if (categories != null && categories.size() > 0 ) {
                gameActivity.getQuizViewModel().setCategoriesSet(categories);
            } else {
                categories.add(gameActivity.getString(R.string.default_category));
                gameActivity.getQuizViewModel().setCategoriesSet(categories);
                WarningCategories warningCategories = new WarningCategories();
                warningCategories.show(SettingsActivity.getFM(),"warning");
          }
      }
        Toast.makeText(gameActivity, R.string.restarting_quiz,
                Toast.LENGTH_SHORT).show();
    }
}
