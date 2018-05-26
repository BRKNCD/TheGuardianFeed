package com.example.android.theguardianfeed;

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
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QueryUtils {
    //Log tag contains class name
    private static final String LOG_TAG = QueryUtils.class.getName();

    private QueryUtils() {
    }

    /**
     * Query the dataset and return a list of {@link NewsLoader} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;

        jsonResponse = makeHttpRequest(url);
        // Extract relevant fields from the JSON response and create a list of {@link News}s
        List<News> news = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Earthquake}s
        return news;
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



    /*
        Initiate internet connection and fetch JSON string
        from the Json data source URL
        */
    private static String makeHttpRequest(URL url) {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(5000);
            urlConnection.setConnectTimeout(1500);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
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

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<News> newsList = new ArrayList<>();

        String title;
        String section;
        String url;
        String firstName;
        String lastName;
        String author;
        String rawDate;
        String date;

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject newsObject = baseJsonResponse.getJSONObject("response");
            JSONArray newsArray = newsObject.getJSONArray("results");

            // For each earthquake in the newsArray, create an {@link Earthquake} object
            for (int i = 0; i < newsArray.length(); i++) {
                // Get a single earthquake at position i within the list of earthquakes
                JSONObject currentNews = newsArray.getJSONObject(i);
                url = currentNews.getString("webUrl");
                JSONArray tagsArray = currentNews.getJSONArray("tags");

                // Extract the value for the key called "title"
                title = currentNews.getString("webTitle");

                // Extract the value for the key called "section"
                section = currentNews.getString("sectionName");

                // Get firstName and lastName for creating author String

                if (!tagsArray.isNull(0)) {
                    JSONObject currentTagObj = tagsArray.getJSONObject(0);

                    //Check if firstName and store otherwise set it to null
                    if (!currentTagObj.isNull("firstName")) {
                        firstName = currentTagObj.getString("firstName");
                    } else {
                        firstName = null;
                    }

                    //Check lastName and store otherwise set it to null
                    if (!currentTagObj.isNull("lastName")) {
                        lastName = currentTagObj.getString("lastName");
                    } else {
                        lastName = null;
                    }

                    //Call method to store formatted Author name
                    author = getAuthorName(firstName, lastName);
                } else {
                    author = null;
                }

                // Extract the value for the key called "date"
                if (!currentNews.isNull("webPublicationDate")) {
                    rawDate = currentNews.getString("webPublicationDate");
                    date = getFormattedDate(rawDate); //Format raw date and store it in date
                } else {
                    date = null;
                }

                // Create a new {@link News} object with the title, section, author, date
                // and url from the JSON response.
                News news = new News(title, section, author, date, url);

                // Add the new {@link News} to the list of News.
                newsList.add(news);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        // Return the list of earthquakes
        return newsList;
    }


    //Format the raw date fetched from the JSON object and a user friendly date
    private static String getFormattedDate(String rawDate) {
        if (rawDate == null) {
            return null;
        }
        Date date = null;
        SimpleDateFormat formattedDate = new SimpleDateFormat("MMM dd, yyyy - HH:mm");
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(rawDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate.format(date);
    }

    /*
    Get the first name and last name if available and return the
    formatted available names, otherwise null
    */
    private static String getAuthorName(String firstName, String lastName) {
        if (firstName == null && lastName == null) {
            return null;
        } else if (firstName == null || firstName.isEmpty()) {
            return lastName;
        } else if (lastName == null || lastName.isEmpty()) {
            return firstName;
        } else {
            return (firstName + " " + lastName);
        }
    }
}