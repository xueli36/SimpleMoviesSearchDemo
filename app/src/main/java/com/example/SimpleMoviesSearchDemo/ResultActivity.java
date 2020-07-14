package com.example.SimpleMoviesSearchDemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Get the bundle
        Bundle bundle = getIntent().getExtras();

        // Extract the data
        String[] movieTitle = bundle.getStringArray("movieTitle");
        String[] imageUrl = bundle.getStringArray("moviePoster");
        String[] movieOverview = bundle.getStringArray("movieOverview");

        // data to populate the RecyclerView with
        ArrayList<String> movieTitleArrayList = new ArrayList<>();
        ArrayList<String> movieImageArrayList = new ArrayList<>();
        ArrayList<String> movieOverviewArrayList = new ArrayList<>();

        // Convert String[] to Array List<String>
        for(int i = 0; i < movieTitle.length; i++) {
            movieTitleArrayList.add(movieTitle[i]);
            movieImageArrayList.add(imageUrl[i]);
            movieOverviewArrayList.add(movieOverview[i]);
        }
        recyclerView = findViewById(R.id.my_recycle_view);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Add dividers to recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new MyRecyclerViewAdapter(this, movieTitleArrayList, movieImageArrayList, movieOverviewArrayList);
        recyclerView.setAdapter(mAdapter);
    }
}
