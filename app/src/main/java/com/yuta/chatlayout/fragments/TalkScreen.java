package com.yuta.chatlayout.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.transition.Scene;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yuta.chatlayout.MainActivity;
import com.yuta.chatlayout.R;
import com.yuta.chatlayout.TalkAdapter;

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

public class TalkScreen extends Fragment {
    private RecyclerView recycler_talk;
    private TalkAdapter adapter;
    private String chatDataUrl = "http://10.0.2.2/chatData.json";
    private final int PERMISSION_CODE = 101;
    private String jsonString = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TransitionInflater inflater = TransitionInflater.from(getContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_talk_screen, container, false);
        recycler_talk = view.findViewById(R.id.recycler_talk);
        recycler_talk.setHasFixedSize(true);
        recycler_talk.setLayoutManager(new GridLayoutManager(getContext(), 1));

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.INTERNET}, PERMISSION_CODE);
        } else {
            appSetUP();
        }
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    appSetUP();
                }
        }
    }

    private void appSetUP() {
//        Glide.with(this).load(Uri.parse(imgUrl)).into(img_localhost);
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(dataFetch);
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
                while ((line = bf.readLine()) != null) {
                    builder.append(line);
                }
                jsonString = builder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (jsonString != null) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonString);
                    JSONArray data = jsonObject.getJSONArray("data");
                    Log.d("dataLength", String.valueOf(data.length()));
                    adapter = new TalkAdapter(getContext(), data, listener);
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

    private final TalkAdapter.MessageAction listener = new TalkAdapter.MessageAction() {
        @Override
        public void imgClicked(String url, View shareElement) {
            Bundle args = new Bundle();
            args.putString("url", url);
            Fragment fragment = new ImagePreview();
            fragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .addSharedElement(shareElement, "img_large")
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null).commit();
//            Scene imgPrev = Scene.getSceneForLayout(getActivity().findViewById(R.id.fragmentContainer), R.layout.fragment_image_preview, getContext());
//            TransitionManager.go(imgPrev);
        }
    };
}