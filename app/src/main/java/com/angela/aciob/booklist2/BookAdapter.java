package com.angela.aciob.booklist2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * {@link BookAdapter} is a custom adapter that can provide the layout for each list item
 * based on a data source, which is a list of {@link Book} objects.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    // Tag for the log messages
    private final String LOG_TAG = BookAdapter.class.getSimpleName();

    /**
     * Constructs a new {@link BookAdapter}. This adapter needs to know
     * the context and the data to adapt, a list of Book objects.
     *
     * @param context of the app
     * @param books is the list of books, which is the data source of the adapter.
     */
    public BookAdapter(Context context, List<Book> books) {
        super(context,0,books);
    }

    /**
     * Using the ViewHolder pattern. The ViewHolder design pattern enables us
     * to access each list item view without the need for the look up, saving
     * valuable processor cycles. Specifically, it avoids frequent call of findViewById(),
     * making ListView scrolling smooth.
     */
    private static class ViewHolder {

        ImageView bookCover;
        TextView bookTitle;
        TextView bookAuthors;
        RatingBar bookRating;
        TextView bookPrice;
        TextView bookCurrency;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;
        if(convertView == null) {
            // Inflate a new view.
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_item,
                    parent,
                    false);

            // Populate holder
            holder = new ViewHolder();
            holder.bookCover = convertView.findViewById(R.id.book_cover);
            holder.bookTitle = convertView.findViewById(R.id.book_title);
            holder.bookAuthors = convertView.findViewById(R.id.book_authors);
            holder.bookRating = convertView.findViewById(R.id.book_rating);
            holder.bookPrice = convertView.findViewById(R.id.book_price);
            holder.bookCurrency = convertView.findViewById(R.id.book_currency);

            // Set tag on the view to be recycled
            convertView.setTag(holder);
        }
        else {
            // Reuse view
            holder = (ViewHolder) convertView.getTag();
        }

        // Set the data for our list.
        // Get the current book that is being requested for display
        Book currentBook = getItem(position);

        // Set the book title to the correct view
        holder.bookTitle.setText(currentBook.getTitle());

        // Set the author of the book to the correct view
        try {
            String authors = currentBook.getAuthors();
            if (!authors.isEmpty()) {
                holder.bookAuthors.setText(authors);
            }
        } catch (NullPointerException e) {
            // Author information is not available from the JSON response
            Log.v(LOG_TAG, "No information on authors");

            // Hide view from book
            holder.bookAuthors.setVisibility(View.INVISIBLE);
        }

        // Set the rating for the book
        holder.bookRating.setRating((float) currentBook.getRating());

        // Set price and currency code for the book
        if (currentBook.getPrice() > 0) {
            holder.bookPrice.setText(String.valueOf(currentBook.getPrice()));
            holder.bookCurrency.setText(currentBook.getCurrency());
        }

        // Set cover image
        Picasso.with(getContext())
                .load(currentBook.getImageUrl())
                //.placeholder(R.drawable.book)
                .into(holder.bookCover);

        return convertView;
    }

}
