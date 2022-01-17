package com.example.tileswipe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class ColorPickerDialogFragment extends DialogFragment {
    public interface ColorPickerListener {
        public void onARGBChange(int[] argb, String buttonTag);
        public void onConfirm(int[] argb, String buttonTag);
    }

    AlertDialog dialog;
    TextView colorTextView;
    int a = 255;
    int r = 255;
    int g = 255;
    int b = 255;

    @Override
    public Dialog onCreateDialog(Bundle savedInstance) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        View view = requireActivity().getLayoutInflater().inflate(R.layout.fragment_color_picker_dialog, null);

        SharedPreferences preferences = requireContext().getSharedPreferences(ChangePuzzleBoardColorsActivity.COLOR_SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
        String colorString = preferences.getString(getTag(), Color.WHITE + "");
        int color = Integer.parseInt(colorString);
        a = Color.alpha(color);
        r = Color.red(color);
        g = Color.green(color);
        b = Color.blue(color);


        colorTextView = view.findViewById(R.id.color_picker_dialog_color_text_view);
        colorTextView.setBackgroundColor(Color.argb(a,r,g,b));

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
                String aNumber = "a: " + getAlignedText(i + "");
                aTextView.setText(aNumber);
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
                String rNumber = "r: " + getAlignedText(i + "");
                rTextView.setText(rNumber);
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
                String rNumber = "g: " + getAlignedText(i + "");
                gTextView.setText(rNumber);
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
                String bNumber = "b: " + getAlignedText(i + "");
                bTextView.setText(bNumber);
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
        colorTextView.setBackgroundColor(Color.argb(a,r,g,b));
    }

    public String getAlignedText(String number) {
        int length = number.length();
        Log.d("length", length + "");

        switch (length) {
            case 1:
                return "  " + number;
            case 2:
                return " " + number;
            default:
                return number;
        }
    }

}