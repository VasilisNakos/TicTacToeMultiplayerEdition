package com.example.tictactoemultiplayeredition;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Matchmaking extends AppCompatActivity {
    public boolean no_opponenet = true;
    private int currentImageIndex = 0;
    private int[] imageResources = { R.drawable.earth2, R.drawable.earth3, R.drawable.earth4, R.drawable.earth5, R.drawable.earth6, R.drawable.earth7, R.drawable.earth8, R.drawable.earth10, R.drawable.earth11};
    private ImageView imageView;
    private TextView wating_text_view;
    private Handler handler = new Handler();
    private String generated_game_id = null;
    private Intent main_screen;

    private FirebaseFirestore db_public;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaking);
        getSupportActionBar().hide();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        imageView = findViewById(R.id.earhts);
        wating_text_view = findViewById(R.id.waiting_for_opponent_text);
        main_screen = new Intent(this,MainScreen.class);
        // Start the image changing process
        handler.post(imageChangeRunnable);
        //inisializing firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db_public = db;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        //check if there is an avaliabe game
        find_current_match(db,user);

        //back button
        ImageButton back_button = findViewById(R.id.back_button_matchmaking);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomPopup(db);
            }
        });



    }
    private Runnable imageChangeRunnable = new Runnable() {
        @Override
        public void run() {
            // Change the image resource
            String text= "Waiting for an opponent" ;
            imageView.setImageResource(imageResources[currentImageIndex]);
            for(int i = -1; i < currentImageIndex % 3;i++){
                text = text + " .";
            }
            wating_text_view.setText(text);
            // Increment the index for the next image
            currentImageIndex = (currentImageIndex + 1) % imageResources.length;

            // Schedule the next image change after 1/10 of a second
            handler.postDelayed(this, 500);
        }
    };



    void find_current_match(FirebaseFirestore db,FirebaseUser user){
        CollectionReference collectionReference = db.collection("games");
        collectionReference.get().addOnCompleteListener(this, task -> {
            boolean flag = true;
            if(task.isSuccessful()){
                QuerySnapshot queryDocumentSnapshots = task.getResult();
                for(DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()){
                    if(documentSnapshot.getString("username_user2").equals("")){
                        flag = false;
                        String game_id = documentSnapshot.getId();
                        generated_game_id = game_id;
                        update_second_username(db,game_id,user);
                    }
                }
                if(flag) {
                    create_new_room(db, user);
                }
            }
        });


    }
    //updating the second username in order to join a already created game
    void update_second_username(FirebaseFirestore db,String game_id,FirebaseUser user){
        Map<String, Object> updates = new HashMap<>();
        updates.put("username_user2", user.getUid());
        db.collection("games").document(game_id).update(updates).addOnCompleteListener(this,task -> {
            if(task.isSuccessful()){
                check_if_granted(db,game_id,user);
            }else{
                find_current_match(db,user);
            }
        });

    }
    //checking if someone else tried to join the same game, if not the game is granted
    void check_if_granted(FirebaseFirestore db,String game_id, FirebaseUser user){
        db.collection("games").document(game_id).get().addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                if(documentSnapshot.getString("username_user2").equals(user.getUid())){
                    //matchmaking complete
                    System.out.println("granted");
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("game_status", "ready");
                    //choose who is playing first
                    Random random = new Random();
                    int randint = random.nextInt(2);
                    if(randint == 0){
                        updates.put("turn",documentSnapshot.getString("username_user1"));
                    }else{
                        updates.put("turn",user.getUid());
                    }
                    db.collection("games").document(game_id).update(updates).addOnCompleteListener(this,task1 -> {
                        if(task1.isSuccessful()){

                            Intent multiplaer_intent = new Intent(this,Multiplayer_game.class);
                            multiplaer_intent.putExtra("game_id",game_id);
                            startActivity(multiplaer_intent);
                        }
                    });

                }
            }else{
                find_current_match(db,user);
            }
        });
    }
    //shows a pop up and handles the deletion of the game room in case a player
    //wants to quite matchmaking
    private void showCustomPopup(FirebaseFirestore db) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.custom_pop_up, null);
        builder.setView(popupView);
        TextView popupTextView = popupView.findViewById(R.id.popupText);
        popupTextView.setText("Are you sure you want to quite matchmaking?");
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
                if(generated_game_id != null ) {
                    db.collection("games").document(generated_game_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            startActivity(main_screen);
                        }
                    });
                }else{
                    startActivity(main_screen);
                }
            }
        });
    }
    //if there is no avaliable room the user creates a new one and
    //waits until an other user enters
    void create_new_room(FirebaseFirestore db, FirebaseUser user){

        Game game = new Game(user.getUid(),"","waiting","","","","","","","","","","","","");
        db.collection("games").add(game).addOnCompleteListener(this,task -> {
            if(task.isSuccessful()){
                String game_id = task.getResult().getId();
                System.out.println(game_id);
                db.collection("games").document(game_id).addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {


                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Document data changed, handle the changes
                        String data = documentSnapshot.getString("game_status");
                        generated_game_id = documentSnapshot.getId();
                        System.out.println(generated_game_id);
                        if(data.equals("ready") && no_opponenet){
                            Intent multiplaer_intent = new Intent(this,Multiplayer_game.class);
                            multiplaer_intent.putExtra("game_id",documentSnapshot.getId());
                            no_opponenet = false;
                            startActivity(multiplaer_intent);
                        }



                    }
                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the pending callbacks when the activity is destroyed
        handler.removeCallbacks(imageChangeRunnable);
        if(generated_game_id != null ) {
            db_public.collection("games").document(generated_game_id).delete();

        }

    }
}