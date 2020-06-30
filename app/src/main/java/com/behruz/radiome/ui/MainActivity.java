package com.behruz.radiome.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.behruz.radiome.R;
import com.behruz.radiome.databinding.ActivityMainBinding;
import com.behruz.radiome.model.Radio;
import com.behruz.radiome.utils.Constants;
import com.behruz.radiome.service.PlayerInService;
import com.behruz.radiome.utils.PreferenUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.HashMap;
import java.util.Map;

import static com.behruz.radiome.service.PlayerInService.ARGS_RADIO_MODEL;
import static com.behruz.radiome.service.PlayerInService.mp;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    public  Radio homeRadioPlaying;
    private PreferenUtil preferenUtil;
    private Map<String, Runnable> navigationMap = new HashMap<String, Runnable>();
    private String action;

    private Runnable navigateLibrary = new Runnable() {
        public void run() {
            Fragment fragment =  HomeFragment.newInstance((radio) -> {
                preferenUtil.saveLastRadioPlayed(radio);
                setUpView(radio);
                playRadio();
            });
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, fragment).commitAllowingStateLoss();

        }
    };

    private Runnable navigateRadioList= new Runnable() {
        public void run() {
            String  category = getIntent().getExtras().getString("category");

            Fragment fragment =  RadioListFragment.newInstance(category, radio -> {
                preferenUtil.saveLastRadioPlayed(radio);
                setUpView(radio);
                playRadio();
            });
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, fragment).commit();

        }
    };

    private void playRadio() {
        PreferenUtil preferenUtil = PreferenUtil.getInstant(getApplicationContext());
        homeRadioPlaying = preferenUtil.getLastRadioPlayed();
        if (homeRadioPlaying != null) {
            if (mp != null) {
                if (mp.isPlaying()) {
                    binding.imgPlayPause.setImageDrawable(getApplicationContext()
                            .getResources().getDrawable(R.drawable.baseline_play_arrow_black_48dp));

                } else {
                    binding.imgPlayPause.setImageDrawable(getApplicationContext()
                            .getResources().getDrawable(R.drawable.baseline_stop_black_48dp));
                }
            } else {
                binding.imgPlayPause.setImageDrawable(getApplicationContext()
                        .getResources().getDrawable(R.drawable.baseline_stop_black_48dp));
            }
            //Save last option of either music or radio played
            preferenUtil.saveLastOptionOfPlayed("radio");
            //    dataBinding.quickControlRadioProgressbarPlay.setVisibility(View.VISIBLE);
            Intent intent = new Intent(MainActivity.this, PlayerInService.class);
            intent.setAction(Constants.ACTION.PLAY_ACTION);
            intent.putExtra(ARGS_RADIO_MODEL, homeRadioPlaying);
            startService(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Select a radio station from radio list",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        action = getIntent().getAction();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        preferenUtil = PreferenUtil.getInstant(getApplicationContext());
        navigationMap.put(Constants.ACTION.NAVIGATE_HOME, navigateLibrary);
        navigationMap.put(Constants.ACTION.NAVIGATE_RADIO_LIST, navigateRadioList);
        loadEverything();
        homeRadioPlaying = preferenUtil.getLastRadioPlayed();
        if (homeRadioPlaying != null){
            setUpView(homeRadioPlaying);
        }
        openNowPlayingScreen();
        playClick();
        addBackstackListener();
    }

    private void loadEverything() {
        Runnable navigation = navigationMap.get(action);
        if (navigation != null) {
            navigation.run();
        } else {
            navigateLibrary.run();
        }

    }

    private void setUpView(Radio radio){
        Glide.with(this)
                .load(radio.getRadioImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(binding.imgRadio);

        binding.txtNowPlaying.setText("Now Playing " +radio.getRadioName());
        binding.radioThings.setText(radio.getRadioName());
    }

    private void playClick(){
        binding.imgPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if (NetworkUtil.hasInternetConnection(getContext())) {
                playRadio();
            }
        });
    }

    private boolean isNavigatingMain() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        return (currentFragment instanceof HomeFragment );
    }



    private void openNowPlayingScreen(){
        binding.bottomPlaying.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (homeRadioPlaying != null) {
                    Intent intent = new Intent(MainActivity.this, NowPlayingActivity.class);
                    intent.putExtra(ARGS_RADIO_MODEL, homeRadioPlaying);
                    startActivityForResult(intent, 1);
                    //  startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Select a radio station from radio list",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void addBackstackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                getSupportFragmentManager().findFragmentById(R.id.frame_layout).onResume();
            }
        });
    }
}