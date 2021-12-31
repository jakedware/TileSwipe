package com.example.tileswipe;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PuzzleSolvedDialogFragment extends DialogFragment {
    public static final String TAG = "puzzle_solved_dialog_fragment";


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setView(R.layout.puzzle_solved_dialog);
        return builder.create();
    }
    
}