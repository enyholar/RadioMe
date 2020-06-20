package com.behruz.radiome.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.behruz.radiome.model.Radio;
import com.google.gson.Gson;


/**
 * Created by Tinh Vu on 27/09/2016.
 */

public class PreferenUtil {

    private static final String SKIP_LANGUANGE_KEY = "SKIP_LANGUANGE_KEY";
    private static final String SKIP_REGISTRATION_KEY = "SKIPREGISTRATIONKEY";
    private static final String NIGHT_MODE_KEY = "NightMode";
    private static final String RADIO = "Radio";
    private static final String ALLRADIOJSONlIST = "AllRadiolist";
    private static final String THEMECOLOR = "ThemeColor";
    public static final String COLOR_KEY = "color";
    private static final String check_first = "Check";
    private static final String MusicPlayed = "MUSICPLAYED";
    private static final String ALARMTRIGGERED = "ALARMTRIGGER";
    private static final String TipsMain = "tip_main";
    private static final String DarkTheme = "tip_dark";
    private static final String TipsMusicRec= "tip_MusicRec";
    private static final String TipsNowPlaying = "tip_now";
    private static final String TOKENReQUEST = "TokenRequest";
    private static final String COUNTNOTIFICATION = "CountNotification";
    private static final String LANGUAGE_SELECTED = "language_selected";
    private static final String LANGUAGECHANGE = "LANGUAGE_CHANGE";
    private static final String USERINFO = "USER_INFO";
    private static final String REFFERAL = "REFFERAL_INFO";
    private static final String TIPS_SHOW = "TIPS_SHOW";
    private static final String DATE_TIPS = "DATE_TIPS";

    private static Context mContext;
    @SuppressLint("StaticFieldLeak")
    private static PreferenUtil mInstant = null;

    private PreferenUtil(Context context) {
        mContext = context;
    }

    public static PreferenUtil getInstant(Context context) {
        if (mInstant == null) {
            mInstant = new PreferenUtil(context);
            PreferenceManager.getDefaultSharedPreferences(context)
                    .registerOnSharedPreferenceChangeListener(mListener);
        }
        return mInstant;
    }


