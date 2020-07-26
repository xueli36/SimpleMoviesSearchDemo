package com.example.SimpleMoviesSearchDemo;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class ConfigureFragment extends Fragment {

    View view;
    Button btnConfigure;

    String encrypted;

    SendMessage SM;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_configure, container, false);

        btnConfigure = view.findViewById(R.id.btnConfigure);

        btnConfigure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get data
                try {
                    // Get user input TMDB API Key
                    TextView queryText = getActivity().findViewById(R.id.APIKeyTextView);
                    final String inputQueryString = queryText.getText().toString();

                    // Encrypt TMDB API Key to be stored securely in the app
                    String key = "Bar12345Bar12345";

                    // Create key and cipher
                    Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
                    Cipher cipher = Cipher.getInstance("AES");

                    byte[] inputByte = inputQueryString.getBytes("UTF-8");

                    // Encrypt the text
                    cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                    encrypted = new String(Base64.encode(cipher.doFinal(inputByte), Base64.DEFAULT));

                    //SM.sendData(inputQueryString);
                    SM.sendData(encrypted);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public interface SendMessage {
        void sendData(String message);
    }

    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            SM = (SendMessage) getActivity();
        }
        catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }

}