package com.example.tictactoemultiplayeredition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LOG_IN extends AppCompatActivity implements Serializable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        getSupportActionBar().hide();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        FirebaseApp.initializeApp(this);
        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();

        EditText email_text_field = findViewById(R.id.login_in_email_textfield);
        EditText password_textfield = findViewById(R.id.log_in_password_textfield);
        TextView error = findViewById(R.id.error_loging_in);
        error.setVisibility(View.INVISIBLE);

        ImageButton back_button = findViewById(R.id.back_button_login_to_main_activity);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton log_in = findViewById(R.id.login_in_button_to_activity_main_screen);
        Intent main_sceen_intent = new Intent(this, MainScreen.class);
        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
                Pattern pattern = Pattern.compile(emailRegex);
                Matcher matcher = pattern.matcher(email_text_field.getText().toString());

                if(matcher.matches()){
                    auth.signInWithEmailAndPassword(email_text_field.getText().toString(),password_textfield.getText().toString())
                            .addOnCompleteListener(task -> {
                               if(task.isSuccessful()){
                                   FirebaseUser user = auth.getCurrentUser();
                                   //main_sceen_intent.putExtra("Uid",user.getUid());
                                   //main_sceen_intent.putExtra("user", user);
                                   startActivity(main_sceen_intent);
                               }else{
                                   String errorMessage = task.getException().getMessage();
                                   error.setText(errorMessage);
                                   error.setVisibility(View.VISIBLE);

                               }
                            });
                }else{
                    error.setText("Email form is invalid");
                    error.setVisibility(View.VISIBLE);
                }

            }
        });




    }


}