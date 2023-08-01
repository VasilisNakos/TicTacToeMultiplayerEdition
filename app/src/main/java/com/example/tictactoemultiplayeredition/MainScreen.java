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

public class MainScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        ImageView user_pfp = findViewById(R.id.user_pfp);
        //here we will check if the user has a pfp. if it does have we have to draw it as a circlue
        boolean user_has_pfp = false;
        if(user_has_pfp){
            setCircularImage(user_pfp);
        }
        TextView user_Username = findViewById(R.id.user_Username);
        user_Username.setText("Georgebad");


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