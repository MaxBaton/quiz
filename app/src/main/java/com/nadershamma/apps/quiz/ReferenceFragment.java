package com.nadershamma.apps.quiz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;


public class ReferenceFragment extends AppCompatDialogFragment {

    private GameActivityFragment gameActivityFragment;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        gameActivityFragment = (GameActivityFragment) getFragmentManager().findFragmentById(R.id.quizFragment);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Справка")
                .setView(R.layout.reference)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dismiss();
                   }
               });
        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (gameActivityFragment != null){
            gameActivityFragment.resumeTimer();
        }
    }
}
