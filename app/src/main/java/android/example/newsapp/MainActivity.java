package android.example.newsapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<NewsClass>> {

    // Tag for log messages
    private static final String LOG_TAG = MainActivity.class.getName();
    // loader ID
    private static final int EARTHQUAKE_LOADER_ID = 1;
    // Adapter for the list of earthquakes
    private CustomArrayAdapter adapter;
    // Empty TextView
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adapter = new CustomArrayAdapter(this, new ArrayList<NewsClass>());

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        // add click listener to the news items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current news that was clicked on
                NewsClass currentNews = adapter.getItem(position);

                // Convert the String URL into a URI
                Uri newsUri = Uri.parse(currentNews.getUrl());

                // Create a new intent to view the news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity if suitable app exist
                if (websiteIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(websiteIntent);
                }
            }
        });

        LoaderManager loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyStateTextView);
    }

    @NonNull
    @Override
    public Loader<List<NewsClass>> onCreateLoader(int id, @Nullable Bundle args) {
        return new NewsLoader(this, uriBuilder());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<NewsClass>> loader, List<NewsClass> data) {

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        //check for internet connection
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            //state that there is no internet connection
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        } else if (networkInfo != null && networkInfo.isConnected()) {
            //There is internet but list is still empty
            mEmptyStateTextView.setText(R.string.no_News);
        }

        adapter.clear();
        if (data != null && !data.isEmpty()) {
            adapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<NewsClass>> loader) {
        adapter.clear();
    }

    // uri builder method
    private String uriBuilder() {
        //Uri builder
        Uri.Builder builder = new Uri.Builder().scheme("https")
                .authority("content.guardianapis.com")
                .appendPath("search")
                .appendQueryParameter("show-tags", "contributor")
                .appendQueryParameter("api-key", "30bc138d-348d-4e2d-83f9-0cd4933448cd");
        return builder.build().toString();
    }

}