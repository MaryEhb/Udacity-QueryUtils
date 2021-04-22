package android.example.newsapp;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<NewsClass>> {

    private static final String LOG_TAG = NewsLoader.class.getName();
    private final String url;

    public NewsLoader(Context context, String aUrl) {
        super(context);
        url = aUrl;
    }

    // it didn't work till i added this method
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    // get data in background
    @Override
    public List<NewsClass> loadInBackground() {
        if (url == null) {
            return null;
        }
        List<NewsClass> newsList = QueryUtils.fetchNewsData(url);
        return newsList;
    }
}
