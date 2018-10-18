package com.angela.aciob.booklist2;

/**
 * Data Model is a Book object with the following properties:
 *      title - title of the book
 *      authors - book authors
 *      rating - average rating of the book
 *      price - retail price of the book
 *      currency - currency code for the retail price
 *      image URL - URL address of an image cover of the book
 *      book URL - URL address of the book preview
 */

public class Book {

    private String mTitle;
    private String mAuthors;
    private double mRating;
    private double mPrice;
    private String mCurrency;
    private String mImageUrl;
    private String mBookUrl;

    // Constructor overloading

    public Book(String title, String authors, double rating, double price,
                String currency, String imageUrl, String url) {
        mTitle = title;
        mAuthors = authors;
        mRating = rating;
        mPrice = price;
        mCurrency = currency;
        mImageUrl = imageUrl;
        mBookUrl = url;
    }

    public Book(String title, String authors, double rating, double price,
                String currency, String url) {
        mTitle = title;
        mAuthors = authors;
        mRating = rating;
        mPrice = price;
        mCurrency = currency;
        mBookUrl = url;
    }


    public String getTitle() {
        return mTitle;
    }

    public String getAuthors() {
        return mAuthors;
    }

    public double getRating() {
        return mRating;
    }

    public double getPrice() {
        return mPrice;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getBookUrl() {
        return mBookUrl;
    }
}