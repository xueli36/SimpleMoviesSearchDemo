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
import java.text.DecimalFormat;
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
            String[] resultPosterURLStringArr = new String[10];
            String[] resultOverviewStringArr = new String[10];
            Float[] resultPopularityFloatArr = new Float[10];
            int[] resultReleaseYearIntArr = new int[10];
            String[] resultGenreNameArr = new String[10];

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

                    if (stream != null) {

                        String urlInputStream = readStream(stream, 100000000);

                        // Cast input stream as JSON object
                        JSONObject reader = new JSONObject(urlInputStream);

                        // Get "results" as JSON Array
                        JSONArray resultsArray = reader.getJSONArray("results");

                        int numOfResultsInt = resultsArray.length();

                        for (int i = 0; i < numOfResultsInt; i++) {
                            // Get each result in JSON Array as JSON object
                            JSONObject individualResult = resultsArray.getJSONObject(i);

                            // Read each result data
                            String title = individualResult.getString("title");
                            String releaseDate = individualResult.getString("release_date");
                            String posterPath = individualResult.getString("poster_path");
                            String overview = individualResult.getString("overview");
                            Float popularity = (float) individualResult.getDouble("popularity");

                            // Set Float to 1 decimal point
                            DecimalFormat df = new DecimalFormat("0.0");

                            popularity = Float.parseFloat(df.format(popularity));

                            String baseImageUrl = "https://image.tmdb.org/t/p/w500";

                            String fullImageUrl = baseImageUrl + posterPath;

                            // Check for invalid or empty release date
                            if (releaseDate.length() != 0) {

                                // Manipulate date string to get year only
                                String releaseYear = releaseDate.substring(0, 4);

                                int releaseYearInt = Integer.parseInt(releaseYear);

                                // Filter by release year and maximum of 10 results
                                if ((releaseYearInt == 2017 || releaseYearInt == 2018) && resultSize < 10) {

                                    JSONArray genreArray = individualResult.getJSONArray("genre_ids");

                                    String genreNames = "";

                                    for (int j = 0; j < genreArray.length(); j++) {
                                        if(!genreNames.isEmpty())
                                        {
                                            genreNames += ", ";
                                        }
                                        genreNames += getGenre((int) genreArray.get(j));
                                    }

                                    resultTitleStringArr[resultSize] = title;
                                    resultPopularityFloatArr[resultSize] = popularity;
                                    resultPosterURLStringArr[resultSize] = fullImageUrl;
                                    resultOverviewStringArr[resultSize] = overview;
                                    resultReleaseYearIntArr[resultSize] = releaseYearInt;
                                    resultGenreNameArr[resultSize] = genreNames;

                                    resultSize++;
                                }
                            }
                        }
                    }

                    // Get order of popularity and change array order accordingly
                    // Rank by popularity
                    // Sort array in descending order
                    String[] sortedTitleStringArr = new String[resultSize];
                    String[] sortedPosterUrlStringArr = new String[resultSize];
                    String[] sortedOverviewStringArr = new String[resultSize];
                    String[] sortedGenreStringArr = new String[resultSize];
                    Float[] sortedPopularityFloatArray = new Float[resultSize];
                    int[] sortedReleaseYearIntArray = new int[resultSize];

                    // Only need to sort if more than 1 result
                    if (resultSize > 0) {

                        // Convert popularity string array to sort
                        for (int i = 0; i < resultSize; i++) {
                            sortedPopularityFloatArray[i] = resultPopularityFloatArr[i];
                        }

                        // Get popularity and display only top 10 movies with highest rating
                        // Sort array according to descending order
                        Arrays.sort(sortedPopularityFloatArray, Collections.reverseOrder());
                    }

                    for (int i = 0; i < resultSize; i++) {
                        for (int j = 0; j < resultSize; j++) {
                            if (resultPopularityFloatArr[j].equals(sortedPopularityFloatArray[i])) {
                                sortedTitleStringArr[i] = resultTitleStringArr[j];
                                sortedPosterUrlStringArr[i] = resultPosterURLStringArr[j];
                                sortedOverviewStringArr[i] = resultOverviewStringArr[j];
                                sortedReleaseYearIntArray[i] = resultReleaseYearIntArr[j];
                                sortedGenreStringArr[i] = resultGenreNameArr[j];
                            }
                        }
                    }

                    // Set sorted array results if sorted
                    if (resultSize > 0) {
                        // Convert Float [] array to float[] array
                        float sortedPopularityfloatArray[] = new float[resultSize];

                        for (int i = 0; i < resultSize; i++) {
                            sortedPopularityfloatArray[i] = sortedPopularityFloatArray[i];
                        }

                        returnCombinedData.setMovieTitleArray(sortedTitleStringArr);
                        returnCombinedData.setMoviePosterArray(sortedPosterUrlStringArr);
                        returnCombinedData.setMovieOverviewArray(sortedOverviewStringArr);
                        returnCombinedData.setMoviePopularityArray(sortedPopularityfloatArray);
                        returnCombinedData.setMovieReleaseYearArray(sortedReleaseYearIntArray);
                        returnCombinedData.setMovieGenreArray(sortedGenreStringArr);
                        returnCombinedData.setDataEntered(true);
                    }
                } else {
                    popupWarning("Failed connection",
                            "Unable to connect (" + responseCode + ")");
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return returnCombinedData;
        }

        // Displays the result of the AsyncTask.
        // The String result is passed from doInBackground().
        @Override
        protected void onPostExecute(MovieData data) {

            if (data.getDataEntered()) {
                Intent intent = new Intent(MainActivity.this, ResultActivity.class);

                // Create the bundle
                Bundle bundle = new Bundle();

                // Add data to bundle
                bundle.putStringArray("movieTitle", data.getMovieTitlesArray());
                bundle.putStringArray("moviePoster", data.getMoviePosterArray());
                bundle.putStringArray("movieOverview", data.getMovieOverviewArray());
                bundle.putFloatArray("moviePopularity", data.getMoviePopularityArray());
                bundle.putIntArray("movieReleaseYear", data.getMovieReleaseYearArray());
                bundle.putStringArray("movieGenre", data.getMovieGenreArray());

                // Add the bundle to the intent
                intent.putExtras(bundle);

                startActivity(intent);
            } else {
                popupWarning("No result",
                        "No result. Please enter another search.");
            }
        }

        String getGenre(int genreID) {

            String genreName = "";

            if (genreID == 28) {

                genreName = "Action";
            }
            else if (genreID == 12) {
                genreName = "Comedy";
            }
            else if (genreID == 16) {
                genreName = "Animation";
            }
            else if (genreID == 35) {
                genreName = "Comedy";
            }
            else if (genreID == 80){
                genreName = "Crime";
            }
            else if (genreID == 99) {
                genreName = "Documentary";
            }
            else if (genreID == 18) {
                genreName = "Drama";
            }
            else if (genreID == 10751) {
                genreName = "Family";
            }
            else if (genreID == 14) {
                genreName = "Fantasy";
            }
            else if (genreID == 36) {
                genreName = "History";
            }
            else if (genreID == 27) {
                genreName = "Horror";
            }
            else if (genreID == 10402) {
                genreName = "Music";
            }
            else if (genreID == 9648) {
                genreName = "Mystery";
            }
            else if (genreID == 10749) {
                genreName = "Romance";
            }
            else if (genreID == 878) {
                genreName = "Science Fiction";
            }
            else if (genreID == 10770) {
                genreName = "TV Movie";
            }
            else if (genreID == 53) {
                genreName = "Thriller";
            }
            else if (genreID == 10752) {
                genreName = "War";
            }
            else if (genreID == 37) {
                    genreName = "Western";
            }

            return genreName;
        }
    }
}
