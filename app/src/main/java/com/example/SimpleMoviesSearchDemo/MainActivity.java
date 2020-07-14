package com.example.SimpleMoviesSearchDemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.util.Arrays;
import java.util.Collections;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    TextView txtInputMovieSearch;
    Button btnSend;
    Button btnAbout;
    Button btnConfigureAPI;

    static String APIKeyStr = "";

    public void popupWarning(String title, String message)
    {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setPositiveButton(android.R.string.yes, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtInputMovieSearch = findViewById(R.id.inputTextMovieSearch);
        btnSend = findViewById(R.id.btnSearchMovies);
        btnAbout = findViewById(R.id.btnAbout);
        btnConfigureAPI = findViewById(R.id.btnConfigureAPI);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(APIKeyStr.length() == 0)
                {
                    popupWarning("Configure API Key",
                            "API Key have not been configured. Click on Configure API Key button to configure.");
                }
                else
                {
                    // Get data
                    String inputQueryString = txtInputMovieSearch.getText().toString();

                    // Check for valid input
                    if(inputQueryString.length() != 0)
                    {
                        // Use api search function to search for movies
                        String baseUrlString = "https://api.themoviedb.org/3/search/movie?";
                        String apiKey = "api_key=" + APIKeyStr;
                        String fullQueryString = "&query=";

                        // Add received movie text search to query string
                        fullQueryString += inputQueryString;

                        String fullUrlString = baseUrlString + apiKey + fullQueryString;

                        // Send HTTP request
                        new HttpTask().execute(fullUrlString);
                    }
                    else
                    {
                        popupWarning("Invalid search",
                                "Search empty. Please enter a search.");
                    }
                }
            }
        });

        btnAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start about activity
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        btnConfigureAPI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start Configuration activity
                Intent intent = new Intent(MainActivity.this, ConfigurationActivity.class);
                startActivityForResult(intent, 1);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String strEncryptedAPIKey = data.getStringExtra("EncryptedAPIKey");
                String strEncryptionKey = data.getStringExtra("EncryptionKey");

                // Create key and cipher
                assert strEncryptionKey != null;
                Key aesKey = new SecretKeySpec(strEncryptionKey.getBytes(), "AES");
                Cipher cipher;
                try {
                    assert strEncryptedAPIKey != null;
                    byte[] inputByte = strEncryptedAPIKey.getBytes("UTF-8");

                    cipher = Cipher.getInstance("AES");

                    // decrypt the text
                    cipher.init(Cipher.DECRYPT_MODE, aesKey);

                    APIKeyStr = new String(cipher.doFinal(Base64.decode(inputByte, Base64.DEFAULT)));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Converts the contents of an InputStream to a String.
    public String readStream(InputStream stream, int maxReadSize)
            throws IOException {
        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] rawBuffer = new char[maxReadSize];
        int readSize;
        StringBuffer buffer = new StringBuffer();
        while (((readSize = reader.read(rawBuffer)) != -1) && maxReadSize > 0) {
            if (readSize > maxReadSize) {
                readSize = maxReadSize;
            }
            buffer.append(rawBuffer, 0, readSize);
            maxReadSize -= readSize;
        }
        return buffer.toString();
    }

    // Run the HTTP request in a background thread, separating from the main UI thread
    private class HttpTask extends AsyncTask<String, Void, MovieData> {
        @Override
        protected MovieData doInBackground(String... strURLs) {

            MovieData returnCombinedData = new MovieData();

            String[] resultTitleStringArr = new String[10];
            int[] resultPopularityIntArr = new int[10];
            String[] resultPosterURLStringArr = new String[10];
            String[] resultOverviewStringArr = new String[10];

            // Keep track of number of valid results
            int resultSize = 0;

            URL url;
            HttpURLConnection conn;

            try {
                url = new URL(strURLs[0]);
                conn = (HttpURLConnection) url.openConnection();

                // Get the HTTP response
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    // Get results
                    InputStream stream = conn.getInputStream();

                    if(stream != null) {

                        String urlInputStream = readStream(stream, 100000000);

                        // Cast input stream as JSON object
                        JSONObject reader = new JSONObject(urlInputStream);

                        // Get "results" as JSON Array
                        JSONArray resultsArray = reader.getJSONArray("results");

                        int numOfResultsInt = resultsArray.length();

                        for(int i = 0; i < numOfResultsInt; i++)
                        {
                            // Get each result in JSON Array as JSON object
                            JSONObject individualResult = resultsArray.getJSONObject(i);

                            // Read each result data
                            String title = individualResult.getString("title");
                            String releaseDate = individualResult.getString("release_date");
                            String posterPath = individualResult.getString("poster_path");
                            String overview = individualResult.getString("overview");

                            String baseImageUrl = "https://image.tmdb.org/t/p/w500";

                            String fullImageUrl = baseImageUrl + posterPath;

                            // Get popularity and display only top 10 movies with highest rating
                            int popularity = (int)Double.parseDouble(individualResult.getString("popularity"));

                            // Check for invalid or empty release date
                            if(releaseDate.length() != 0) {

                                // Manipulate date string to get year only
                                String releaseYear = releaseDate.substring(0,4);

                                int releaseYearInt = Integer.parseInt(releaseYear);

                                // Filter by release year and maximum of 10 results
                                if( (releaseYearInt == 2017 || releaseYearInt == 2018) && resultSize < 10)
                                {
                                    resultTitleStringArr[resultSize] = title;
                                    resultPopularityIntArr[resultSize] = popularity;
                                    resultPosterURLStringArr[resultSize] = fullImageUrl;
                                    resultOverviewStringArr[resultSize] = overview;

                                    resultSize++;
                                }
                            }
                        }
                    }

                    // Get order of popularity and change array order accordingly
                    // Rank by popularity
                    // Sort array in descending order
                    //TODO: Sort with doubles not int as will lose precision and give wrong result
                    Integer[] popularityIntegerArray = new Integer[resultSize];
                    String[] sortedResultString = new String [resultSize];
                    String[] sortedImageUrlString = new String [resultSize];
                    String[] sortedOverviewArray = new String [resultSize];

                    // Only need to sort if more than 1 result
                    if(resultSize > 0) {

                        // Convert int array to Integer array
                        for(int i=0; i<resultSize; i++)
                        {
                            popularityIntegerArray[i] = resultPopularityIntArr[i];
                        }

                        // Sort integer array according to descending order
                        Arrays.sort(popularityIntegerArray, Collections.reverseOrder());

                        for(int i=0; i<resultSize; i++)
                        {
                            for(int j=0; j<resultPopularityIntArr.length; j++)
                            {
                                if(resultPopularityIntArr[j] == popularityIntegerArray[i])
                                {
                                    sortedResultString[i] = resultTitleStringArr[j];
                                    sortedImageUrlString[i] = resultPosterURLStringArr[j];
                                    sortedOverviewArray[i] = resultOverviewStringArr[j];
                                }
                            }
                        }
                    }

                    // Set sorted array results if sorted
                    if(resultSize >= 2)
                    {
                        returnCombinedData.setMovieTitleArray(sortedResultString);
                        returnCombinedData.setMoviePosterArray(sortedImageUrlString);
                        returnCombinedData.setMovieOverviewArray(sortedOverviewArray);
                        returnCombinedData.setDataEntered(true);
                    }
                    // Set result array results if not sorted
                    else if(resultSize == 1)
                    {
                        returnCombinedData.setMovieTitleArray(resultTitleStringArr);
                        returnCombinedData.setMoviePosterArray(resultPosterURLStringArr);
                        returnCombinedData.setMovieOverviewArray(resultOverviewStringArr);
                        returnCombinedData.setDataEntered(true);
                    }
                }
                else {
                    popupWarning("Failed connection",
                            "Unable to connect (" + responseCode + ")");
                }
            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return returnCombinedData;
        }

        // Displays the result of the AsyncTask.
        // The String result is passed from doInBackground().
        @Override
        protected void onPostExecute(MovieData data) {

            if(data.getDataEntered())
            {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);

                // Create the bundle
                Bundle bundle = new Bundle();

                // Add data to bundle
                bundle.putStringArray("movieTitle", data.getMovieTitlesrray());
                bundle.putStringArray("moviePoster", data.getMoviePosterArray());
                bundle.putStringArray("movieOverview", data.getMovieOverviewArray());

                // Add the bundle to the intent
                intent.putExtras(bundle);

                startActivity(intent);
            }
            else
            {
                popupWarning("No result",
                        "No result. Please enter another search.");
            }
        }
    }
}