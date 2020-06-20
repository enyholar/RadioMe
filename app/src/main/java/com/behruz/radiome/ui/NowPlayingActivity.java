package com.behruz.radiome.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.behruz.radiome.R;
import com.behruz.radiome.databinding.ActivityNowPlayingBinding;
import com.behruz.radiome.model.Radio;
import com.behruz.radiome.service.Constants;
import com.behruz.radiome.service.PlayerInService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import static com.behruz.radiome.service.Constants.ACTION.NEXT_ACTION;
import static com.behruz.radiome.service.Constants.ACTION.PREV_ACTION;
import static com.behruz.radiome.service.PlayerInService.ARGS_RADIO_MODEL;

public class NowPlayingActivity extends AppCompatActivity {
    private ActivityNowPlayingBinding binding;
    public static Radio radioModel;
    public static final String ARGS_RADIO_MODEL = "radio_model";
    public static final String ARGS_RADIO_ARRAYLIST = "radio_arraylist";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_now_playing);
        radioModel = (Radio) getIntent().getSerializableExtra(ARGS_RADIO_MODEL);
        if(PlayerInService.mp != null){
            if (PlayerInService.mp.isPlaying()) {
                binding.imgPlayPause.setImageDrawable(getApplicationContext()
                        .getResources().getDrawable(R.drawable.baseline_stop_black_48dp));
            }
        }
        setUpView();
        onButtonClick();
    }

    private void setUpView(){
        if (radioModel != null){
            binding.txtName.setText(radioModel.getRadioName());
            binding.txtTitle.setText(radioModel.getRadioName());
            Glide.with(this)
                    .load(radioModel.getRadioImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.radiArt);
        }
    }

    private void onButtonClick(){
        binding.imgPlayPause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startService(Constants.ACTION.PLAY_ACTION);
            }

        });
        binding.imgNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(NowPlayingActivity.this, PlayerInService.class);
                intent.setAction(NEXT_ACTION);
                startService(intent);
            }
        });
        //  btnRecord.setEnabled(false);
        binding.imgPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(NowPlayingActivity.this, PlayerInService.class);
                intent.setAction(PREV_ACTION );
                startService(intent);

            }
        });
    }

    public  void startService(String action){
        Intent intent = new Intent(this, PlayerInService.class);
        intent.setAction(action);
        intent.putExtra(ARGS_RADIO_MODEL, radioModel);
     //   intent.putExtra(ARGS_RADIO_ARRAYLIST, radioJson);
        startService(intent);
    }
}