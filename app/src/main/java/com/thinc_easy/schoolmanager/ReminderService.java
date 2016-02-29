package com.thinc_easy.schoolmanager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ComponentInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

public class ReminderService extends WakeReminderIntentService {

    private static final String TAG = ComponentInfo.class.getCanonicalName();

    private String[] colorNames = {"red", "pink", "purple", "deep_purple", "indigo", "blue",
            "light_blue", "cyan", "teal", "green", "light_green", "lime", "yellow", "amber",
            "ornge", "deep_orange", "brown", "grey", "blue_grey", "black", "white"};
    private int[] colorInts = {0xffF44336, 0xffE91E63, 0xff9C27B0, 0xff673AB7, 0xff3F51B5, 0xff2196F3,
            0xff03A9F4, 0xff00BCD4, 0xff009688, 0xff4CAF50, 0xff8BC34A, 0xffCDDC39,
            0xffFFEB3B, 0xffFFC107, 0xffFF9800, 0xffFF5722, 0xff795548, 0xff9E9E9E, 0xff607D8B, 0xff000000, 0xffFFFFFF};
	
    public ReminderService() {
        super("ReminderService");
        Log.d(TAG, "ReminderService()");
    	}

    @Override
    void doReminderWork(Intent intent) {
        Log.d(TAG, "doReminderWork start");
    	/** Create an intent that will be fired when the user clicks the notification.
         * The intent needs to be packaged into a {@link android.app.PendingIntent} so that the
         * notification service can fire it on our behalf.
         */
        Intent intent2 = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent2, 0);

        String mSubjectName = intent.getExtras().getString("SUBJECT_NAME");
        String mSubjectAbbrev = intent.getExtras().getString("SUBJECT_ABBREV");
        String Room = intent.getExtras().getString("ROOM");
        String mTeacherName = intent.getExtras().getString("TEACHER_NAME");
        String Color = intent.getExtras().getString("COLOR");
        String Textcolor = intent.getExtras().getString("TEXTCOLOR");
        /**
         * Use NotificationCompat.Builder to set up our notification.
         */
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
 
        /** Set the icon that will appear in the notification bar. This icon also appears
         * in the lower right hand corner of the notification itself.
         *
         * Important note: although you can use any drawable as the small icon, Android
         * design guidelines state that the icon should be simple and monochrome. Full-color
         * bitmaps or busy images don't render well on smaller screens and can end up
         * confusing the user.
         */
        builder.setSmallIcon(R.drawable.ic_launcher);
 
        // Set the intent that will fire when the user taps the notification.
        builder.setContentIntent(pendingIntent);
 
        // Set the notification to auto-cancel. This means that the notification will disappear
        // after the user taps it, rather than remaining until it's explicitly dismissed.
        builder.setAutoCancel(true);


        int colorInt = 0xffFFFFFF;
        for (int i1 = 0; i1 < colorNames.length; i1++) {
            if (colorNames[i1].equals(Color)) {
                colorInt = colorInts[i1];
            }
        }

        final float scale = getResources().getDisplayMetrics().density;
        int bounds = (int) (40 * scale + 0.5f);

        GradientDrawable gd = new GradientDrawable();
        gd.setShape(1);
        gd.setBounds(0, 0, bounds, bounds);
        gd.setSize(bounds, bounds);
        gd.setColor(colorInt);

        Bitmap largeIcon = drawableToBitmap(gd);
        /**
         *Build the notification's appearance.
         * Set the large icon, which appears on the left of the notification. In this
         * sample we'll set the large icon to be the same as our app icon. The app icon is a
         * reasonable default if you don't have anything more compelling to use as an icon.
         */
        builder.setLargeIcon(largeIcon);

        /**
         * Set the text of the notification. This sample sets the three most commononly used
         * text areas:
         * 1. The content title, which appears in large type at the top of the notification
         * 2. The content text, which appears in smaller text below the title
         * 3. The subtext, which appears under the text on newer devices. Devices running
         *    versions of Android prior to 4.2 will ignore this field, so don't use it for
         *    anything vital!
         */
        builder.setContentTitle(mSubjectName.replace("[comma]", ","));
        builder.setContentText(Room.replace("[comma]", ","));
        builder.setSubText(mTeacherName.replace("[comma]", ","));
 
        
        Long taskId = intent.getExtras().getLong("taskId");
        int NOTIFICATION_ID = (int) ((long) taskId);
 
        /**
         * Send the notification. This will immediately display the notification icon in the
         * notification bar.
         */
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private int getNotificationIcon() {
        boolean whiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return /*whiteIcon ? R.drawable.icon_silhouette : */R.drawable.ic_launcher;
    }
}