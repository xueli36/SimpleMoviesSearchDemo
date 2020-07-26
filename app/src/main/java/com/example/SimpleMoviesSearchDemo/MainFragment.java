package com.example.SimpleMoviesSearchDemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment {

    View view;

    TextView txtInputMovieSearch;
    Button btnSend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);
        txtInputMovieSearch = getActivity().findViewById(R.id.inputTextMovieSearch);
        btnSend = getActivity().findViewById(R.id.btnSearchMovies);

        // Get arguments passed to fragment from activity
        assert getArguments() != null;
        String txt = getArguments().getString("encrypted");

        return view;
    }

}