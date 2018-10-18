package com.angela.aciob.booklist2;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText mSearchEditText;
    private ImageButton mSearchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the views from the layout
        mSearchEditText = findViewById(R.id.search_edit_text);
        mSearchButton = findViewById(R.id.search_button);

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the query string from the input field.
                String queryString = mSearchEditText.getText().toString()
                        .trim().replaceAll("\\s+","+");

                // Hide the keyboard when the button is pushed.
                hideKeyboard(MainActivity.this);

                if (!queryString.isEmpty()) {
                    // Build intent to go to the {@link QueryResultsActivity} activity
                    Intent results = new Intent(
                            MainActivity.this,
                            QueryResultsActivity.class);
                    results.putExtra("topic", queryString);
                    startActivity(results);

                } else {
                    // Notify user to enter text via toast
                    Toast.makeText(MainActivity.this,
                            getString(R.string.enter_text),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }



    public static void hideKeyboard(Activity activity) {
        // Get the activity's input method service
        InputMethodManager inputMethodManager =  (InputMethodManager)
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE);

        // Hide the soft keypad
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
