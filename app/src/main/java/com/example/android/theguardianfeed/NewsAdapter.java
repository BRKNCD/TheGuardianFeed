package com.example.android.theguardianfeed;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News>{

    public NewsAdapter(@NonNull Context context, List<News> News) {
        super(context,  0, News);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news_list_item, parent, false);
        }

        News currentNews = getItem(position);

        TextView titleView = listItemView.findViewById(R.id.title);
        String currentTile = currentNews.getTitle();
        titleView.setText(currentTile);

        TextView bodyView = listItemView.findViewById(R.id.body);
        String currentBody = currentNews.getBody();
        bodyView.setText(currentBody);

        TextView authorView = listItemView.findViewById(R.id.author);
        String currentAuthor = currentNews.getAuthor();
        authorView.setText(currentAuthor);

        return listItemView;
    }
}
