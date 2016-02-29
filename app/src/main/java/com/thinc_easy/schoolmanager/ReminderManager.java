package com.thinc_easy.schoolmanager;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.Calendar;

public class ReminderManager {

    private Context mContext;
    private AlarmManager mAlarmManager;

    public ReminderManager(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
    }

    public void setReminder(Long taskId, Calendar when) {

        Intent i = new Intent(mContext, OnAlarmReceiver.class);
        i.putExtra("taskId", (long) taskId);
        i.putExtra("DAY_OF_YEAR", when.get(Calendar.DAY_OF_YEAR));
        i.putExtra("PERIOD_START", 12);

        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, i,
                PendingIntent.FLAG_ONE_SHOT);

        mAlarmManager.set(AlarmManager.RTC_WAKEUP, when.getTimeInMillis(), pi);
//        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, pi);
        //Toast.makeText(mContext, String.valueOf(when.getTimeInMillis()), Toast.LENGTH_SHORT).show();
    }

	public void setLessonReminder1(Long taskId, Calendar calStart, Calendar calEnd, String mSubjectName, String mSubjectAbbrev, String Room, String mTeacherName, String Color, String Textcolor){
		Intent i = new Intent(mContext, OnAlarmReceiver.class);
		i.putExtra("task", "startLesson");
		i.putExtra("taskId", (long) taskId);
		i.putExtra("SUBJECT_NAME", mSubjectName);
		i.putExtra("SUBJECT_ABBREV", mSubjectAbbrev);
		i.putExtra("ROOM", Room);
		i.putExtra("TEACHER_NAME", mTeacherName);
		i.putExtra("COLOR", Color);
		i.putExtra("TEXTCOLOR", Textcolor);

		PendingIntent pi = PendingIntent.getBroadcast(mContext, (int) ((long) taskId), i,
				PendingIntent.FLAG_UPDATE_CURRENT);

		final int ONE_WEEK_MILLIS = 1000*60*60*24*7;

		mAlarmManager.setRepeating(AlarmManager.RTC, calStart.getTimeInMillis(), ONE_WEEK_MILLIS, pi);

		/*Intent i2 = new Intent(mContext, OnAlarmReceiver.class);
		i2.putExtra("task", "endLesson");
		i2.putExtra("taskId", (long) taskId);

		PendingIntent pi2 = PendingIntent.getBroadcast(mContext, 0, i2,
				PendingIntent.FLAG_ONE_SHOT);

		mAlarmManager.setRepeating(AlarmManager.RTC, calEnd.getTimeInMillis(), ONE_WEEK_MILLIS, pi2);*/
	}

	public void deleteReminder(Long taskId){
		Intent i = new Intent(mContext, OnAlarmReceiver.class);

		PendingIntent pi = PendingIntent.getBroadcast(mContext, (int) ((long) taskId), i,
				PendingIntent.FLAG_UPDATE_CURRENT);

		mAlarmManager.cancel(pi);
	}
    
    public void setLessonReminder(Long taskId,String mSubjectName, String Room, String mTeacherName, String Color, String Textcolor, int periodStart, String dayOfWeek) {

    	SharedPreferences shared = PreferenceManager.getDefaultSharedPreferences(mContext);
    	
    	String p1start = (shared.getString("pref_key_period1_start", "07:55"));
    	String p1end = (shared.getString("pref_key_period1_end", "08:40"));
    	String p2start = (shared.getString("pref_key_period2_start", "8:40"));
    	String p2end = (shared.getString("pref_key_period2_end", "09:25"));
    	String p3start = (shared.getString("pref_key_period3_start", "09:45"));
    	String p3end = (shared.getString("pref_key_period3_end", "10:30"));
    	String p4start = (shared.getString("pref_key_period4_start", "10:35"));
    	String p4end = (shared.getString("pref_key_period4_end", "11:20"));
    	String p5start = (shared.getString("pref_key_period5_start", "11:40"));
    	String p5end = (shared.getString("pref_key_period5_end", "12:25"));
    	String p6start = (shared.getString("pref_key_period6_start", "12:25"));
    	String p6end = (shared.getString("pref_key_period6_end", "13:10"));
    	String p7start = (shared.getString("pref_key_period7_start", "13:45"));
    	String p7end = (shared.getString("pref_key_period7_end", "14:30"));
    	String p8start = (shared.getString("pref_key_period8_start", "14:30"));
    	String p8end = (shared.getString("pref_key_period8_end", "15:15"));
    	String p9start = (shared.getString("pref_key_period9_start", "15:15"));
    	String p9end = (shared.getString("pref_key_period9_end", "16:00"));
    	String p10start = (shared.getString("pref_key_period10_start", "16:00"));
    	String p10end = (shared.getString("pref_key_period10_end", "16:45"));
    	String p11start = (shared.getString("pref_key_period11_start", "16:45"));
    	String p11end = (shared.getString("pref_key_period11_end", "17:30"));
    	String p12start = (shared.getString("pref_key_period12_start", "17:30"));
    	String p12end = (shared.getString("pref_key_period12_end", "18:15"));
    	
    	String[] times = {p1start, p1end, p2start, p2end, p3start, p3end, p4start, p4end, p5start, p5end, p6start, p6end, 
    						p7start, p7end, p8start, p8end, p9start, p9end, p10start, p10end, p11start, p11end, p12start, p12end};

    	int hp1s=0, hp1e=0, hp2s=0, hp2e=0, hp3s=0, hp3e=0, hp4s=0, hp4e=0, hp5s=0, hp5e=0, hp6s=0, hp6e=0, hp7s=0, hp7e=0, hp8s=0, hp8e=0,
    		hp9s=0, hp9e=0, hp10s=0, hp10e=0, hp11s=0, hp11e=0, hp12s=0, hp12e=0;
    	int mp1s=0, mp1e=0, mp2s=0, mp2e=0, mp3s=0, mp3e=0, mp4s=0, mp4e=0, mp5s=0, mp5e=0, mp6s=0, mp6e=0, mp7s=0, mp7e=0, mp8s=0, mp8e=0,
		mp9s=0, mp9e=0, mp10s=0, mp10e=0, mp11s=0, mp11e=0, mp12s=0, mp12e=0;
    	
    	int[] hourInts = {hp1s, hp1e, hp2s, hp2e, hp3s, hp3e, hp4s, hp4e, hp5s, hp5e, hp6s, hp6e, hp7s, hp7e, hp8s, hp8e,
        		hp9s, hp9e, hp10s, hp10e, hp11s, hp11e, hp12s, hp12e};
    	int[] minuteInts = {mp1s, mp1e, mp2s, mp2e, mp3s, mp3e, mp4s, mp4e, mp5s, mp5e, mp6s, mp6e, mp7s, mp7e, mp8s, mp8e,
    			mp9s, mp9e, mp10s, mp10e, mp11s, mp11e, mp12s, mp12e};
    	int[] hourIntsStart = {hp1s, hp2s, hp3s, hp4s, hp5s, hp6s, hp7s, hp8s, hp9s, hp10s, hp11s, hp12s};
    	int[] minuteIntsStart = {mp1s, mp2s, mp3s, mp4s, mp5s, mp6s, mp7s, mp8s, mp9s, mp10s, mp11s, mp12s};
    	
    	int[] periodNumbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
    	int[] dayNumbers = {2, 3, 4, 5, 6, 7, 1};
    	int hour=0, minute=0, day=0;
        String[] mDayNames = mContext.getResources().getStringArray(R.array.DayNames);
    	
    	for (int i = 0; i < times.length; i++){
    		String[] timeSplit = times[i].split(":");
    		hourInts[i] = Integer.valueOf(timeSplit[0]);
    		minuteInts[i] = Integer.valueOf(timeSplit[1]);
    	}
    	
    	String check = "false";
    	for (int i2 = 0; i2<periodNumbers.length; i2++){
    		if (Integer.valueOf(periodStart).equals(periodNumbers[i2])){
    			hour = hourInts[(i2)*2];
    			minute = minuteInts[(i2)*2];
    			check = "true";
    		}
    	}
    	
    	for (int i3 = 0; i3 < mDayNames.length; i3++){
    		if (mDayNames[i3].equals(dayOfWeek)){
    			day = dayNumbers[i3];
    		}
    	}
    	
//    	OnAlarmReceiver yourBR = null;
//    	yourBR = new OnAlarmReceiver();
//	    yourBR.setReminderManagerHandler(this);    
//	    IntentFilter callInterceptorIntentFilter = new IntentFilter("android.intent.action.ANY_ACTION");
//	    registerReceiver(callInterceptor,  callInterceptorIntentFilter);
    	
    	final Calendar cal=Calendar.getInstance();
    	int nowDay = cal.get(Calendar.DAY_OF_WEEK);
    	int nowHour = cal.get(Calendar.HOUR_OF_DAY);
    	int nowMinute = cal.get(Calendar.MINUTE);
    	int nowDayOfYear = cal.get(Calendar.DAY_OF_YEAR);
    	int nowYear = cal.get(Calendar.YEAR);
    	
    	int dayDifference = 0;
    	
    	if (nowDay < day){
    		dayDifference = day - nowDay;
    	}
    	if (nowDay == day & nowHour < hour){
    			dayDifference = 0;
    	}
    	if (nowDay == day & nowHour == hour & nowMinute < minute){
    		dayDifference = 0;
    	}
    	if (nowDay > day){
    		dayDifference = day + 7 - nowDay;
    	}
    	if (nowDay == day & nowHour > hour){
			dayDifference = day + 7 - nowDay;
    	}
    	if (nowDay == day & nowHour == hour & nowMinute > minute){
    		dayDifference = day + 7 - nowDay;
    	}
    	
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.DAY_OF_YEAR, nowDayOfYear + dayDifference);
        cal.set(Calendar.YEAR, nowYear);
    	
        Intent i = new Intent(mContext, OnAlarmReceiver.class);
        i.putExtra("taskId", (long) taskId);
        i.putExtra("DAY_OF_YEAR", cal.get(Calendar.DAY_OF_YEAR));
        i.putExtra("PERIOD_START", periodStart);
        i.putExtra("SUBJECT_NAME", mSubjectName);
        i.putExtra("DAY_OF_WEEK", dayOfWeek);
        i.putExtra("ROOM", Room);
        i.putExtra("TEACHER_NAME", mTeacherName);
        i.putExtra("COLOR", Color);
        i.putExtra("TEXTCOLOR", Textcolor);

        PendingIntent pi = PendingIntent.getBroadcast(mContext, 0, i,
                PendingIntent.FLAG_ONE_SHOT);

        mAlarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
//        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 100, pi);
        //Toast.makeText(mContext, check+" + "+periodStart+" + "+String.valueOf(hour)+" + "+String.valueOf(minute)+" + "+dayOfWeek+" + "+String.valueOf(day)+" + "+String.valueOf(nowDay)+" + "+String.valueOf(cal.get(Calendar.YEAR))+" + "+String.valueOf(cal.get(Calendar.MONTH))+" + "+String.valueOf(cal.get(Calendar.DAY_OF_MONTH))+" + "+String.valueOf(cal.get(Calendar.HOUR_OF_DAY))+" + "+String.valueOf(cal.get(Calendar.MINUTE)), Toast.LENGTH_LONG).show();
    }
}
