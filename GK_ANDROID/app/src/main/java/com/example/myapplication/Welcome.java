package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;

import androidx.appcompat.app.AppCompatActivity;

public class Welcome extends AppCompatActivity {
    LottieAnimationView json;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        setControl();
        setAnim();

    }
    private void setAnim() {
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                json.setVisibility(View.VISIBLE);
            }
        }, 500);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                finish();
                Intent intent = new Intent(Welcome.this, MainActivity.class);
                overridePendingTransition(R.anim.left, R.anim.right);
                startActivity( intent );
            }
        }, 300);
    }
    private void setControl() {

        json = findViewById(R.id.json);
        json.setVisibility(View.INVISIBLE);
    }
}
