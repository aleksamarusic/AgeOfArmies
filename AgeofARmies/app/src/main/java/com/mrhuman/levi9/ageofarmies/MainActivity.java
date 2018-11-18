package com.mrhuman.levi9.ageofarmies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView widthTextView;
    private SeekBar widthSeekBar;
    private TextView heightTextView;
    private SeekBar heightSeekBar;
    private TextView scaleTextView;
    private SeekBar scaleSeekBar;
    private Button battleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        widthTextView = findViewById(R.id.width_label);
        widthSeekBar = findViewById(R.id.width_seekBar);
        widthSeekBar.setMin(1);
        widthSeekBar.setMax(10);
        widthSeekBar.incrementProgressBy(1);
        widthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                widthTextView.setText(String.format(
                        "%s %s", getResources().getText(R.string.board_width).toString(),
                        Integer.toString(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        widthSeekBar.setProgress(5);

        heightTextView = findViewById(R.id.height_label);
        heightSeekBar = findViewById(R.id.height_seekBar);
        heightSeekBar.setMin(1);
        heightSeekBar.setMax(10);
        heightSeekBar.incrementProgressBy(1);
        heightSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                heightTextView.setText(String.format(
                        "%s %s", getResources().getText(R.string.board_height).toString(),
                        Integer.toString(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        heightSeekBar.setProgress(5);

        scaleTextView = findViewById(R.id.scale_label);
        scaleSeekBar = findViewById(R.id.scale_seekBar);
        scaleSeekBar.setMin(1);
        scaleSeekBar.setMax(10);
        scaleSeekBar.incrementProgressBy(1);
        scaleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                scaleTextView.setText(String.format(
                        "%s %s", getResources().getText(R.string.board_scale).toString(),
                        Integer.toString(progress)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        scaleSeekBar.setProgress(3);

        battleButton = findViewById(R.id.battle_button);
        battleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int width = widthSeekBar.getProgress();
                int height = heightSeekBar.getProgress();
                int scale =  scaleSeekBar.getProgress();

                Intent intent = new Intent(v.getContext(), GameActivity.class);
                intent.putExtra(GameActivity.WIDTH_ARG, width);
                intent.putExtra(GameActivity.HEIGHT_ARG, height);
                intent.putExtra(GameActivity.SCALE_ARG, scale);

                startActivity(intent);
            }
        });

    }
}
