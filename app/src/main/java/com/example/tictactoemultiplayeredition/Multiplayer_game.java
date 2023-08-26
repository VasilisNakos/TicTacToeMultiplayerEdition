package com.example.tictactoemultiplayeredition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.io.InputStream;
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
    String other_user_pfp_uri;
    String me_pfp_uri;

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

    private Intent main_screen;
    private Intent multiplayer;








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
        progressBar = findViewById(R.id.progressBar);
        count_down_textview = findViewById(R.id.count_down_textview);
        TextView turn_textview = findViewById(R.id.turn_textview);
        TextView me_username = findViewById(R.id.me_username);
        ImageView me_pfp = findViewById(R.id.me_pfp);
        TextView other_user_username = findViewById(R.id.other_user_username);
        ImageView other_user_pfp = findViewById(R.id.other_user_pfp);

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
        main_screen = new Intent(this,MainScreen.class);
        multiplayer = new Intent(this,Multiplayer_game.class);









        //firebase inisiation
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser me = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String game_id = getIntent().getStringExtra("game_id");
        is_my_turn = false;
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomPopup(db,me,game_id);
            }
        });




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
               db.collection("users").document(other_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                              @Override
                                                                                              public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                                                                                  if(task1.isSuccessful()){
                                                                                                      DocumentSnapshot documentSnapshot1 = task1.getResult();
                                                                                                      other_user_username.setText(documentSnapshot1.getString("username"));
                                                                                                      if(!documentSnapshot1.getString("pfp_id").equals("###")){
                                                                                                          new DownloadImageTask1(other_user_pfp).execute(documentSnapshot1.getString("pfp_id"));
                                                                                                      }
                                                                                                  }

                                                                                              }
                                                                                          }
               );

           }else{
               //something went wrong
           }
        });
        String[] searchValues = {me.getUid(), other_user_id }; // Replace with your search values
        db.collection("users").document(me.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                       @Override
                                                                                       public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                                                                           if(task1.isSuccessful()){
                                                                                               DocumentSnapshot documentSnapshot1 = task1.getResult();
                                                                                               me_username.setText(documentSnapshot1.getString("username"));
                                                                                               if(!documentSnapshot1.getString("pfp_id").equals("###")){
                                                                                                   new DownloadImageTask1(me_pfp).execute(documentSnapshot1.getString("pfp_id"));
                                                                                               }

                                                                                           }

                                                                                       }
                                                                                   }
        );





                r1_c1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!r1_c1_already_pressed && is_my_turn) {
                            is_my_turn = false;
                            r1_c1_already_pressed = true;
                            update_db(db, game_id, me, "r1c1");
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
                                        showCustomPopup_won(db,me,game_id,"You won!!! Do you want to play again?");
                                    }
                                });
                            }
                        });
                        showCustomPopup_won(db,me,game_id,"You won!!! Do you want to play again?");
                    }else {
                        showCustomPopup_won(db,me,game_id,"Better luck next time! Do you want to play again?");
                    }
                }
            }
        });







    }

    private void load_images(ImageView otherUserPfp, String otherUserPfpUri, ImageView mePfp, String mePfpUri) {
        new DownloadImageTask1(otherUserPfp).execute(otherUserPfpUri);
        new DownloadImageTask1(mePfp).execute(me_pfp_uri);
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
    private class DownloadImageTask1 extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;


        public DownloadImageTask1(ImageView view) {
            imageView = view;

        }
        @Override
        protected Bitmap doInBackground(String... urls) {
            String imageUrl = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(imageUrl).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                int targetWidth = imageView.getWidth();
                int targetHeight = imageView.getHeight();

                // Calculate the scale factors to fit the bitmap within the ImageView
                float scaleFactor = Math.min(
                        (float) targetWidth / result.getWidth(),
                        (float) targetHeight / result.getHeight()
                );

                // Create a scaled bitmap
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                        result,
                        Math.round(result.getWidth() * scaleFactor),
                        Math.round(result.getHeight() * scaleFactor),
                        false
                );

                // Create a circular mask bitmap
                Bitmap circularBitmap = Bitmap.createBitmap(
                        scaledBitmap.getWidth(), scaledBitmap.getHeight(), Bitmap.Config.ARGB_8888
                );
                Canvas canvas = new Canvas(circularBitmap);
                Paint paint = new Paint();
                paint.setAntiAlias(true);
                canvas.drawCircle(
                        scaledBitmap.getWidth() / 2f,
                        scaledBitmap.getHeight() / 2f,
                        Math.min(scaledBitmap.getWidth(), scaledBitmap.getHeight()) / 2f,
                        paint
                );

                // Apply the circular mask to the scaled bitmap
                Bitmap circularScaledBitmap = Bitmap.createBitmap(
                        scaledBitmap.getWidth(),
                        scaledBitmap.getHeight(),
                        Bitmap.Config.ARGB_8888
                );
                Canvas canvas2 = new Canvas(circularScaledBitmap);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                canvas2.drawBitmap(scaledBitmap, 0, 0, null);
                canvas2.drawBitmap(circularBitmap, 0, 0, paint);
                paint.setXfermode(null);

                // Set the circular scaled bitmap to the ImageView
                imageView.setImageBitmap(circularScaledBitmap);

            }
        }
    }
    private void showCustomPopup(FirebaseFirestore db,FirebaseUser me,String game_id) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.custom_pop_up, null);
        builder.setView(popupView);
        TextView popupTextView = popupView.findViewById(R.id.popupText);
        popupTextView.setText("Are you sure you want to quite the game? You will lose 100 points");
        ImageButton no = popupView.findViewById(R.id.no_matchmaking);
        ImageButton yes = popupView.findViewById(R.id.yes_matchmaking);


        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> updates = new HashMap<>();
                updates.put("terminated_by",me.getUid());

                db.collection("games").document(game_id).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        startActivity(main_screen);
                    }
                });
            }
        });
    }
    private void showCustomPopup_won(FirebaseFirestore db,FirebaseUser me,String game_id,String text) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.custom_pop_up, null);
        builder.setView(popupView);
        TextView popupTextView = popupView.findViewById(R.id.popupText);
        popupTextView.setText(text);
        ImageButton no = popupView.findViewById(R.id.no_matchmaking);
        ImageButton yes = popupView.findViewById(R.id.yes_matchmaking);


        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(main_screen);
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(multiplayer);
            }
        });
    }
    private void setCircularImage(ImageView imageView) {
        // Get the bitmap from the image view
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        // Create a circular bitmap using shader
        Bitmap circularBitmap = getCircularBitmap(bitmap);

        // Set the circular bitmap to the ImageView
        imageView.setImageBitmap(circularBitmap);
    }
    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Calculate the radius to form a circular bitmap
        int radius = Math.min(width, height) / 2;

        // Create a new bitmap with the calculated radius
        Bitmap circularBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // Create a canvas to draw on the new bitmap
        Canvas canvas = new Canvas(circularBitmap);

        // Create a paint object and set shader to create a circular bitmap
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setShader(shader);

        // Draw the circular bitmap on the canvas
        canvas.drawCircle(width / 2, height / 2, radius, paint);

        return circularBitmap;
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