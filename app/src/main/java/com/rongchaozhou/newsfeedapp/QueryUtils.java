package com.rongchaozhou.newsfeedapp;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.*;




import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * Helper methods related to requesting and receiving news data from guardianAPI.
 */
public final class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static final int READ_TIMEOUT = 10000;
    private static final int CONNECT_TIMEOUT = 15000;
    private static final String REQUEST_METHOD = "GET";
    private static final int SUCCESSFUL_RESPONSE_CODE = 200;


    private static final String KEY_JSON_RESPONSE = "response";
    private static final String KEY_JSON_RESULTS = "results";
    private static final String KEY_TITLE = "webTitle";
    private static final String KEY_DATE = "webPublicationDate";
    private static final String KEY_AUTHOR_FIELD = "fields";
    private static final String KEY_AUTHOR = "byline";
    private static final String KEY_SECTION = "sectionName";
    private static final String KEY_URL = "webUrl";
    private static final String DEFAULT_AUTHOR_NAME = "Unknown";

    private QueryUtils() {
    }

    /**
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    public static List<News> fetchNewsData(String requestUrl) {
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        return extractFeatureFromJson(jsonResponse);
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT /* milliseconds */);
            urlConnection.setRequestMethod(REQUEST_METHOD);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == SUCCESSFUL_RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the News JSON results.", e);
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

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List<News> extractFeatureFromJson(String newsJSON) {
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        List<News> newsList = new ArrayList<>();
        try {
            JsonElement jElement = new JsonParser().parse(newsJSON);
            JsonObject baseJsonResponse = jElement.getAsJsonObject();
            JsonObject responseObject = baseJsonResponse.getAsJsonObject(KEY_JSON_RESPONSE);
            JsonArray newsArray = responseObject.getAsJsonArray(KEY_JSON_RESULTS);

            for (int i = 0; i < newsArray.size(); i++) {
                JsonObject currentNews = newsArray.get(i).getAsJsonObject();
                String title = currentNews.get(KEY_TITLE).getAsString();
                String date = currentNews.get(KEY_DATE).getAsString();
                Optional<JsonObject> authorField = Optional.ofNullable(currentNews.getAsJsonObject(KEY_AUTHOR_FIELD));
                String author =  authorField.isPresent() ? authorField.get().get(KEY_AUTHOR).getAsString() : DEFAULT_AUTHOR_NAME;

                String section = currentNews.get(KEY_SECTION).getAsString();
                String url = currentNews.get(KEY_URL).getAsString();
                News news = new News(title, date, author, section, url);
                newsList.add(news);
            }
        } catch (JsonIOException e) {
            Log.e("QueryUtils", "Problem parsing the News JSON results", e);
        }
        return newsList;
    }
}
