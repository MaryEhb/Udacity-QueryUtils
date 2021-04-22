package android.example.newsapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomArrayAdapter extends ArrayAdapter<NewsClass> {
    public CustomArrayAdapter(Activity context, ArrayList<NewsClass> newsData) {
        super(context, 0, newsData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);

        NewsClass currentNews = getItem(position);

        // get title textview and set its text
        TextView titleTextview = listItemView.findViewById(R.id.title);
        titleTextview.setText(currentNews.getTitle());

        //get section textview and set its text
        TextView SectionTextview = listItemView.findViewById(R.id.section);
        SectionTextview.setText(currentNews.getSection());

        // get time textView and set its text to the time after formatting it if it exists
        String time = currentNews.getTime();
        TextView dateTextview = listItemView.findViewById(R.id.date);

        if (time != null) {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss a");

            try {
                Date date = input.parse(time);
                String formattedTime = output.format(date);
                dateTextview.setText(formattedTime);
            } catch (ParseException e) {
                e.printStackTrace();
                dateTextview.setVisibility(View.GONE);
            }
        } else {
            dateTextview.setVisibility(View.GONE);
        }

        // get author and set its text if it exists
        String author = currentNews.getAuthor();
        if (author != null) {
            TextView autherTextview = listItemView.findViewById(R.id.author);
            autherTextview.setText(author);
        }

        return listItemView;
    }

}
