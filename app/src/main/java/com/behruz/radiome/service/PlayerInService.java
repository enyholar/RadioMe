package com.behruz.radiome.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.os.StrictMode.ThreadPolicy.Builder;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.media.app.NotificationCompat;

import com.behruz.radiome.R;
import com.behruz.radiome.model.Radio;
import com.behruz.radiome.ui.NowPlayingActivity;
import com.behruz.radiome.utils.PreferenUtil;
import com.behruz.radiome.utils.Utility;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;


public class PlayerInService extends Service implements OnClickListener,
        OnCompletionListener, OnSeekBarChangeListener, MediaPlayer.OnInfoListener,
        OnAudioFocusChangeListener {

  private WeakReference<ImageButton> btnPlay;
  private WeakReference<ImageButton> btnNext;
  public static WeakReference<TextView> textViewSongTime;
  public static WeakReference<SeekBar> songProgressBar;
  static Handler progressBarHandler = new Handler();
  public static final String ARGS_RADIO_MODEL = "radio_model";
  private final IBinder mBinder = new LocalBinder();

  public static MediaPlayer mp;
  public static boolean isPause = false;
  Radio secondModel;
  Radio firstRadioModel;
  List<Radio> radioList;
  NowPlayingActivity activity;
  private String radioJson;
  Utility utility;
  private Notification status;
  private PendingIntent pendingIntent;
  private String LOG_TAG = "Value";
  private ServiceCallbacks serviceCallbacks;
  Handler handler;
  private String title_artist;
  private String artist;
  private Timer timer;
  private DatabaseReference mDatabaseUsers;
  String CHANNEL_ID = "my_channel";// The id of the channel.
  CharSequence name = "Channel";// The user-visible name of the channel.
  int importance = NotificationManager.IMPORTANCE_HIGH;

  private boolean onGoingCall = true;
  public static final int NOTIFICATION_ID = 555;
  private final String PRIMARY_CHANNEL = "PRIMARY_CHANNEL_ID";
  private final String PRIMARY_CHANNEL_NAME = "PRIMARY";
  private NotificationManagerCompat notificationManager;

  private NotificationChannel mChannel;
  private NotificationManager mNotificationManager;
  private AudioManager mAudioManager;
  private RemoteViews simpleContentView;
  public static Boolean isNotYetLoad = true;
  private Resources resources;
  private PlayerInService service;
  private MediaSessionCompat ms;
  private PreferenUtil preferenUtil;
  private TelephonyManager telephonyManager;
  private LocalBroadcastManager broadcaster;

  @Override
  public void onCreate() {
    ThreadPolicy policy = new Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
    mNotificationManager = (NotificationManager) this
        .getSystemService(Context.NOTIFICATION_SERVICE);
    if (VERSION.SDK_INT >= VERSION_CODES.O) {
      mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
    }
    broadcaster = LocalBroadcastManager.getInstance(this);

    handler = new Handler();
    mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
    telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
    telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    mp = new MediaPlayer();
    mp.reset();
    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
    preferenUtil = PreferenUtil.getInstant(getApplicationContext());
    this.service = PlayerInService.this;
    activity = new NowPlayingActivity();
    initUI();
    super.onCreate();
  }

  @SuppressWarnings("deprecation")
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (intent != null) {
      if (intent.getAction() != null && !intent.getAction().isEmpty()) {
        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
          telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
          assert telephonyManager != null;
          telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {
//      Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show();
          Log.i(LOG_TAG, "Clicked Previous");
          initUI();
          getPreviousSong();
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
          initUI();
          setUpRadioData(intent);
          playPause(firstRadioModel);
          //     Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show();
          Log.i(LOG_TAG, "Clicked Play");
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {
          initUI();
          getNextSong();
          Log.i(LOG_TAG, "Clicked Next");
        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION_BACKGROUND)) {
          //     Toast.makeText(this, "Clicked Previous", Toast.LENGTH_SHORT).show();
          Log.i(LOG_TAG, "Clicked Previous");
          initUI();
          getPreviousSong();
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION_FROM_LIST)) {
          initUI();
          setUpRadioData(intent);
          playPauseFromHome(firstRadioModel);
          //     Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show();
          Log.i(LOG_TAG, "Clicked Play");
        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION_FROM_BACKGROUND)) {
          playPause(firstRadioModel);
          //     Toast.makeText(this, "Clicked Play", Toast.LENGTH_SHORT).show();
//          Log.i(LOG_TAG, "Clicked Play");
        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION_BACKGROUND)) {
//      Toast.makeText(this, "Clicked Next", Toast.LENGTH_SHORT).show();
          initUI();
          getNextSong();
          Log.i(LOG_TAG, "Clicked Next");
        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
          Log.i(LOG_TAG, "Received Stop Foreground Intent");
          //    Toast.makeText(this, "Service Stoped", Toast.LENGTH_SHORT).show();
          if (mp != null) {
            mp.stop();
          }
//          }
          mNotificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
           stopForeground(true);
          stopSelf();
          //     onDestroy();
        }
      }
    }

    super.onStart(intent, startId);
    return START_STICKY;
  }



  private PhoneStateListener phoneStateListener = new PhoneStateListener() {

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

      if(state == TelephonyManager.CALL_STATE_OFFHOOK
          || state == TelephonyManager.CALL_STATE_RINGING){
        if (mp != null && !mp.isPlaying()) return;
        if (!isPause){
          if (mp != null && mp.isPlaying()){
            onGoingCall = true;
            isPause = true;
            mp.setVolume(0f,0f);
          }
        }
      } else if (state == TelephonyManager.CALL_STATE_IDLE){
        if(!onGoingCall) return;
        if (isPause && onGoingCall){
          mp.setVolume(1.0f,1.0f);
//         if(firstRadioModel != null){
//           playSong(firstRadioModel);
//         }
        }
        onGoingCall = false;
      }
    }

  };

  private void setUpRadioData(Intent intent) {
    firstRadioModel = (Radio) intent.getSerializableExtra(ARGS_RADIO_MODEL);
    //   radioJson = intent.getStringExtra(RadioPlayerActivity.ARGS_RADIO_ARRAYLIST);
    //   convertFromJsonToRadioList(radioJson);
//    getLatestModel(firstRadioModel);
  }

  private void   convertFromJsonToRadioList() {
    Gson gson = new Gson();
    Type type = new TypeToken<List<Radio>>() {
    }.getType();
    String jsonString = preferenUtil.getAllRadioList();
    if (jsonString != null && !jsonString.isEmpty()) {
      if (radioList != null && !radioList.isEmpty()) {
        radioList.clear();
      }
      radioList = gson.fromJson(jsonString, type);
    }

  }

  private void getLatestModel(Radio model) {
    if (radioList != null && radioList.size() > 0) {
      for (int i = 0; i < radioList.size(); i++) {
        if (radioList.get(i).getRadioName().equals(model.getRadioName())) {
          secondModel = radioList.get(i);
        }
      }
    }

  }

  @Override
  public void onClick(View view) {

  }

  @Override
  public void onAudioFocusChange(int focusChange) {
    if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
      onCompletion(mp);
      if (mp != null) {
        if (mp.isPlaying()) {
          mp.stop();
        }
      }
//      if (mainBindingWeakReference.get() != null) {
//        if (mainBindingWeakReference.get().quickControlPlaypause != null) {
//          mainBindingWeakReference.get().quickControlPlaypause
//              .setImageResource(R.drawable.ic_play_arrow_grey_500_48dp);
//      tint();
//        }
//
//      }


//      mNotificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
    } else if (focusChange ==  AudioManager.AUDIOFOCUS_LOSS){
      if (mp != null) {
        if (mp.isPlaying()) {
          mp.stop();
        }
      }
//      if (mainBindingWeakReference.get() != null) {
//        if (mainBindingWeakReference.get().quickControlPlaypause != null) {
//          mainBindingWeakReference.get().quickControlPlaypause
//              .setImageResource(R.drawable.ic_play_arrow_grey_500_48dp);
//          tint();
//        }
//      }
    }else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){
      if (mp != null) {
        if (mp.isPlaying()) {
          mp.setVolume(0.2f,0.2f);
        }
      }
    }else if (focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) {
      if (mp != null) {
        if (mp.isPlaying()) {
          mp.setVolume(1.0f, 1.0f);
        }
      }
    }else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
      if (mp != null) {
        if (mp.isPlaying()) {
          mp.setVolume(1.0f, 1.0f);
        }
      }
    }

  }


  public class LocalBinder extends Binder {

    public PlayerInService getService() {
      return PlayerInService.this;
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  public void setList(List<Radio> theSongs) {
    radioList = theSongs;
  }


  private void initUI() {
    mp.setOnCompletionListener(this);
    mp.setOnInfoListener(this);

  }


  private void playPause(Radio model) {
    if (mp.isPlaying()) {
      isPause = true;
      mp.stop();
      Intent intent = new Intent("MyData");
      intent.putExtra("stop", "stop");
      broadcaster.sendBroadcast(intent);
      startNotify("", model);
      progressBarHandler.removeCallbacks(mUpdateTimeTask);
      return;
    }

    if (isPause) {
      isPause = false;
      updateProgressBar();
      playSong(firstRadioModel);
      return;
    }

    if (!mp.isPlaying()) {

      playSong(firstRadioModel);
    }
  }


  private void tint() {

  }


  private void playPauseFromHome(Radio model) {
    boolean mps = mp.isPlaying();
    if (mp.isPlaying()) {
      isPause = true;
      mp.stop();
      progressBarHandler.removeCallbacks(mUpdateTimeTask);
//      if (btnPlay.get() != null) {
//        btnPlay.get().setBackgroundResource(R.drawable.ic_play_arrow_white_48dp);
//      }
      playSong(firstRadioModel);
      //    btnPlay.get().setBackgroundResource(R.drawable.ic_play_circle_outline_white_48dp);
//      timer.cancel();

      return;
    }

    if (isPause) {
      isPause = false;
      updateProgressBar();

      playSong(firstRadioModel);
      return;
    }

    if (!mp.isPlaying()) {

      playSong(firstRadioModel);
    }
  }


  public void updateProgressBar() {
    try {
      progressBarHandler.postDelayed(mUpdateTimeTask, 100);
    } catch (Exception e) {

    }
  }

  public void setCallbacks(ServiceCallbacks callbacks) {
    serviceCallbacks = callbacks;
  }

  public void getNextSong() {
    PreferenUtil preferenUtil = PreferenUtil.getInstant(getApplicationContext());
    convertFromJsonToRadioList();
    if (firstRadioModel == null) {
      firstRadioModel = preferenUtil.getLastRadioPlayed();
      if (firstRadioModel != null) {
        if (radioList != null && radioList.size() > 0) {
          for (int i = 0; i < radioList.size(); i++) {
            if (radioList.get(i).getRadioName().equals(firstRadioModel.getRadioName())) {
              int index = i + 1;
              if (radioList.size() == index) {
                firstRadioModel = radioList.get(0);
                NowPlayingActivity.radioModel = firstRadioModel;
              } else {
                firstRadioModel = radioList.get(index);
                NowPlayingActivity.radioModel = firstRadioModel;
              }
              if (mp.isPlaying()) {
                mp.stop();
              }
              if (timer != null) {
                timer.cancel();
              }


              preferenUtil.saveLastRadioPlayed(firstRadioModel);
              updateProgressBar();

              Intent intent = new Intent("MyData");
              intent.putExtra("radio", "radio");
              intent.putExtra("model",firstRadioModel);
              broadcaster.sendBroadcast(intent);
              playSong(firstRadioModel);
              break;
            }
          }
        }

      }
    } else {
      if (radioList != null && radioList.size() > 0) {
        for (int i = 0; i < radioList.size(); i++) {
          if (radioList.get(i).getRadioName().equals(firstRadioModel.getRadioName())) {
            int index = i + 1;
            if (radioList.size() == index) {
              firstRadioModel = radioList.get(0);
              NowPlayingActivity.radioModel = firstRadioModel;
            } else {
              firstRadioModel = radioList.get(index);
              NowPlayingActivity.radioModel = firstRadioModel;
            }
            if (mp.isPlaying()) {
              mp.stop();
            }
            if (timer != null) {
              timer.cancel();
            }

//            if (btnPlay != null && btnPlay.get() != null) {
//              btnPlay.get().setBackgroundResource(R.drawable.ic_play_arrow_white_48dp);
//            }

            preferenUtil.saveLastRadioPlayed(firstRadioModel);
            Intent intent = new Intent("MyData");
            intent.putExtra("radio", "radio");
            intent.putExtra("model",firstRadioModel);
            broadcaster.sendBroadcast(intent);

            playSong(firstRadioModel);
            break;
          }
        }
      }
    }
  }

  public void getPreviousSong() {
    PreferenUtil preferenUtil = PreferenUtil.getInstant(getApplicationContext());
    convertFromJsonToRadioList();
    if (firstRadioModel == null) {
      firstRadioModel = preferenUtil.getLastRadioPlayed();
      if (firstRadioModel != null) {
        if (radioList != null && radioList.size() > 0) {
          for (int i = 0; i < radioList.size(); i++) {
            if (radioList.get(i).getRadioName().equals(firstRadioModel.getRadioName())) {
              int index = i - 1;
              if (index < 0) {
                firstRadioModel = radioList.get(0);
                NowPlayingActivity.radioModel = firstRadioModel;
              } else {
                firstRadioModel = radioList.get(index);
                NowPlayingActivity.radioModel = firstRadioModel;
              }
              if (mp.isPlaying()) {
                mp.stop();
              }
              if (timer != null) {
                timer.cancel();
              }

//              if (btnPlay != null && btnPlay.get() != null) {
//                btnPlay.get().setBackgroundResource(R.drawable.ic_play_arrow_white_48dp);
//              }

              preferenUtil.saveLastRadioPlayed(firstRadioModel);
              Intent intent = new Intent("MyData");
              intent.putExtra("radio", "radio");
              intent.putExtra("model",firstRadioModel);
              broadcaster.sendBroadcast(intent);
              playSong(firstRadioModel);
              break;
            }
          }
        }
      }
    } else {
      if (radioList != null && radioList.size() > 0) {
        for (int i = 0; i < radioList.size(); i++) {
          if (radioList.get(i).getRadioName().equals(firstRadioModel.getRadioName())) {
            int index = i - 1;
            if (index < 0) {
              firstRadioModel = radioList.get(0);
              NowPlayingActivity.radioModel = firstRadioModel;
            } else {
              firstRadioModel = radioList.get(index);
              NowPlayingActivity.radioModel = firstRadioModel;
            }
            if (mp.isPlaying()) {
              mp.stop();
            }
            if (timer != null) {
              timer.cancel();
            }

//            if (btnPlay != null && btnPlay.get() != null) {
//              btnPlay.get().setBackgroundResource(R.drawable.ic_play_arrow_white_48dp);
//            }

            preferenUtil.saveLastRadioPlayed(firstRadioModel);
            Intent intent = new Intent("MyData");
            intent.putExtra("radio", "radio");
            intent.putExtra("model",firstRadioModel);
            broadcaster.sendBroadcast(intent);
            playSong(firstRadioModel);
            break;
          }
        }
      }
    }

  }

  static Runnable mUpdateTimeTask = new Runnable() {
    public void run() {
      long totalDuration = 0;
      long currentDuration = 0;

      try {
        totalDuration = mp.getDuration();
        currentDuration = mp.getCurrentPosition();
        textViewSongTime.get().setText(Utility.milliSecondsToTimer(currentDuration) + "/" + Utility
            .milliSecondsToTimer(totalDuration)); // Displaying time completed playing
        int progress = (int) (Utility.getProgressPercentage(currentDuration, totalDuration));
        //	songProgressBar.get().setProgress(progress);	/* Running this thread after 100 milliseconds */
        progressBarHandler.postDelayed(this, 100);

      } catch (Exception e) {
        e.printStackTrace();
      }

    }
  };

  @Override
  public void onDestroy() {
//    Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
//    btnPlay.get().setImageResource(R.drawable.ic_play_circle_outline_white_48dp);
//    if (timer != null){
//      timer.cancel();
//    }
    onCompletion(mp);
    if (mp != null) {
      if (mp.isPlaying()) {
        mp.stop();
      }
    }

    mNotificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);
    stopForeground(true);
  }

  // Play song
  public void playSong(final Radio model) {
    startNotify("", model);
    setUpNextImage(model);
    try {

      mp.reset();
      mp.setDataSource(String.valueOf(model.getRadioUrl()));
      mp.prepareAsync();
      mp.setOnPreparedListener(new OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
          try {
            isPause = false;
            mp.start();
            Intent intent = new Intent("MyData");
            intent.putExtra("play", "play");
            broadcaster.sendBroadcast(intent);

            //           RadioPlayerActivity.mProgressBar.hide();
            //  setUpMetadatForStreamRadio(model);
            updateProgressBar();

          } catch (Exception e) {
            Log.i("EXCEPTION", "" + e.getMessage());
          }
        }
      });

    } catch (Exception e) {
      e.printStackTrace();
    }
  }




  @Override
  public boolean onInfo(MediaPlayer mp, int what, int extra) {
    switch (what) {
      case MediaPlayer.MEDIA_INFO_BUFFERING_START:


        break;
      case MediaPlayer.MEDIA_INFO_BUFFERING_END:

        break;
    }
    return false;
  }

  private void setUpNextImage(Radio radioModel){
//    if (mBlurredArt.get() != null && mRadioArt.get() != null){
//      if(radioModel.getRadioImage() != null && !radioModel.getRadioImage().isEmpty() ){
//        if (radioModel.getRadioImage().equalsIgnoreCase("empty")){
//          Glide.with(this)
//              .load(R.drawable.radio_now_art)
//              .diskCacheStrategy(DiskCacheStrategy.ALL)
//              .into(mRadioArt.get());
//          Glide.with(this)
//              .load(R.drawable.radio_now_art)
//              .into(mBlurredArt.get());
//        }else {
//          Glide.with(this)
//              .load(radioModel.getRadioImage())
//              .diskCacheStrategy(DiskCacheStrategy.ALL)
//              .centerCrop()
//              .into(mRadioArt.get());
//
//        }
//
//      }else {
//        Glide.with(this)
//            .load(R.drawable.radio_now_art)
//            .diskCacheStrategy(DiskCacheStrategy.ALL)
//            .into(mRadioArt.get());
//        Glide.with(this)
//            .load(R.drawable.radio_now_art)
//            .into(mBlurredArt.get());
//      }
//    }
  }


  @Override
  public void onCompletion(MediaPlayer mp) {
    //  songProgressBar.get().setProgress(0);
    progressBarHandler.removeCallbacks(mUpdateTimeTask); /* Progress Update stop */
//    btnPlay.get().setBackgroundResource(R.drawable.ic_play_circle_outline_white_48dp);
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {
    progressBarHandler.removeCallbacks(mUpdateTimeTask);
  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    progressBarHandler.removeCallbacks(mUpdateTimeTask);
    int totalDuration = mp.getDuration();
    int currentPosition = Utility.progressToTimer(seekBar.getProgress(), totalDuration);
    mp.seekTo(currentPosition);
    updateProgressBar();
  }




  public Bitmap getBitmapFromURL(String strURL) {
    try {
      URL url = new URL(strURL);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoInput(true);
      connection.connect();
      InputStream input = connection.getInputStream();
      Bitmap myBitmap = BitmapFactory.decodeStream(input);
      return myBitmap;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }



  public void startNotify(String playbackStatus, Radio model) {
    Bitmap bitmap = null;
    notificationManager = NotificationManagerCompat.from(service);
    ms = new MediaSessionCompat(this, "TheFan");

    if (model != null) {
      if (model.getRadioImage() != null && !model.getRadioImage().isEmpty()) {
        if (model.getRadioImage().equalsIgnoreCase("empty")) {
          bitmap = BitmapFactory.decodeResource(this.getResources(),
              R.drawable.radio_default_art);

        } else {
          bitmap = getBitmapFromURL(model.getRadioImage());
        }
      } else {
        bitmap = BitmapFactory.decodeResource(this.getResources(),
            R.drawable.radio_default_art);
      }
    }

    Bitmap largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
    resources = PlayerInService.this.getResources();
    int icon;
    if (isPause){
       icon = R.drawable.baseline_play_arrow_white_36dp;
    }else {
       icon = R.drawable.baseline_pause_white_36dp;
    }

    int next = R.drawable.baseline_skip_next_white_36dp;
    int previous = R.drawable.baseline_skip_previous_white_36dp;
    Intent playbackAction = new Intent(service, PlayerInService.class);
    playbackAction.setAction(Constants.ACTION.PLAY_ACTION_FROM_BACKGROUND);
    PendingIntent action = PendingIntent.getService(this, 1, playbackAction, 0);

    Intent nextIntent = new Intent(service, PlayerInService.class);
    nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
    PendingIntent nextaction = PendingIntent.getService(service, 2, nextIntent, 0);

    Intent prevIntent = new Intent(service, PlayerInService.class);
    prevIntent.setAction(Constants.ACTION.PREV_ACTION);
    PendingIntent prevAction = PendingIntent.getService(service, 4, prevIntent, 0);



    Intent stopIntent = new Intent(service, PlayerInService.class);
    stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
    PendingIntent stopAction = PendingIntent.getService(service, 3, stopIntent, 0);

    Intent intent = new Intent(service, NowPlayingActivity.class);
    intent.setAction(Intent.ACTION_MAIN);
    intent.putExtra(ARGS_RADIO_MODEL, firstRadioModel);
    intent.addCategory(Intent.CATEGORY_LAUNCHER);
    PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, 0);

    notificationManager.cancel(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      NotificationManager manager = (NotificationManager) PlayerInService.this
          .getSystemService(Context.NOTIFICATION_SERVICE);
      NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL, PRIMARY_CHANNEL_NAME,
          NotificationManager.IMPORTANCE_LOW);
      channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
      assert manager != null;
      manager.createNotificationChannel(channel);
    }

    assert model != null;
    androidx.core.app.NotificationCompat.Builder builder = new   androidx.core.app.NotificationCompat.Builder(this,
        PRIMARY_CHANNEL)
        .setAutoCancel(false)
        .setContentTitle(model.getRadioName())
        .setContentText(model.getRadioName())
        .setLargeIcon(bitmap)
        .setContentIntent(pendingIntent)
        .setVisibility(  androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
        .setSmallIcon(android.R.drawable.stat_sys_headset)
        .addAction(previous, "previous", prevAction)
        .addAction(icon, "pause", action)
        .addAction(next, "next", nextaction)
        .addAction(R.drawable.baseline_stop_white_36dp, "stop", stopAction)
        .setPriority(  androidx.core.app.NotificationCompat.PRIORITY_HIGH)
        .setWhen(System.currentTimeMillis())
        .setStyle(new NotificationCompat.MediaStyle()
            .setMediaSession(ms.getSessionToken())
            .setShowActionsInCompactView(0, 1)
            .setShowCancelButton(true)
            .setCancelButtonIntent(stopAction));

    service.startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, builder.build());

  }

  public void subscribeToRadioListFromFirebase() {
    radioList = new ArrayList<>();
    mDatabaseUsers = FirebaseDatabase.getInstance()
        .getReferenceFromUrl("https://audioapp-fbc4c.firebaseio.com/RadioList");
    mDatabaseUsers.keepSynced(true);
    mDatabaseUsers.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        if (radioList != null && radioList.size() > 0) {
          radioList.clear();
        }
        Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
        while (items.hasNext()) {
          DataSnapshot item = items.next();
          Radio radio = item.getValue(Radio.class);
          radioList.add(radio);
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });

  }




  public interface ServiceCallbacks {

    void startServiceFromServiceClass();
  }
}
