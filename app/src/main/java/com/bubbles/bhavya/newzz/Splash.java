package com.bubbles.bhavya.newzz;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        new CountDownTimer(5000,1000)
        {
            public void onFinish()
            {
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);

                finish();
            }

            public void onTick(long millisUntilFinished)
            {

            }
        }.start();

    }
}
