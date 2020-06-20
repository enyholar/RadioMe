package com.behruz.radiome.service;

/**
 * Created by ENNY on 2/25/2018.
 */

public class Constants {
  public interface ACTION {
    public static String MAIN_ACTION = "com.marothiatechs.customnotification.action.main";
    public static String INIT_ACTION = "com.marothiatechs.customnotification.action.init";
    public static String PREV_ACTION = "com.marothiatechs.customnotification.action.prev";
    public static String PLAY_ACTION = "com.marothiatechs.customnotification.action.play";
    public static String NEXT_ACTION = "com.marothiatechs.customnotification.action.next";
    public static String STARTFOREGROUND_ACTION = "com.marothiatechs.customnotification.action.startforeground";
    public static String STOPFOREGROUND_ACTION = "com.marothiatechs.customnotification.action.stopforeground";

    public static String PREV_ACTION_BACKGROUND = "com.marothiatechs.customnotification.action.prev_back";
    public static String PLAY_ACTION_FROM_LIST = "com.marothiatechs.customnotification.action.play_back";
    public static String PLAY_ACTION_FROM_BACKGROUND = "com.marothiatechs.customnotification.action.play_background";
    public static String NEXT_ACTION_BACKGROUND = "com.marothiatechs.customnotification.action.next_back";
    public static boolean isPause = false;

  }

  public interface NOTIFICATION_ID {
    public static int FOREGROUND_SERVICE = 101;
  }

//  public static Bitmap getDefaultAlbumArt(Context context) {
//    Bitmap bm = null;
//    BitmapFactory.Options options = new BitmapFactory.Options();
//    try {
//      bm = BitmapFactory.decodeResource(context.getResources(),
//          R.drawable.ic_skip_next_orange_800_48dp, options);
//    } catch (Error ee) {
//    } catch (Exception e) {
//    }
//    return bm;
//  }

}
