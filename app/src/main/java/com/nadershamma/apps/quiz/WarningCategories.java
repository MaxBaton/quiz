package com.nadershamma.apps.quiz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;

public class WarningCategories extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Предупреждение").setMessage(R.string.default_category_message)
                .setPositiveButton("ok",null);
        return builder.create();
    }
}
