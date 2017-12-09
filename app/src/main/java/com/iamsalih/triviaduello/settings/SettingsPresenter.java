package com.iamsalih.triviaduello.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.iamsalih.triviaduello.R;
import com.iamsalih.triviaduello.settings.profile.ProfileActivity;

import java.util.Arrays;

/**
 * Created by muhammedsalihguler on 09.12.17.
 */

public class SettingsPresenter {

    private SettingsView settingsView;

    public SettingsPresenter(SettingsView settingsView) {
        this.settingsView = settingsView;
    }


    public void showLogoutDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(settingsView.getAppContext())
                .setTitle(settingsView.getAppContext().getString(R.string.logout_dialog_title))
                .setMessage(settingsView.getAppContext().getString(R.string.dialog_message))
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        FirebaseAuth.getInstance().signOut();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }
    public void showCategoryDialog() {
        final Gson gson = new Gson();
        final SharedPreferences preferences = settingsView.getAppContext().getSharedPreferences("appPreferences", Context.MODE_PRIVATE);
        final String[] categoryArray = settingsView.getAppContext().getResources().getStringArray(R.array.question_categories);
        String someText = preferences.getString("selectedCategories", null);
        final boolean[] categorySelected;
        if (TextUtils.isEmpty(someText)) {
            categorySelected = new boolean[categoryArray.length];
            Arrays.fill(categorySelected, true);
        } else {
            categorySelected = gson.fromJson(someText, boolean[].class);
        }
        AlertDialog alertDialog = new AlertDialog.Builder(settingsView.getAppContext())
                .setTitle(settingsView.getAppContext().getString(R.string.category_dialog_title))
                .setMultiChoiceItems(categoryArray, categorySelected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isItemChecked) {
                        for (int i = 0; i < categoryArray.length; i++) {
                            if (position == 0) {
                                categorySelected[i]=isItemChecked;
                                ((AlertDialog) dialogInterface).getListView().setItemChecked(i, isItemChecked);
                            }
                            else {
                                if (i == position) {
                                    categorySelected[i] = isItemChecked;
                                    categorySelected[0] = false;
                                    ((AlertDialog) dialogInterface).getListView().setItemChecked(i, isItemChecked);
                                    ((AlertDialog) dialogInterface).getListView().setItemChecked(0, false);
                                    break;
                                }
                            }
                        }
                    }
                })
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        String json = gson.toJson(categorySelected);
                        preferences.edit().putString("selectedCategories", json).apply();
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        alertDialog.show();
    }

    public void showProfileFragment() {
        Intent intent = new Intent(settingsView.getAppContext(), ProfileActivity.class);
        settingsView.getAppContext().startActivity(intent);
    }
}
