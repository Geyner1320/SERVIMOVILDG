package com.dgteam.servimovildg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.dgteam.servimovildg.logueo.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //variable de tiempo declarada para el intent de pagina al finalizar el SplashScreen
        TimerTask tarea = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        //Tiempo establecido para el SplashScreen
        Timer tiempo = new Timer();
        tiempo.schedule(tarea,3000);
    }
}