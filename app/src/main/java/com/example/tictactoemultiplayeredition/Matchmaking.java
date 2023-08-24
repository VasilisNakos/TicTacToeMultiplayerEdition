package com.example.tictactoemultiplayeredition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchmaking);
        //inisializing firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        //check if there is an avaliabe game
        find_current_match(db,user);


    }

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
                        update_second_username(db,game_id,user);
                    }
                }
                if(flag) {
                    create_new_room(db, user);
                }
            }
        });


    }

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
}