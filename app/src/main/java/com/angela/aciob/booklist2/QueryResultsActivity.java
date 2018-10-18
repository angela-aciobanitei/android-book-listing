package com.angela.aciob.booklist2;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class QueryResultsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    // URL for books data from the Google books API
    private String REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    // Adapter for the list of book titles
    private BookAdapter mAdapter;

    // Constant value for the books loader ID
    private static final int BOOKS_LOADER_ID = 1;

    // Loading indicator
    private ProgressBar mProgressBar;

    // TextView that is displayed when the list is empty
    private TextView mEmptyStateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_results);

        // Find a reference to the {@link ListView} in the layout
        ListView booksListView = (ListView) findViewById(R.id.list_view);

        // Set the view that is displayed when the list is empty
        mEmptyStateView = (TextView) findViewById(R.id.empty_view);
        booksListView.setEmptyView(mEmptyStateView);

        // Initialize and set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        booksListView.setAdapter(mAdapter);

        // Find a reference to the {@link ProgressBar} in the layout
        mProgressBar = (ProgressBar) findViewById(R.id.loading_indicator);


        // Get the intent that launched this activity and the extra data sent along with it
        String searchText = getIntent().getStringExtra("topic");

        // Build the URL from user search text
        REQUEST_URL += searchText + "&maxResults=40";

        if(QueryUtils.isNetworkAvailable(QueryResultsActivity.this)) {
            // Kick off loader
            getLoaderManager().initLoader(
                    BOOKS_LOADER_ID,
                    null,
                    QueryResultsActivity.this);
        }
        else {
            // Otherwise, display error message to the user
            // First, hide loading indicator so error message will be visible
            mProgressBar.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateView.setText(R.string.no_internet_connection);
        }

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected book.
        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = null;
                if (currentBook != null) {
                    bookUri = Uri.parse(currentBook.getBookUrl());
                }

                // Create intent and launch a new activity
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                startActivity(websiteIntent);
            }
        });
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id The ID whose loader is to be created.
     * @param bundle Any arguments supplied by the caller.
     * @return  A new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle bundle) {
        return new BookLoader(QueryResultsActivity.this, REQUEST_URL);
    }

    /**
     * Called when the data has been loaded. Gets the desired information from
     * the JSON and updates the Views.
     *
     * @param loader The loader that has finished.
     * @param data The JSON response from the Books API.
     */
    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        // Hide progress bar because the data has been loaded
        mProgressBar.setVisibility(View.GONE);

        // Set empty state text to display "No books to display."
        mEmptyStateView.setText(R.string.no_books);

        // Clear the adapter of previous data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            mAdapter.addAll(data);
        }

    }

    /**
     * Called when a previously created loader is being reset, thus making its data unavailable.
     *
     * @param loader The loader that was reset.
     */
    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Clear the adapter of previous data
        mAdapter.clear();
    }
}