    private static SharedPreferences.OnSharedPreferenceChangeListener mListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        }
    };

    public void enableNightMode(boolean isChecked) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(NIGHT_MODE_KEY, isChecked);
        sharedPreferencesEditor.apply();
    }

    public boolean checkEnableNightMode() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getBoolean(NIGHT_MODE_KEY, false);
    }

    public void enableSkipRegistration(boolean isChecked) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(SKIP_REGISTRATION_KEY, isChecked);
        sharedPreferencesEditor.apply();
    }

    public boolean checkSkipRegistration() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getBoolean(SKIP_REGISTRATION_KEY, false);
    }


    public void enableSkipLanguage(boolean isChecked) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(SKIP_LANGUANGE_KEY, isChecked);
        sharedPreferencesEditor.apply();
    }

    public boolean checkSkipLanguage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getBoolean(SKIP_LANGUANGE_KEY, false);
    }

    public void enableDarkThemeDefault(boolean isChecked) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(DarkTheme, isChecked);
        sharedPreferencesEditor.apply();
    }

    public boolean checkDarkThemeDefault() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getBoolean(DarkTheme, false);
    }


    public void checkFirstLaunch(Boolean isFirstLaunch) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        if (!getFirstLaunch()) {
            sharedPreferencesEditor.putBoolean(check_first, true);
            sharedPreferencesEditor.apply();
        } else {
            sharedPreferencesEditor.putBoolean(check_first, isFirstLaunch);
            sharedPreferencesEditor.apply();
        }

    }


    public boolean getFirstLaunch() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getBoolean(check_first, false);
    }


    public boolean checkFirtLaunch() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String keyCheckFirstLunch = "CheckFirstLunch";
        boolean checkFirstLunch = sharedPreferences.getBoolean(keyCheckFirstLunch, true);
        if (checkFirstLunch) {
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putBoolean(keyCheckFirstLunch, false);
            sharedPreferencesEditor.apply();
        }
        return checkFirstLunch;
    }

    public void saveLastRadioPlayed(Radio radio) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(radio);
        sharedPreferencesEditor.putString(RADIO, json);
        sharedPreferencesEditor.apply();
    }

    public void saveAllRadioListFromFireBase(String key, String radioJson) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(key, radioJson);
        sharedPreferencesEditor.apply();
    }

    public void saveAllGenres(String key, String radioJson) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(key, radioJson);
        sharedPreferencesEditor.apply();
    }

    public void saveCountOfRadioFromFireBase(String key, Long count) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putLong(key, count);
        sharedPreferencesEditor.apply();
    }

    public void saveAllRadioList(String radioJson) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(ALLRADIOJSONlIST, radioJson);
        sharedPreferencesEditor.apply();
    }

    public String getAllRadioList() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getString(ALLRADIOJSONlIST, "");
    }

    public Long getAllCountFromRadio(String country_key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getLong(country_key, 0);
    }

    public String getAllRadioListRadioBasedOnCountry(String country_key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return String.valueOf(sharedPreferences.getString(country_key, ""));
    }

    public String getAllRadioListRadioBasedOnGenres(String genreskey) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return String.valueOf(sharedPreferences.getString(genreskey, ""));
    }

    public Radio getLastRadioPlayed() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(RADIO, "");
        return gson.fromJson(json, Radio.class);
    }

    public void saveCurrentTheme(String theme) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(THEMECOLOR, theme);
        sharedPreferencesEditor.apply();
    }

    public String getCurrentTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return String.valueOf(sharedPreferences.getString(THEMECOLOR, ""));
    }

    public void saveLastOptionOfPlayed(String option) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(MusicPlayed, option);
        sharedPreferencesEditor.apply();
    }

    public String getLastOptionOfPlayed() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return String.valueOf(sharedPreferences.getString(MusicPlayed, ""));
    }


    public void saveLastPlayedVideoResume(String key, int position) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putInt(key, position);
        sharedPreferencesEditor.apply();
    }

    public int getLastPlayedVideoResume(String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getInt(key, 0);
    }


    public void saveAlarmTriggered(String option) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(ALARMTRIGGERED, option);
        sharedPreferencesEditor.apply();
    }

    public String getAlarmtriggered() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return String.valueOf(sharedPreferences.getString(ALARMTRIGGERED, ""));
    }

    public void saveTipsMainShow(String option) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(TipsMain, option);
        sharedPreferencesEditor.apply();
    }

    public String getTipsShowMain() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return String.valueOf(sharedPreferences.getString(TipsMain, ""));
    }

    public void saveFirstTimeDarkThemeShow(String option) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(DarkTheme, option);
        sharedPreferencesEditor.apply();
    }

    public String getDarkThemeShowFirst() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return String.valueOf(sharedPreferences.getString(DarkTheme, ""));
    }

    public void saveTipsMusicRecShow(String option) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(TipsMusicRec, option);
        sharedPreferencesEditor.apply();
    }

    public String getTipsMusicRecMain() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return String.valueOf(sharedPreferences.getString(TipsMusicRec, ""));
    }

    public void saveTipsNowPlayingShow(String option) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(TipsNowPlaying, option);
        sharedPreferencesEditor.apply();
    }

    public String getTipsNowPlayingMain() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return String.valueOf(sharedPreferences.getString(TipsNowPlaying, ""));
    }

    public void saveToken(String token) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(TOKENReQUEST, token);
        sharedPreferencesEditor.apply();
    }

    public String getTokenRequest() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return String.valueOf(sharedPreferences.getString(TOKENReQUEST, ""));
    }

    public void saveNotificationCount(int count) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putInt(COUNTNOTIFICATION, count);
        sharedPreferencesEditor.apply();
    }

    public int getNotificationCount() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getInt(COUNTNOTIFICATION, 0);
    }


    public void setLanguageSelection(String preference) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(LANGUAGE_SELECTED, preference);
        sharedPreferencesEditor.apply();
    }

    public String getLanguageSelected() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return String.valueOf(sharedPreferences.getString(LANGUAGE_SELECTED, ""));
    }

    public void setLanguageChanged(boolean preference) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putBoolean(LANGUAGECHANGE, preference);
        sharedPreferencesEditor.apply();
    }

    public Boolean didLanguageChange() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getBoolean(LANGUAGECHANGE, false);
    }

    public void setUserReferralCode(String preference) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(REFFERAL, preference);
        sharedPreferencesEditor.apply();
    }

    public void saveUserInfo(String preference) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(USERINFO, preference);
        sharedPreferencesEditor.apply();
    }

//    public User getUserInfo() {
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
//        Gson gson = new Gson();
//        String json = sharedPreferences.getString(USERINFO, "");
//        return gson.fromJson(json, User.class);
//    }

    public String getReferredUser() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return String.valueOf(sharedPreferences.getString(REFFERAL, ""));
    }


    public void startTips(String option) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(TIPS_SHOW, option);
        sharedPreferencesEditor.apply();
    }

    public String getTipsShow() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return String.valueOf(sharedPreferences.getString(TIPS_SHOW, ""));
    }

    public void lastTipsDate(String option) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putString(DATE_TIPS, option);
        sharedPreferencesEditor.apply();
    }

    public String getLastTipDate() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        return String.valueOf(sharedPreferences.getString(DATE_TIPS, ""));
    }

}
