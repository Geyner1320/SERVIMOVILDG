package com.dgteam.servimovildg.registro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.dgteam.servimovildg.R;

public class SelectUserType extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user_type);
        getSupportActionBar().hide();
    }

    public void goToRegAsUser(View V) {
        Intent irAregistro = new Intent(
                SelectUserType.this,
                RegistroActivity.class);
        irAregistro.putExtra("userType", "User");
        startActivity(irAregistro);
    }

    public void goToRegAsMec(View V) {
        Intent irAregistro = new Intent(
                SelectUserType.this,
                RegistroActivity.class);
        irAregistro.putExtra("userType", "Mec");
        startActivity(irAregistro);
    }
}