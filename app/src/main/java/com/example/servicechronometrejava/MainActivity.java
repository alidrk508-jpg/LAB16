package com.example.servicechronometrejava;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private TextView drk_tvTemps;
    private Button drk_btnStart, drk_btnStop;
    private ChronometreService drk_chronometreService;
    private boolean drk_isBound = false;

    private final Handler drk_handler = new Handler(Looper.getMainLooper());
    private final Runnable drk_updateTimeTask = new Runnable() {
        @Override
        public void run() {
            if (drk_isBound && drk_chronometreService != null) {
                drk_tvTemps.setText(drk_chronometreService.getTempsFormate());
                drk_handler.postDelayed(this, 1000);
            }
        }
    };

    // Connexion au service (Bound Service)
    private final ServiceConnection drk_connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName drk_name, IBinder drk_service) {
            ChronometreService.LocalBinder drk_binder = (ChronometreService.LocalBinder) drk_service;
            drk_chronometreService = drk_binder.getService();
            drk_isBound = true;
            drk_handler.post(drk_updateTimeTask);
        }

        @Override
        public void onServiceDisconnected(ComponentName drk_name) {
            drk_isBound = false;
            drk_handler.removeCallbacks(drk_updateTimeTask);
        }
    };

    @Override
    protected void onCreate(Bundle drk_savedInstanceState) {
        super.onCreate(drk_savedInstanceState);
        setContentView(R.layout.activity_main);

        drk_tvTemps = findViewById(R.id.drk_tvTemps);
        drk_btnStart = findViewById(R.id.drk_btnStart);
        drk_btnStop = findViewById(R.id.drk_btnStop);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        drk_btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View drk_v) {
                startChronometre();
            }
        });

        drk_btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View drk_v) {
                stopChronometre();
            }
        });
    }

    private void startChronometre() {
        Intent drk_intent = new Intent(this, ChronometreService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(drk_intent);
        } else {
            startService(drk_intent);
        }
        bindService(drk_intent, drk_connection, Context.BIND_AUTO_CREATE);
    }

    private void stopChronometre() {
        Intent drk_intent = new Intent(this, ChronometreService.class);
        drk_intent.setAction("STOP");
        startService(drk_intent); // Envoie l'action STOP

        if (drk_isBound) {
            unbindService(drk_connection);
            drk_isBound = false;
        }
        drk_handler.removeCallbacks(drk_updateTimeTask);
        drk_tvTemps.setText("00:00");
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Se reconnecter au service s'il tourne déjà
        Intent drk_intent = new Intent(this, ChronometreService.class);
        bindService(drk_intent, drk_connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (drk_isBound) {
            unbindService(drk_connection);
            drk_isBound = false;
            drk_handler.removeCallbacks(drk_updateTimeTask);
        }
    }
}
