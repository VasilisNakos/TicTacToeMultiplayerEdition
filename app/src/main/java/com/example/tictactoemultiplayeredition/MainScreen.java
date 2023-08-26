package com.example.tictactoemultiplayeredition;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MainScreen extends AppCompatActivity implements Serializable {
    private FirebaseStorage storage;
    FirebaseUser user ;
    FirebaseFirestore db;
    StorageReference storageReference;
    ImageView settings_profile_picture;
    ImageView user_pfp;

    Intent main_screen;
    Intent main_activity ;

    private int imageview_index = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        TextView username_textview = findViewById(R.id.user_Username);
        TextView points_textview = findViewById(R.id.user_points);
        user_pfp = findViewById(R.id.user_pfp);
        ImageButton multiplayer_button = findViewById(R.id.multiplayer_button);
        Intent matchmaking_intent = new Intent(this,Matchmaking.class);
        main_activity = new Intent(this,MainActivity.class);
        main_screen = new Intent(this,MainScreen.class);
        //Initializing leaderboard fields
        TextView top1_username = findViewById(R.id.top1_user_username);
        TextView top1_points = findViewById(R.id.top1_user_points);
        TextView top2_username = findViewById(R.id.top2_user_username);
        TextView top2_points = findViewById(R.id.top2_user_points);
        TextView top3_username = findViewById(R.id.top3_user_username);
        TextView top3_points = findViewById(R.id.top3_user_points);
        ImageView top_user_1_pfp = findViewById(R.id.top1_user_pfp);
        ImageView top_user_2_pfp = findViewById(R.id.top2_user_pfp);
        ImageView top_user_3_pfp = findViewById(R.id.top3_user_pfp);
        multiplayer_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(matchmaking_intent);
            }
        });




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

                    if(!(document.getString("pfp_id").equals("###"))){

                        new DownloadImageTask(user_pfp).execute(document.getString("pfp_id"));
                    }

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
                            if(!documentSnapshot.getString("pfp_id").equals("###")){
                                new DownloadImageTask(top_user_1_pfp).execute(documentSnapshot.getString("pfp_id"));
                            }
                        }
                        if(i == 1){
                            top2_username.setText(documentSnapshot.getString("username"));
                            top2_points.setText(documentSnapshot.getString("points"));
                            if(!documentSnapshot.getString("pfp_id").equals("###")){
                                new DownloadImageTask(top_user_2_pfp).execute(documentSnapshot.getString("pfp_id"));
                            }
                        }
                        if(i == 2){
                            top3_username.setText(documentSnapshot.getString("username"));
                            top3_points.setText(documentSnapshot.getString("points"));
                            if(!documentSnapshot.getString("pfp_id").equals("###")){
                                new DownloadImageTask(top_user_3_pfp).execute(documentSnapshot.getString("pfp_id"));
                            }
                        }

                        i++;
                    }
                }
            }else{
                //handling with an error pop up
            }
        });




        //here we will check if the user has a pfp. if it does have we have to draw it as a circlue

        user_pfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                settings_showCustomPopup(db,user,auth);

            }
        });




        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        getSupportActionBar().hide();

        }


    private void settings_showCustomPopup(FirebaseFirestore db,FirebaseUser user,FirebaseAuth auth) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.user_setting_pop_up, null);
        builder.setView(popupView);
        //setting ellements
        ImageButton back = popupView.findViewById(R.id.settings_back_button);
        ImageButton upload_pfp = popupView.findViewById(R.id.uploadbutton);
        EditText change_username = popupView.findViewById(R.id.change_username);
        TextView logout = popupView.findViewById(R.id.logout);

        change_username.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    showCustomPopup(db,user,change_username.getText().toString(),auth);
                    return true;
                }
                return false;
            }
        });
        settings_profile_picture = popupView.findViewById(R.id.settings_profile_picture);
         String image_download_Uri;
         db.collection("users").document(user.getUid()).get().addOnCompleteListener(this, task -> {
             if(task.isSuccessful()){
                 DocumentSnapshot documentSnapshot = task.getResult();
                 change_username.setText(documentSnapshot.getString("username"));
                 if(!documentSnapshot.getString("pfp_id").equals("###")){
                     new DownloadImageTask(settings_profile_picture).execute(documentSnapshot.getString("pfp_id"));
                 }
             }
         });
         logout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 auth.signOut();
                 startActivity(main_activity);
             }
         });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
        //setting listeners and actions
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });
        upload_pfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });


    }
    private static final int PICK_IMAGE_REQUEST = 1;

    // Call this method when you want to start the image selection process
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Handle the result of the image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();


            // Upload the selected image to Firebase Storage
            uploadImage(imageUri);
        }
    }
    private void uploadImage(Uri imageUri) {
        settings_profile_picture.setImageURI(imageUri);
        setCircularImage(settings_profile_picture);
        if (imageUri != null) {
            StorageReference imageRef = storageReference.child("images/" + imageUri.getLastPathSegment());
            UploadTask uploadTask = imageRef.putFile(imageUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Image uploaded successfully
                    // You can get the download URL of the uploaded image from taskSnapshot
                    Task<Uri> downloadUrlTask = imageRef.getDownloadUrl();
                    downloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            String downloadUrl = downloadUri.toString();
                            Map<String,Object> updates = new HashMap<>();
                            updates.put("pfp_id",downloadUrl);
                            db.collection("users").document(user.getUid()).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    user_pfp.setImageURI(imageUri);
                                    setCircularImage(user_pfp);
                                }
                            });

                            // Do something with the download URL, like storing it in a database
                        }
                    });
                }
            });
        }
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
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;


        public DownloadImageTask(ImageView view) {
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

                imageView.setImageBitmap(result);
                setCircularImage(imageView);
            }
        }
    }

    private void showCustomPopup(FirebaseFirestore db,FirebaseUser user,String text,FirebaseAuth auth) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View popupView = inflater.inflate(R.layout.custom_pop_up, null);
        builder.setView(popupView);
        TextView popupTextView = popupView.findViewById(R.id.popupText);
        popupTextView.setText("Are you sure you want to change your username?");
        ImageButton no = popupView.findViewById(R.id.no_matchmaking);
        ImageButton yes = popupView.findViewById(R.id.yes_matchmaking);


        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings_showCustomPopup(db,user,auth);
                dialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> updates = new HashMap<>();
                updates.put("username",text);
                db.collection("users").document(user.getUid()).update(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        startActivity(main_screen);
                    }
                });
            }
        });
    }



}