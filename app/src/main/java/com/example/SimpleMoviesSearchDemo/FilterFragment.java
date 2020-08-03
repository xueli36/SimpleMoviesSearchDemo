package com.example.SimpleMoviesSearchDemo;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.fragment.app.Fragment;

public class FilterFragment extends Fragment {

    View view;
    CheckBox chkBoxPopularity;
    CheckBox chkBoxAdult;

    boolean sortByPopularity = false;
    boolean sortByAdult = false;

    SendFilterData SFD;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_filter, container, false);

        chkBoxPopularity = view.findViewById(R.id.checkBoxPopularity);
        chkBoxAdult = view.findViewById(R.id.checkBoxAdult);

        chkBoxPopularity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                sortByPopularity = isChecked;
                SFD.sendFilterInfo(sortByPopularity, sortByAdult);
            }
        });

        chkBoxAdult.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                sortByAdult = isChecked;
                SFD.sendFilterInfo(sortByPopularity, sortByAdult);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public interface SendFilterData {
        void sendFilterInfo(boolean filterByPopularity, boolean filterByAdult);
    }

    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            SFD = (SendFilterData) getActivity();
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

}