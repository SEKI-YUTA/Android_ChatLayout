package com.yuta.chatlayout;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TalkAdapter extends RecyclerView.Adapter<TalkAdapter.TalkViewHolder> {
    private Context context;
    private JSONArray data;
    private MessageAction listener;
    private SimpleDateFormat toDate = new SimpleDateFormat("yyyy-MM-dd_HH_mm");
    private SimpleDateFormat toDisplay = new SimpleDateFormat("HH:mm");

    public TalkAdapter(Context context, JSONArray data, MessageAction listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TalkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.vote_item, parent, false);
        return new TalkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TalkViewHolder holder, int position) {
        Date sendDate = null;
        String dateStr = null;
        String text = null;
        String imgUri = null;
        boolean isMe = true;
        try {
            JSONObject item = data.getJSONObject(holder.getAdapterPosition());
            dateStr = item.getString("time");
            sendDate = toDate.parse(dateStr);
            text = item.getString("text");
            imgUri = item.getString("image");
            isMe = item.getBoolean("isMe");
            Log.d("isMe", String.valueOf(isMe));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(sendDate != null) {
            holder.tv_time.setText(toDisplay.format(sendDate));
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
            ViewCompat.setTransitionName(holder.img_content, "img_content");
            final String uri = imgUri;
            holder.img_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.imgClicked(uri, holder.img_content);
                }
            });
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

    public interface MessageAction {
        void imgClicked(String url, View shareElement);
    }

    class TalkViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_text, tv_time;
        public ImageView img_content;
        public LinearLayout textWrapper, voteWrapper;
        public TalkViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_text = itemView.findViewById(R.id.tv_text);
            tv_time = itemView.findViewById(R.id.tv_time);
            img_content = itemView.findViewById(R.id.img_content);
            textWrapper = itemView.findViewById(R.id.textWrapper);
            voteWrapper = itemView.findViewById(R.id.voteWrapper);
        }
    }
}
