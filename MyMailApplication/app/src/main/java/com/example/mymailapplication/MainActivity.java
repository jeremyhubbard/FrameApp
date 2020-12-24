package com.example.mymailapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

public class MainActivity extends AppCompatActivity{
//    private EditText editTextEmail;
//    private EditText editTextSubject;
//    private EditText editTextMessage;
//    private Button buttonSend;
    static int MyInt = 0;
    ImageView imageView;
    VideoView videoView;
    static int FileIndex = 0;
    SendMail sm = new SendMail(this);
    int EMAILDELAY = 20000000;
    int PICCHANGEDELAY = 20000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.my_image_view);
        videoView = (VideoView) findViewById(R.id.my_video_view);
        String filePath = getExternalFilesDir(null).getPath();
        File directory = new File(filePath);
        Log.d("Files", "Path: " + filePath);

        this.getSupportActionBar().hide();


        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                File[] files = directory.listFiles();

                //Do something after 10s
                if(files.length > 0) {
                    if(files[FileIndex].toString().toLowerCase().contains(".mp4"))
                    {
                        Log.i("Video", "video file detected at " + files[FileIndex].toString());
                        imageView.setVisibility(View.GONE);
                        videoView.setVisibility(View.VISIBLE);
                        videoView.setVideoPath(files[FileIndex++].toString());
                        videoView.start();

                        final Handler videorepeatHandler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(!videoView.isPlaying())
                                {
                                    videoView.start();
                                }
                            }
                        }, PICCHANGEDELAY/2);
                    }
                    else if(files[FileIndex].toString().toLowerCase().contains(".gif"))
                    {
                        imageView.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.GONE);
                        GlideApp.with(MainActivity.this)
                                .asGif()
                                .load(files[FileIndex++])
                                .override(3000, 8000)
                                .into(imageView);
                    }else {
                        imageView.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.GONE);
                        GlideApp.with(MainActivity.this)
                                .load(files[FileIndex++])
                                .override(3000, 8000)
                                .into(imageView);
                    }
                    if (FileIndex >= files.length) {
                        FileIndex = 0;
                    }
                }
                handler.postDelayed(this, PICCHANGEDELAY);
            }
        }, 100);

        final Handler emailHandler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getEmail();
                File directory = new File(filePath);
                File[] files = directory.listFiles();
                Log.d("Files", "Size: "+ files.length);
                for (int i = 0; i < files.length; i++)
                {
                    Log.d("Files", "FileName:" + files[i].getName());
                }
                handler.postDelayed(this, EMAILDELAY);
            }
        }, 3000);
    }

    private void getEmail() {

        sm.execute();
    }
}