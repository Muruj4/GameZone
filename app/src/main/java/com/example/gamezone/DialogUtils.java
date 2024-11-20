package com.example.gamezone;

import android.app.AlertDialog;
import android.content.Context;

public class DialogUtils {
    public static void showSuccessDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Your enrollment for this tournament was successful!")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    public static void showAlreadyRegisteredDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Already Enrolled")
                .setMessage("You are already enrolled in this tournament.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    public static void showErrorDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }
}
