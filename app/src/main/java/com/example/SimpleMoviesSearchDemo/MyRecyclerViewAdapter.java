package com.example.SimpleMoviesSearchDemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.List;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private List<String> movieTitle;
    private List<String> moviePoster;
    private List<String> movieOverview;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<String> data, List<String>image, List<String>overview) {
        this.mInflater = LayoutInflater.from(context);
        this.movieTitle = data;
        this.moviePoster = image;
        this.movieOverview = overview;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycleview_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int index) {
        String individualMovieTitle = movieTitle.get(index);
        String individualMoviePoster = moviePoster.get(index);
        String individualMovieOverview = movieOverview.get(index);

        // Set movie title text
        holder.myTextView.setText(individualMovieTitle);

        // Get and set movie poster
        new DownloadImageTask(holder.myImageView)
              .execute(individualMoviePoster);

        // Set movie overview text
        holder.myOverviewView.setText(individualMovieOverview);

    }

    // total number of rows
    @Override
    public int getItemCount() {
        return movieTitle.size();
    }

    // stores and recycles views as they are scrolled off screen
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;
        ImageView myImageView;
        TextView myOverviewView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.movieTitle);
            myImageView = itemView.findViewById(R.id.moviePoster);
            myOverviewView = itemView.findViewById(R.id.movieOverview);
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return movieTitle.get(id);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        ImageView bmImage;

        DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap icon = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                icon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return icon;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}