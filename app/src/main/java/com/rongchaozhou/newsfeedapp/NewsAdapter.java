package com.rongchaozhou.newsfeedapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    private static final String TIME_SEPARATOR = "[TZ]";

    public NewsAdapter(Context context, List<News> newsList) {
        super(context, 0, newsList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }
        News currentNews = getItem(position);

        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title);
        titleTextView.setText(currentNews.getTitle());

        String rawDate = currentNews.getDate();
        String[] parts = rawDate.split(TIME_SEPARATOR);

        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date);
        dateTextView.setText(parts[0].substring(6));

        TextView timeTextView = (TextView) listItemView.findViewById(R.id.time);
        timeTextView.setText(parts[1].substring(0, 5));

        TextView authorTextView = (TextView) listItemView.findViewById(R.id.author);
        authorTextView.setText(currentNews.getAuthor());

        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.section);
        sectionTextView.setText(currentNews.getSection());

        return listItemView;
    }
}
