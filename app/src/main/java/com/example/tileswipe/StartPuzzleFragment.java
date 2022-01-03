package com.example.tileswipe;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StartPuzzleFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartPuzzleFragment extends DialogFragment {
    public static final String TAG = "start-puzzle-fragment";

    protected TextView newGameTextView;
    protected TextView resumeGameTextView;
    public StartPuzzleFragment() {
        // Required empty public constructor
    }

    public static StartPuzzleFragment newInstance(String param1, String param2) {
        StartPuzzleFragment fragment = new StartPuzzleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.setCancelable(true);
        View inflatedView = inflater.inflate(R.layout.fragment_start_puzzle, container, false);

        newGameTextView = inflatedView.findViewById(R.id.puzzle_start_new_game_button);
        resumeGameTextView = inflatedView.findViewById(R.id.puzzle_start_resume_game_button);

        newGameTextView.setTextSize(25f);
        resumeGameTextView.setTextSize(25f);

        newGameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPuzzle(false);
            }
        });

        resumeGameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPuzzle(true);
            }
        });

        return inflatedView;
    }

    public void startPuzzle(boolean resumePreviousGame) {
        Intent intent = new Intent(getContext(), PuzzleActivity.class);
        intent.putExtra("resume-previous-game", resumePreviousGame);
        startActivity(intent);
    }
}