package com.yuta.chatlayout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TalkAdapter extends RecyclerView.Adapter<TalkAdapter.TalkViewHolder> {
    private Context context;
    private JSONArray data;

    public TalkAdapter(Context context, JSONArray data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public TalkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vote_item, parent, false);
        return new TalkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TalkViewHolder holder, int position) {
        String text = null;
        String imgUri = null;
        boolean isMe = true;
        try {
            JSONObject item = data.getJSONObject(holder.getAdapterPosition());
            text = item.getString("text");
            imgUri = item.getString("image");
            isMe = item.getBoolean("isMe");
            Log.d("isMe", String.valueOf(isMe));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(text != null) {
//            Log.d("MyLog", "is not null");
            holder.tv_text.setText(text);
        }
        if(!imgUri.equals("null")) {
//            Log.d("MyLog", imgUri);
//            Log.d("MyLog", imgUri.getClass().toString());
            holder.img_content.setVisibility(View.VISIBLE);
            Glide.with(context).load(Uri.parse(imgUri)).into(holder.img_content);
        }
        if(!isMe) {
            Drawable bg = context.getDrawable(R.drawable.someone_vote_bg);
            holder.voteWrapper.setGravity(Gravity.START);
            holder.textWrapper.setBackground(bg);
        }
    }

    @Override
    public int getItemCount() {
        return data.length();
    }

    class TalkViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_text;
        public ImageView img_content;
        public LinearLayout textWrapper, voteWrapper;
        public TalkViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_text = itemView.findViewById(R.id.tv_text);
            img_content = itemView.findViewById(R.id.img_content);
            textWrapper = itemView.findViewById(R.id.textWrapper);
            voteWrapper = itemView.findViewById(R.id.voteWrapper);
        }
    }
}
