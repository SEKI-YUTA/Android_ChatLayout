package com.yuta.chatlayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recycler_talk;
    private TalkAdapter adapter;
    private String imgUrl = "http://10.0.2.2/sample1.png";
    private String chatDataUrl = "http://10.0.2.2/chatData.json";
    private final int PERMISSION_CODE = 101;
    private String jsonString = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler_talk = findViewById(R.id.recycler_talk);
        recycler_talk.setHasFixedSize(true);
        recycler_talk.setLayoutManager(new GridLayoutManager(this, 1));


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PERMISSION_CODE);
        } else {
            appSetUP();
        }
    }

    private void appSetUP(){
//        Glide.with(this).load(Uri.parse(imgUrl)).into(img_localhost);
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(dataFetch);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    appSetUP();
                }
        }
    }

    private final Runnable dataFetch = new Runnable() {
        @Override
        public void run() {
            try {
                StringBuilder builder = new StringBuilder();
                URL url = new URL(chatDataUrl);
                InputStreamReader inputStreamReader = new InputStreamReader(url.openStream());
                BufferedReader bf = new BufferedReader(inputStreamReader);
                String line = null;
                while((line = bf.readLine()) != null) {
                    builder.append(line);
                }
                jsonString = builder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(jsonString != null) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonString);
                    JSONArray data = jsonObject.getJSONArray("data");
                    Log.d("dataLength", String.valueOf(data.length()));
                    adapter = new TalkAdapter(MainActivity.this, data);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            recycler_talk.setAdapter(adapter);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}