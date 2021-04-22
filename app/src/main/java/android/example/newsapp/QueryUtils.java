package android.example.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    // Tag for log messages
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    // make url from giving string
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    // make HTTP request for the given url and return string json response
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200), then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // convert inputStream to string
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    // return a list of news data from a given json string
    private static List<NewsClass> extractFeatureFromJson(String newsJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news data to
        List<NewsClass> newsList = new ArrayList<>();

        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // get result JSON array
            JSONArray resultsJSONArray = baseJsonResponse.getJSONObject("response").getJSONArray("results");

            // For each news data in the resultsArray, create an news object
            for (int i = 0; i < resultsJSONArray.length(); i++) {

                // Get a single news data at position i within the list of news
                JSONObject currentNews = resultsJSONArray.getJSONObject(i);

                // extract news details:
                String title = currentNews.getString("webTitle");
                String section = currentNews.getString("sectionName");
                String url = currentNews.getString("webUrl");
                String author = null;
                String time = null;
                JSONObject tags1stObj = currentNews.getJSONArray("tags").getJSONObject(0);
                if(tags1stObj.has("firstName")){
                    author = tags1stObj.getString("firstName");
                }

                if(tags1stObj.has("lastName")){
                    author += " " + tags1stObj.getString("lastName");
                }

                if(currentNews.has("webPublicationDate")){
                    time = currentNews.getString("webPublicationDate");
                }

                // make news object from details
                NewsClass news = new NewsClass(title, section, author, time, url);
                // Add the news data to the news list
                newsList.add(news);
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return newsList;
    }

     //Query the USGS data set and return a list of news objects.
    public static List<NewsClass> fetchNewsData(String requestUrl) {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of news
        List<NewsClass> newsList = extractFeatureFromJson(jsonResponse);

        // Return the list of news
        return newsList;
    }
}
