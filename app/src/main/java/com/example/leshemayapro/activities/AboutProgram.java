package com.example.leshemayapro.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.leshemayapro.R;

public class AboutProgram extends AppCompatActivity  {
    Dialog d;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutprogrammer);

    }
    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return  true;
    }
    @Override

    public boolean onOptionsItemSelected(MenuItem item)
    {

        int id = item.getItemId();
        if (id == R.id.action_aboutProgrammer)
        {
            createAboutProgrammerDialog();

        }
        else if (id == R.id.action_aboutProgram)
        {
            createAboutProgramDialog();
        }
        else if (id == R.id.action_exit)
        {
            new AlertDialog.Builder(this)
                    .setTitle("Going back")
                    .setMessage("are you sure you want to return?")
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finishActivity();
                        }
                    })
                    .setNegativeButton("no",null)
                    .show();
            return true;
        }
        return false;
    }
    private void finishActivity() {
        finishAndRemoveTask();
        finishAffinity();
    }

    public void createAboutProgrammerDialog()//יצירת דיאלוג התחברות
    {
        d= new Dialog(this);
        d.setContentView(R.layout.activity_aboutprogrammer);
        d.setTitle("Login");
        d.setCancelable(true);
        d.show();

    }
    public void createAboutProgramDialog()//יצירת דיאלוג התחברות
    {
        d= new Dialog(this);
        d.setContentView(R.layout.activity_aboutprogram);
        d.setTitle("Login");
        d.setCancelable(true);
        d.show();

    }
}
