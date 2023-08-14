package com.example.tictactoemultiplayeredition;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;

public class MainScreen extends AppCompatActivity implements Serializable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TextView username_textview = findViewById(R.id.user_Username);
        TextView points_textview = findViewById(R.id.user_points);
        //Initializing leaderboard fields
        TextView top1_username = findViewById(R.id.top1_user_username);
        TextView top1_points = findViewById(R.id.top1_user_points);
        TextView top2_username = findViewById(R.id.top2_user_username);
        TextView top2_points = findViewById(R.id.top2_user_points);
        TextView top3_username = findViewById(R.id.top3_user_username);
        TextView top3_points = findViewById(R.id.top3_user_points);




        //here we will set the username and points of the user
       db.collection("users").document(user.getUid())
               .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot document = task.getResult();
                if(document.exists()){
                    String username = document.getString("username");
                    String points = document.getString("points");
                    username_textview.setText(username);
                    points_textview.setText(points);

                }else{
                    //handling with an error pop up
                }
            }
        });
       //here we will fill the leaderboard
        Query leaderboard_query = db.collection("users")
                .orderBy("points",Query.Direction.DESCENDING).limit(3);
        leaderboard_query.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                QuerySnapshot querySnapshots = task.getResult();
                if(querySnapshots != null){
                    int i = 0;
                    for(DocumentSnapshot documentSnapshot : querySnapshots.getDocuments()){
                        if(i == 0){
                            top1_username.setText(documentSnapshot.getString("username"));
                            top1_points.setText(documentSnapshot.getString("points"));
                        }
                        if(i == 1){
                            top2_username.setText(documentSnapshot.getString("username"));
                            top2_points.setText(documentSnapshot.getString("points"));
                        }
                        if(i == 2){
                            top3_username.setText(documentSnapshot.getString("username"));
                            top3_points.setText(documentSnapshot.getString("points"));
                        }

                        i++;
                    }
                }
            }else{
                //handling with an error pop up
            }
        });




        //here we will check if the user has a pfp. if it does have we have to draw it as a circlue
        ImageView user_pfp = findViewById(R.id.user_pfp);
        boolean user_has_pfp = false;
        if(user_has_pfp){
            setCircularImage(user_pfp);
        }




        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();








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




}