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
                if(flag_password){

                }else{
                    error_textview.setText("Passwords don't much");
                    error_textview.setVisibility(View.VISIBLE);
                    flag = false;
                }
                String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
                Pattern pattern = Pattern.compile(emailRegex);
                Matcher matcher = pattern.matcher(email);
                if(matcher.matches()){

                }else{
                    error_textview.setText("email is not valid");
                    error_textview.setVisibility(View.VISIBLE);
                    flag = false;
                }
                if(flag){
                    sign_up_user(auth,email,password,username,db);
                }


            }
        });


    }

    void sign_up_user(FirebaseAuth auth,String email, String password,String username,FirebaseFirestore db){

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                FirebaseUser user = auth.getCurrentUser();
                String userId = user.getUid();
                USER newUser = new USER(userId,email,username);
                db.collection("usernames").document(userId).set(newUser);
                Intent main_sceen_intent = new Intent(this, MainScreen.class);
                startActivity(main_sceen_intent);



            }else{
                System.out.println("Problem signing up");
            }
        });
    }





}