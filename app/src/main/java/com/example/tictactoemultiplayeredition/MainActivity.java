package com.example.tictactoemultiplayeredition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ImageButton sign_up_btn = (ImageButton) findViewById(R.id.sign_up_button);
        Intent secondActivityIntent = new Intent(this, SIGN_UP.class);
        sign_up_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(secondActivityIntent);
            }
        });
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();

        //hello
    }
}
