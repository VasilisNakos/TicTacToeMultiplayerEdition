package com.example.tictactoemultiplayeredition;


import androidx.appcompat.app.AppCompatActivity;
import com.example.tictactoemultiplayeredition.USER;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SIGN_UP extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();
        FirebaseApp.initializeApp(this);
        FirebaseAuth auth;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        ImageButton back_button = findViewById(R.id.back_button);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        EditText email_field = findViewById(R.id.email_field);
        EditText username_field = findViewById(R.id.username_field);
        EditText password_field = findViewById(R.id.password_field);
        EditText confirm_password_field = findViewById(R.id.confirm_password_field);
        TextView error_textview = findViewById(R.id.error);
        error_textview.setVisibility(View.GONE);



        ImageButton  sing_up = findViewById(R.id.sign_up);
            //Intent secondActivityIntent = new Intent(this, MainScreen.class);
        sing_up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println(password_field.getText().toString());
                //startActivity(secondActivityIntent);
                boolean flag = true;
                boolean flag_password = password_field.getText().toString().equals(confirm_password_field.getText().toString());
                String email = email_field.getText().toString();
                String password = password_field.getText().toString();
                String username = username_field.getText().toString();
                boolean not_found_problem = true;

                if(!flag_password ){
                    error_textview.setText("Passwords don't much");
                    error_textview.setVisibility(View.VISIBLE);
                    flag = false;
                    not_found_problem = false;
                }
                String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
                Pattern pattern = Pattern.compile(emailRegex);
                Matcher matcher = pattern.matcher(email);
                if(!matcher.matches() && not_found_problem){
                    error_textview.setText("email is not valid");
                    error_textview.setVisibility(View.VISIBLE);
                    flag = false;
                    not_found_problem = false;
                }


                if(flag){
                    check_if_username_exists(db,username,password,email,auth,error_textview);

                }


            }
        });


    }

    void sign_up_user(FirebaseAuth auth,String email, String password,String username,FirebaseFirestore db){

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                FirebaseUser user = auth.getCurrentUser();
                String userId = user.getUid();
                USER newUser = new USER(userId,email,username,"200","###");
                db.collection("users").document(userId).set(newUser);
                Intent main_sceen_intent = new Intent(this, MainScreen.class);
                startActivity(main_sceen_intent);



            }else{
                System.out.println("Problem signing up");
            }
        });
    }

    void check_if_username_exists(FirebaseFirestore db, String username,String password,String email,FirebaseAuth auth,TextView error){

        db.collection("users").whereEqualTo("username",username).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                boolean usernameExists = !task.getResult().isEmpty();

                if(usernameExists){
                    error.setText("Username already exists");
                    error.setVisibility(View.VISIBLE);
                }else{
                    sign_up_user(auth,email,password,username,db);
                }

            }else{
                error.setText("Something went wrong please try again");
                error.setVisibility(View.VISIBLE);
            }
        });


    }





}