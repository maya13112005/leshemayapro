package com.example.leshemayapro.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.leshemayapro.R;

public class AboutProgrammer extends AppCompatActivity {
    Dialog d;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutprogrammer);

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
