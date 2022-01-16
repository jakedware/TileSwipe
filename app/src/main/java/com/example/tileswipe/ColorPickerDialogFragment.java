package com.example.tileswipe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.slider.RangeSlider;

import org.w3c.dom.Text;

public class ColorPickerDialogFragment extends DialogFragment {
    public interface ColorPickerListener {
        public void onARGBChange(int[] argb, String buttonTag);
        public void onConfirm(int[] argb, String buttonTag);
    }

    AlertDialog dialog;
    TextView color;
    int a = 255;
    int r = 255;
    int g = 255;
    int b = 255;

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        View view = requireActivity().getLayoutInflater().inflate(R.layout.fragment_color_picker_dialog, null);

        color = view.findViewById(R.id.color_picker_dialog_color_text_view);
        color.setBackgroundColor(Color.argb(a,r,g,b));

        TextView aTextView = view.findViewById(R.id.a_text_view);
        aTextView.setText("a: " + a);
        TextView rTextView = view.findViewById(R.id.r_text_view);
        rTextView.setText("r: " + r);
        TextView gTextView = view.findViewById(R.id.g_text_view);
        gTextView.setText("g: " + g);
        TextView bTextView = view.findViewById(R.id.b_text_view);
        bTextView.setText("b: " + b);

        SeekBar aSeekBar = view.findViewById(R.id.a_seek_bar);
        aSeekBar.setProgress(a);
        SeekBar rSeekBar = view.findViewById(R.id.r_seek_bar);
        rSeekBar.setProgress(r);
        SeekBar gSeekBar = view.findViewById(R.id.g_seek_bar);
        gSeekBar.setProgress(g);
        SeekBar bSeekBar = view.findViewById(R.id.b_seek_bar);
        bSeekBar.setProgress(b);

        aSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean bool) {
                a = i;
                aTextView.setText("a: " + i);
                setARGB();
                colorPickerListener.onARGBChange(getARGB(), getTag());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean bool) {
                r = i;
                rTextView.setText("r: " + i);
                setARGB();
                colorPickerListener.onARGBChange(getARGB(), getTag());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        gSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean bool) {
                g = i;
                gTextView.setText("g: " + i);
                setARGB();
                colorPickerListener.onARGBChange(getARGB(), getTag());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        bSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean bool) {
                b = i;
                bTextView.setText("b: " + i);
                setARGB();
                colorPickerListener.onARGBChange(getARGB(), getTag());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.setView(view);

        Button positiveButton = view.findViewById(R.id.color_picker_positive_button);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                colorPickerListener.onConfirm(getARGB(), getTag());
                ColorPickerDialogFragment.this.dismiss();
            }
        });

        Button negativeButton = view.findViewById(R.id.color_picker_negative_button);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ColorPickerDialogFragment.this.dismiss();
            }
        });

        dialog = builder.create();
        return dialog;
    }

    ColorPickerListener colorPickerListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            colorPickerListener = (ColorPickerListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("must implement NoticeDialogListener");
        }
    }

    public int[] getARGB() {
        return new int[] {a,r,g,b};
    }

    public void setARGB() {
        color.setBackgroundColor(Color.argb(a,r,g,b));
    }

}