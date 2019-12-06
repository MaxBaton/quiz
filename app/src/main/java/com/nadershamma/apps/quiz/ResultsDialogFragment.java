package com.nadershamma.apps.quiz;


import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;

import com.nadershamma.apps.database.DataBaseHelper;
import com.nadershamma.apps.database.Fields;
import com.nadershamma.apps.lifecyclehelpers.QuizViewModel;

public class ResultsDialogFragment extends DialogFragment{

    private int totalGuesses;
    private static int categories;
    private static int buttons;
    private static int guesses;
    private static int time;
    private static double points;
    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase sqLiteDatabase;
    private ContentValues contentValues;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final QuizViewModel quizViewModel = ViewModelProviders.of(getActivity()).get(QuizViewModel.class);
        dataBaseHelper = GameActivity.getDataBaseHelper();
        sqLiteDatabase = dataBaseHelper.getWritableDatabase();
        contentValues   = new ContentValues();
        categories = quizViewModel.getNumberOfCategories();
        buttons = quizViewModel.getGuessRows() * 2;
        guesses = quizViewModel.getTotalGuesses();
        time = quizViewModel.getTimeToAnswer();
        totalGuesses = quizViewModel.getTotalGuesses();
        points = (double)1000/totalGuesses;
        fillInWithData();
        dataBaseHelper.close();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(
                getString(R.string.results,totalGuesses,points));

        builder.setPositiveButton(R.string.reset_quiz, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    GameActivityFragment gameActivityFragment1 = (GameActivityFragment) getParentFragment();
                    try{
                        gameActivityFragment1.finalQuiz();
                        gameActivityFragment1.getActivity().finish();
                    }catch (Exception e){
                        Log.e(quizViewModel.getTag(),"Unable to call resetQuiz()", e);
                    }
                }
                catch (Exception e){
                    Log.e(quizViewModel.getTag(),"Unable to get ActivityMainFragment", e);
                }
            }
        });
        return builder.create();
    }
    public void fillInWithData() {
        contentValues.put(Fields.ResultTable.Cols.POINTS, points);
        contentValues.put(Fields.ResultTable.Cols.GUESSES,guesses);
        contentValues.put(Fields.ResultTable.Cols.CATEGORIES,categories);
        contentValues.put(Fields.ResultTable.Cols.BUTTONS,buttons);
        contentValues.put(Fields.ResultTable.Cols.TIME,time);
        sqLiteDatabase.insert(Fields.ResultTable.NAME,null,contentValues);
    }

    public static double getPoints() {return points;}
}
