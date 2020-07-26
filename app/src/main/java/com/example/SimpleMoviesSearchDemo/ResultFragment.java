package com.example.SimpleMoviesSearchDemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ResultFragment extends Fragment {

    View view;

    TextView txt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_result, container, false);

        // Get arguments passed to fragment from activity
        assert getArguments() != null;

        // Extract the data
        String[] movieTitle = getArguments().getStringArray("movieTitle");
        String[] imageUrl = getArguments().getStringArray("moviePoster");
        String[] movieOverview = getArguments().getStringArray("movieOverview");
        String[] movieGenre = getArguments().getStringArray("movieGenre");
        float[] moviePopularity = getArguments().getFloatArray("moviePopularity");
        int[] movieReleaseYear = getArguments().getIntArray("movieReleaseYear");

        // data to populate the RecyclerView with
        ArrayList<String> movieTitleArrayList = new ArrayList<>();
        ArrayList<String> movieImageArrayList = new ArrayList<>();
        ArrayList<String> movieOverviewArrayList = new ArrayList<>();
        ArrayList<String> movieGenreArrayList = new ArrayList<>();
        ArrayList<Float> moviePopularityArrayList = new ArrayList<>();
        ArrayList<Integer> movieReleaseYearArrayList = new ArrayList<>();

        // Convert String[] to Array List<String>
        for(int i = 0; i < movieTitle.length; i++) {
            movieTitleArrayList.add(movieTitle[i]);
            movieImageArrayList.add(imageUrl[i]);
            movieOverviewArrayList.add(movieOverview[i]);
            moviePopularityArrayList.add(moviePopularity[i]);
            movieReleaseYearArrayList.add(movieReleaseYear[i]);
            movieGenreArrayList.add(movieGenre[i]);
        }

        RecyclerView recyclerView = view.findViewById(R.id.my_recycle_view);

        //recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // Add dividers to recycler view
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        RecyclerView.Adapter mAdapter = new MyRecyclerViewAdapter(getActivity(), movieTitleArrayList, movieImageArrayList, movieOverviewArrayList, moviePopularityArrayList, movieReleaseYearArrayList, movieGenreArrayList);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    protected void displayReceivedData(String message)
    {
        txt.setText(message);
    }

}