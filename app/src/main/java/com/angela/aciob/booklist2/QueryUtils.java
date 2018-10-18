package com.angela.aciob.booklist2;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for using the Google Book Search API to download book information.
 */

public final class QueryUtils {

    // Tag for the log messages
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    // Prevent accidental instantiation of this class
    private QueryUtils() {
    }

    /**
     * Query the search String and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookData(String queryString) {
        // Create URL object
        URL url = createUrl(queryString);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Books}s
        List<Book> books = extractDataFromJson(jsonResponse);

        // Return the list of {@link Book}s
        return books;
    }



    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            // The constructor URL(String spec)
            // Creates a URL object from the String representation.
            // Throws MalformedURLException - if no protocol is specified,
            // or an unknown protocol is found, or spec is null.
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }



    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        // Initialize json response
        String jsonResponse = "";

        // If URL is null, we shouldn't try to make an HTTP request, return early
        if (url == null)
            return jsonResponse;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            // Call the URL object's openConnection method to get a URLConnection object
            // then cast it to a HttpURLConnection type
            urlConnection = (HttpURLConnection) url.openConnection();
            // Use this object to setup parameters and general request properties
            // that you may need before connecting
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            // Initiate the connection
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }



    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {

        // StringBuilder is a  mutable sequence of characters.
        // We'll use its append() method to build the output result.
        StringBuilder output = new StringBuilder();

        // InputStream represents an input stream of bytes (small chunks of data).
        if (inputStream != null) {
            // InputStreamReader is a bridge from byte streams to character streams.
            // The constructor InputStreamReader(InputStream in, Charset cs)
            // creates an InputStreamReader that uses the given charset.
            // The static method  Charset.forName(String charsetName)
            // returns a charset object for the named charset.
            InputStreamReader inputStreamReader = new
                    InputStreamReader(inputStream, Charset.forName("UTF-8"));

            // BufferedReader wrapper helps reading text for character-input stream
            // The constructor BufferedReader(Reader in) creates a
            // buffering character-input stream that uses a default-sized input buffer.
            BufferedReader reader = new BufferedReader(inputStreamReader);

            // Read one line of text at a time and append it to the output result
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        // Convert the mutable StringBuilder to an immutable String and return it
        return output.toString();
    }



    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Book> extractDataFromJson(String booksJSON) {
        // Return early if no data was returned from the HTTP request
        if (TextUtils.isEmpty(booksJSON)) {
            return null;
        }

        // Initialize list of strings to hold the extracted books
        List<Book> bookList = new ArrayList<>();

        // Traverse the raw JSON response parameter and extract relevant information
        try {
            // Create JSON object from response
            JSONObject baseJsonResponse = new JSONObject(booksJSON);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of books.
            JSONArray booksJsonArray = baseJsonResponse.getJSONArray("items");

            // For each book in the booksJsonArray, create an {@link Book} object
            for (int i = 0; i < booksJsonArray.length(); i++) {
                // Get the current book
                JSONObject book = booksJsonArray.getJSONObject(i);

                // Get the current book's volume info
                // It contains information about the title, authors, publisher, description
                // page count, dimensions, print type, average rating, image links, info links
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                // Get the book's title from the volume information
                String title = volumeInfo.getString("title");

                // Extract information on authors of the book
                StringBuilder authors = new StringBuilder();
                if (volumeInfo.has("authors")) {
                    JSONArray jsonAuthors = volumeInfo.getJSONArray("authors");
                    for (int j = 0; j < jsonAuthors.length(); j++) {
                        authors.append(jsonAuthors.getString(j)).append("\n");
                    }
                }

                // Get the average rating of the book from the JSON response
                double rating = 0f;
                if (volumeInfo.has("averageRating")) {
                    rating = volumeInfo.getDouble("averageRating");
                }

                // Get the current book's sale info
                // It contains information about the country, saleability, list price,
                // retail price or if the book is available as e-book
                JSONObject saleInfo = book.getJSONObject("saleInfo");
                String saleability = saleInfo.getString("saleability");
                boolean isForSale = saleability.equals("FOR_SALE");
                double price = 0f;
                String currencyCode = "";
                // Extract sale price and currency code only when book is available for sale
                if (isForSale) {
                    JSONObject retailPrice = saleInfo.getJSONObject("retailPrice");
                    price = retailPrice.getDouble("amount");
                    currencyCode = retailPrice.getString("currencyCode");
                }

                // Get image resource URL from image links
                JSONObject imageLinks;
                String coverImageUrl = "";
                if (volumeInfo.has("imageLinks")) {
                    imageLinks = volumeInfo.getJSONObject("imageLinks");
                    coverImageUrl = imageLinks.getString("smallThumbnail");
                }

                // Get book URL
                String previewLink = volumeInfo.getString("previewLink");

                // Add book to the list
                if(coverImageUrl.isEmpty())
                    bookList.add(new Book(title, authors.toString(), rating,
                            price, currencyCode, previewLink));
                else
                    bookList.add(new Book(title, authors.toString(), rating,
                        price, currencyCode, coverImageUrl, previewLink));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the google books JSON results", e);
        }

        // Return the successfully parsed book titles as a {@link List} object
        return bookList;
    }


    public static boolean isNetworkAvailable(Activity activity) {
        // ConnectivityManager is a class that answers queries about the state of network
        // connectivity. It also notifies applications when network connectivity changes.
        ConnectivityManager manager = (ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Instantiate a NetworkInfo object. It describes the status of a network interface.
        // Use getActiveNetworkInfo() to get an instance that represents the current network
        // connection. Requires the ACCESS_NETWORK_STATE permission.
        NetworkInfo networkInfo = null;
        if (manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }

        boolean isAvailable = false;
        // If network is present and connected to the web
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }
}
