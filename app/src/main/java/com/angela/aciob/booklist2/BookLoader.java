package com.angela.aciob.booklist2;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * AsyncTaskLoader implementation that opens a network connection and
 * query the Book API.
 */
public class BookLoader extends AsyncTaskLoader<List<Book>> {

    // The url to query the API
    private String mSearchUrl;

    // Data from the API
    private List<Book> mData;

    /**
     * Create a loader object
     *
     * @param context the {@link Context} of the application
     * @param url {@link String} search query
     */
    public BookLoader(Context context, String url) {
        super(context);
        mSearchUrl = url;
    }


    @Override
    public void onStartLoading() {
        if (mData != null) {
            // Use cached data
            deliverResult(mData);
        } else {
            forceLoad();
        }
    }


    @Override
    public List<Book> loadInBackground() {
        // Sanity check
        if (mSearchUrl == null) {
            return null;
        }

        // Returns the list of books matching search criteria from Google books
        // after performing network request, parsing input stream, and extracting a list of books
        return QueryUtils.fetchBookData(mSearchUrl);
    }

    @Override
    public void deliverResult(List<Book> data) {
        // Cache data
        mData = data;
        super.deliverResult(data);
    }
}
