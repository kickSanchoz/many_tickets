package com.example.manytickets.ui.home;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.manytickets.ActorsOrDirectorsPackage.AllActorsOrDirectors;
import com.example.manytickets.MoviesPackage.AllMovies;
import com.example.manytickets.R;
import com.example.manytickets.SecondActivity;

import java.sql.Connection;
import java.sql.DriverManager;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        Button first_button = root.findViewById(R.id.first_button);
        first_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSecondPage();
            }
        });

        Button btn_allMovies = root.findViewById(R.id.btn_allMovies);
        TextView home_moviesText = root.findViewById(R.id.home_moviesText);
        home_moviesText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAllMoviesActivity();
            }
        });

        Button btn_allActors = root.findViewById(R.id.btn_allActors);
        TextView home_actorsText = root.findViewById(R.id.home_actorsText);
        home_actorsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAllActorsOrDirectorsActivity("Actors");
            }
        });

        Button btn_allDirectors = root.findViewById(R.id.btn_allDirectors);
        TextView home_directorsText = root.findViewById(R.id.home_directorsText);
        home_directorsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAllActorsOrDirectorsActivity("Directors");
            }
        });


        return root;
    }

    public void openSecondPage(){
        Intent intent = new Intent(getActivity(), SecondActivity.class);
        startActivity(intent);
    }

    public void openAllMoviesActivity(){
        Intent intent = new Intent(getActivity(), AllMovies.class);
        startActivity(intent);
    }

    public void openAllActorsOrDirectorsActivity(String actorsOrDirectors){
        Intent intent = new Intent(getActivity(), AllActorsOrDirectors.class);
        intent.putExtra("actorOrDirector", actorsOrDirectors);
        startActivity(intent);
    }
}