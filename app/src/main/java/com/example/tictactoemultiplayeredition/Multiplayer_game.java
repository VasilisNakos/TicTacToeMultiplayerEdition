package com.example.tictactoemultiplayeredition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Multiplayer_game extends AppCompatActivity {
    //inisializing game state

    public boolean is_my_turn;
    public ImageButton r1_c1 ;
    public ImageButton r1_c2;
    public ImageButton r1_c3;
    public ImageButton r2_c1;
    public ImageButton r2_c2;
    public ImageButton r2_c3;
    public ImageButton r3_c1;
    public ImageButton r3_c2;
    public ImageButton r3_c3;

    public boolean r1_c1_already_pressed = false;
    public boolean r1_c2_already_pressed = false;
    public boolean r1_c3_already_pressed = false;
    public boolean r2_c1_already_pressed = false;
    public boolean r2_c2_already_pressed = false;
    public boolean r2_c3_already_pressed = false;
    public boolean r3_c1_already_pressed = false;
    public boolean r3_c2_already_pressed = false;
    public boolean r3_c3_already_pressed = false;

    public String other_user_id = "";

    public ArrayList<String> game_state;
    private ProgressBar progressBar;

    private TextView count_down_textview;
    public boolean countDownTimer_is_running = false;
    private CountDownTimer countDownTimer;








    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_game);
        getSupportActionBar().hide();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ImageButton back_button = findViewById(R.id.back_button_multiplayer);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("back");
            }
        });
        progressBar = findViewById(R.id.progressBar);
        count_down_textview = findViewById(R.id.count_down_textview);
        TextView turn_textview = findViewById(R.id.turn_textview);
        //inisiating button to white
        r1_c1 = findViewById(R.id.r1_c1);
        r1_c2 = findViewById(R.id.r1_c2);
        r1_c3 = findViewById(R.id.r1_c3);
        r2_c1 = findViewById(R.id.r2_c1);
        r2_c2 = findViewById(R.id.r2_c2);
        r2_c3 = findViewById(R.id.r2_c3);
        r3_c1 = findViewById(R.id.r3_c1);
        r3_c2 = findViewById(R.id.r3_c2);
        r3_c3 = findViewById(R.id.r3_c3);
        int emtpy_drawbleRef = R.drawable.empty_button;
        r1_c1.setImageResource(emtpy_drawbleRef);
        r1_c2.setImageResource(emtpy_drawbleRef);
        r1_c3.setImageResource(emtpy_drawbleRef);
        r2_c1.setImageResource(emtpy_drawbleRef);
        r2_c2.setImageResource(emtpy_drawbleRef);
        r2_c3.setImageResource(emtpy_drawbleRef);
        r3_c3.setImageResource(emtpy_drawbleRef);
        r3_c1.setImageResource(emtpy_drawbleRef);
        r3_c2.setImageResource(emtpy_drawbleRef);
        game_state = new ArrayList<>();







        //firebase inisiation
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser me = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String game_id = getIntent().getStringExtra("game_id");
        is_my_turn = false;



        //inisializing the game
        db.collection("games").document(game_id).get().addOnCompleteListener(this,task ->{
            if(task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                if(documentSnapshot.getString("username_user1").equals(me.getUid())){
                    other_user_id = documentSnapshot.getString("username_user2");
                }else{
                    other_user_id = documentSnapshot.getString("username_user1");
                }
               if(me.getUid().equals(documentSnapshot.getString("turn"))){
                   is_my_turn = true;
               }

           }else{
               //something went wrong
           }
        });

        r1_c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!r1_c1_already_pressed && is_my_turn){
                    is_my_turn = false;
                    r1_c1_already_pressed = true;
                    update_db(db,game_id,me,"r1c1");
                }

            }
        });
        r1_c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!r1_c2_already_pressed && is_my_turn){

                    is_my_turn = false;
                    r1_c2_already_pressed = true;
                    update_db(db,game_id,me,"r1c2");
                }

            }
        });
        r1_c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!r1_c3_already_pressed && is_my_turn){
                    is_my_turn = false;
                    r1_c3_already_pressed = true;
                    update_db(db,game_id,me,"r1c3");

                }
            }
        });
        r2_c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!r2_c1_already_pressed && is_my_turn){
                    is_my_turn = false;
                    r2_c1_already_pressed = true;
                    update_db(db,game_id,me,"r2c1");
                }

            }
        });
        r2_c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!r2_c2_already_pressed && is_my_turn){
                    is_my_turn = false;
                    r2_c2_already_pressed = true;
                    update_db(db,game_id,me,"r2c2");
                }

            }
        });
        r2_c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!r2_c3_already_pressed && is_my_turn){
                    is_my_turn = false;
                    r2_c3_already_pressed = true;
                    update_db(db,game_id,me,"r2c3");
                }
            }
        });
        r3_c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!r3_c1_already_pressed && is_my_turn){
                    is_my_turn = false;
                    r3_c1_already_pressed = true;
                    update_db(db,game_id,me,"r3c1");
                }

            }
        });
        r3_c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!r3_c2_already_pressed && is_my_turn){
                    is_my_turn = false;
                    r3_c2_already_pressed = true;
                    update_db(db,game_id,me,"r3c2");
                }

            }
        });
        r3_c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!r3_c3_already_pressed && is_my_turn){
                    is_my_turn = false;
                    r3_c3_already_pressed = true;
                    update_db(db,game_id,me,"r3c3");
                }

            }
        });



        //listen to changes
        db.collection("games").document(game_id).addSnapshotListener((documentSnapshot,e)-> {
            if (e != null) {
                // Handle error
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {

                int o_reference = R.drawable.o;
                int x_reference = R.drawable.x;
                int empty_reference = R.drawable.empty_button;
                if(documentSnapshot.getString("r1c1").equals("")){
                    r1_c1.setImageResource(empty_reference);
                }else if(documentSnapshot.getString("r1c1").equals(me.getUid())){
                    r1_c1.setImageResource(x_reference);
                    r1_c1_already_pressed = true;
                }else{
                    r1_c1.setImageResource(o_reference);
                    r1_c1_already_pressed = true;
                }
                if(documentSnapshot.getString("r1c2").equals("")){
                    r1_c2.setImageResource(empty_reference);
                }else if(documentSnapshot.getString("r1c2").equals(me.getUid())){
                    r1_c2.setImageResource(x_reference);
                    r1_c2_already_pressed = true;
                }else{
                    r1_c2.setImageResource(o_reference);
                    r1_c2_already_pressed = true;
                }
                if(documentSnapshot.getString("r1c3").equals("")){
                    r1_c3.setImageResource(empty_reference);
                }else if(documentSnapshot.getString("r1c3").equals(me.getUid())){
                    r1_c3.setImageResource(x_reference);
                    r1_c3_already_pressed = true;
                }else{
                    r1_c3.setImageResource(o_reference);
                    r1_c3_already_pressed = true;

                }
                if(documentSnapshot.getString("r2c1").equals("")){
                    r2_c1.setImageResource(empty_reference);
                }else if(documentSnapshot.getString("r2c1").equals(me.getUid())){
                    r2_c1.setImageResource(x_reference);
                    r2_c1_already_pressed = true;
                }else{
                    r2_c1.setImageResource(o_reference);
                    r2_c1_already_pressed = true;
                }
                if(documentSnapshot.getString("r2c2").equals("")){
                    r2_c2.setImageResource(empty_reference);
                }else if(documentSnapshot.getString("r2c2").equals(me.getUid())){
                    r2_c2.setImageResource(x_reference);
                    r2_c2_already_pressed = true;
                }else{
                    r2_c2.setImageResource(o_reference);
                    r2_c2_already_pressed = true;
                }
                if(documentSnapshot.getString("r2c3").equals("")){
                    r2_c3.setImageResource(empty_reference);
                }else if(documentSnapshot.getString("r2c3").equals(me.getUid())){
                    r2_c3.setImageResource(x_reference);
                    r2_c3_already_pressed = true;
                }else{
                    r2_c3.setImageResource(o_reference);
                    r2_c3_already_pressed = true;
                }
                if(documentSnapshot.getString("r3c1").equals("")){
                    r3_c1.setImageResource(empty_reference);
                }else if(documentSnapshot.getString("r3c1").equals(me.getUid())){
                    r3_c1.setImageResource(x_reference);
                    r3_c1_already_pressed = true;
                }else{
                    r3_c1.setImageResource(o_reference);
                    r3_c1_already_pressed = true;
                }
                if(documentSnapshot.getString("r3c2").equals("")){
                    r3_c2.setImageResource(empty_reference);
                }else if(documentSnapshot.getString("r3c2").equals(me.getUid())){
                    r3_c2.setImageResource(x_reference);
                    r3_c2_already_pressed = true;
                }else{
                    r3_c2.setImageResource(o_reference);
                    r3_c2_already_pressed = true;
                }
                if(documentSnapshot.getString("r3c3").equals("")){
                    r3_c3.setImageResource(empty_reference);
                }else if(documentSnapshot.getString("r3c3").equals(me.getUid())){
                    r3_c3.setImageResource(x_reference);
                    r3_c3_already_pressed = true;
                }else{
                    r3_c3.setImageResource(o_reference);
                    r3_c3_already_pressed = true;
                }
                game_state = new ArrayList<>();
                game_state.add(documentSnapshot.getString("r1c1"));
                game_state.add(documentSnapshot.getString("r1c2"));
                game_state.add(documentSnapshot.getString("r1c3"));
                game_state.add(documentSnapshot.getString("r2c1"));
                game_state.add(documentSnapshot.getString("r2c2"));
                game_state.add(documentSnapshot.getString("r2c3"));
                game_state.add(documentSnapshot.getString("r3c1"));
                game_state.add(documentSnapshot.getString("r3c2"));
                game_state.add(documentSnapshot.getString("r3c3"));
                won(db,game_id);
                is_my_turn = false;
                if(!documentSnapshot.getString("terminated_by").equals("")){
                    if(documentSnapshot.getString("terminated_by").equals(me.getUid())){
                        System.out.println("i terminated the game");
                        db.collection("users").document(me.getUid()).get().addOnCompleteListener(this, task -> {
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot1 = task.getResult();
                                String data = documentSnapshot1.getString("points");
                                Integer points = Integer.parseInt(data);
                                points = points - 100;
                                Map<String,Object> updates = new HashMap<>();
                                updates.put("points", points.toString());
                                db.collection("users").document(me.getUid()).update(updates).addOnCompleteListener(this, task2 ->{
                                        if(task2.isSuccessful()){
                                            Intent main_screen = new Intent(this,MainScreen.class);
                                            startActivity(main_screen);
                                        }
                                });
                            }
                        });
                    }else{
                        System.out.println("someone else terminated the game");
                        Intent main_screen = new Intent(this,MainScreen.class);
                        startActivity(main_screen);
                    }
                }
                if(documentSnapshot.getString("won").equals("")){
                    if(documentSnapshot.getString("turn").equals(me.getUid())){
                        turn_textview.setText("It's your turn");
                        startLoadingTimer(db,game_id,me);
                        is_my_turn = true;

                    }else{
                        turn_textview.setText("It's opponents turn");
                        startLoadingTimer(db,game_id,me);
                    }
                }else{
                    if(documentSnapshot.getString("won").equals(me.getUid())){
                        db.collection("users").document(me.getUid()).get().addOnCompleteListener(this, task1 -> {
                            if(task1.isSuccessful()){
                                DocumentSnapshot documentSnapshot1 = task1.getResult();
                                String data = documentSnapshot1.getString("points");
                                Integer points = Integer.parseInt(data);
                                points = points + 100;
                                Map<String,Object> updates = new HashMap<>();
                                updates.put("points", points.toString());
                                db.collection("users").document(me.getUid()).update(updates).addOnCompleteListener(this, task2 ->{
                                    if(task2.isSuccessful()){
                                        Intent main_screen = new Intent(this,MainScreen.class);
                                        startActivity(main_screen);
                                    }
                                });
                            }
                        });
                    }else {
                        Intent main_screen = new Intent(this,MainScreen.class);
                        startActivity(main_screen);
                    }
                }
            }
        });







    }

    //creating the methon that checks if someone won
    private String won(String[] l){
        if(l[0].equals(l[1]) && l[1].equals(l[2]) && !l[0].equals("")){return l[0];}
        if(l[3].equals(l[4]) && l[4].equals(l[5]) && !l[3].equals("") ){return l[3];}
        if(l[6].equals(l[7]) && l[7].equals(l[8]) && !l[6].equals("")){return l[6];}
        if(l[0].equals(l[3]) && l[3].equals(l[6]) && !l[0].equals("")){return l[0];}
        if(l[1].equals(l[4]) && l[4].equals(l[7]) && !l[1].equals("")){return l[1];}
        if(l[2].equals(l[5]) && l[5].equals(l[8]) && !l[2].equals("")){return l[2];}
        if(l[0].equals(l[4]) && l[4].equals(l[8]) && !l[0].equals("")){return l[0];}
        if(l[2].equals(l[4]) && l[4].equals(l[6]) && !l[2].equals("")){return l[2];}
        return null;
    }
    private void update_db(FirebaseFirestore db, String game_id,FirebaseUser me,String collum){
        Map<String,Object> updates = new HashMap<>();
        updates.put("turn",other_user_id);
        updates.put(collum,me.getUid());
        db.collection("games").document(game_id).update(updates).addOnSuccessListener(aVoid -> {
                    // Document updated successfully
            System.out.println("database updated succefully");
        }).addOnFailureListener(e -> {});
    }
    private void won(FirebaseFirestore db,String game_id){
        String winner = "";
        if(game_state.get(0).equals(game_state.get(1)) && game_state.get(1).equals(game_state.get(2)) && !game_state.get(0).equals("")){winner = game_state.get(0);}
        if(game_state.get(3).equals(game_state.get(4)) && game_state.get(4).equals(game_state.get(5)) && !game_state.get(3).equals("")){winner = game_state.get(3);}
        if(game_state.get(6).equals(game_state.get(7)) && game_state.get(7).equals(game_state.get(8)) && !game_state.get(6).equals("")){winner = game_state.get(6);}
        if(game_state.get(0).equals(game_state.get(3)) && game_state.get(3).equals(game_state.get(6)) && !game_state.get(0).equals("")){winner = game_state.get(0);}
        if(game_state.get(1).equals(game_state.get(4)) && game_state.get(4).equals(game_state.get(7)) && !game_state.get(1).equals("")){winner = game_state.get(1);}
        if(game_state.get(2).equals(game_state.get(5)) && game_state.get(5).equals(game_state.get(8)) && !game_state.get(2).equals("")){winner = game_state.get(2);}
        if(game_state.get(0).equals(game_state.get(4)) && game_state.get(4).equals(game_state.get(8)) && !game_state.get(0).equals("")){winner = game_state.get(0);}
        if(game_state.get(2).equals(game_state.get(4)) && game_state.get(4).equals(game_state.get(6)) && !game_state.get(0).equals("")){winner = game_state.get(2);}
        System.out.println("the winner is" + winner);

        if(!winner.equals("")){
            Map<String,Object> updates = new HashMap<>();
            updates.put("won",winner);
            db.collection("games").document(game_id).update(updates).addOnCompleteListener(this, task -> {
               if(task.isSuccessful()){
                   System.out.println("databaseupdates");
               }
            });
        }



    }

    private void startLoadingTimer(FirebaseFirestore db, String game_id,FirebaseUser me) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(100000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int progress = (int) millisUntilFinished / 1000;
                count_down_textview.setText(Integer.toString(100-(-1*(progress - 100))));
                progressBar.setProgress(100 - (-1*(progress - 100)));
            }

            @Override
            public void onFinish() {
                // Loading complete, you can perform any required actions here
                Map<String,Object> updates = new HashMap<>();
                updates.put("terminated_by",me.getUid());
                if(is_my_turn) {
                    db.collection("games").document(game_id).update(updates);

                }
            }
        };

        countDownTimer.start();

    }

}