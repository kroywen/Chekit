package ca.chekit.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ca.chekit.android.model.ChatMessage;
import ca.chekit.android.model.ScheduledStatus;
import ca.chekit.android.model.WorkTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.TypedValue;

public class Utilities {
	
	public static final String yyyy_MM_ddTHH_mm_ss = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String yyyy_MMMM_dd = "yyyy MMMM dd";
	public static final String dd_MMMM_yyyy = "dd MMMM yyyy";
	public static final String yyyy_MM_dd = "yyyy.MM.dd";
	public static final String hh_mm_a = "hh.mm a";
	public static final String dd_MM_yyyy_hh_mm_a = "dd.MM.yyyy hh.mm a";
	
	public static boolean isConnectionAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		return info != null && info.isConnected();
	}
	
	public static String streamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();

	    String line = null;
	    try {
	        while ((line = reader.readLine()) != null) {
	            sb.append(line + "\n");
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            is.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    return sb.toString();
	}
	
	public static boolean isEmpty(Collection<?> c) {
		return (c == null || c.isEmpty());
	}
	
	public static String convertDate(String date, String fromPattern, String toPattern) {
		DateFormat originalFormat = new SimpleDateFormat(fromPattern, Locale.ENGLISH);
		DateFormat targetFormat = new SimpleDateFormat(toPattern, Locale.ENGLISH);
		try {
			Date d = originalFormat.parse(date);
			return targetFormat.format(d); 
		} catch (Exception e) {
			e.printStackTrace();
			return date;
		}		
	}
	
	public static long parseDate(String date, String pattern) throws ParseException {
		DateFormat d = new SimpleDateFormat(pattern, Locale.ENGLISH);
		return d.parse(date).getTime();
	}
	
	public static String addSpaces(String text) {
		if (TextUtils.isEmpty(text)) {
			return text;
		}
		StringBuilder builder = new StringBuilder();
		int i = 0;
		while (i < text.length()) {
			char c = text.charAt(i);
			if (Character.isUpperCase(c) && i > 0) {
				builder.append(' ');
			}
			builder.append(c);
			i++;
		}
		return builder.toString();
	}
	
	public static int dpToPx(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
	}
	
	public static String getHumanReadableTime(long time) {
		if (time == 0) {
			return "0 min";
		}
		
		int d = (int) time / (60 * 24);
	    int remainderDays = (int) (time % (60 * 24));
	    int h = remainderDays / 60;
	    int m = remainderDays % 60;
	    
	    String text = "";
	    if (m > 0) {
	    	text = m + " min" + text; 
	    }
	    if (h > 0) {
	    	text = h + " hrs " + text;
	    }
	    if (d > 0) {
	    	text = d + " d " + text;
	    }
	    return text;
	}
	
	public static String[] generateRange(int start, int end) {
		int length = end - start;
		String[] items = new String[length];
		for (int i=start; i<end; i++) {
			items[i] = String.valueOf(i);
		}
		return items;
	}
	
	public static long getDayStart(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}
	
	public static long getDayEnd(long time) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(time);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTimeInMillis();
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String parseTime(long time, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
		return sdf.format(new Date(time));
	}
	
	public static String getAppVersionName(Context context) {
		try {
			PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return pInfo.versionName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static List<WorkTask> filterWorktasks(List<WorkTask> worktasks, ScheduledStatus... scheduledStatusList) {
		if (Utilities.isEmpty(worktasks)) {
			return null;
		}
		List<WorkTask> filtered = new ArrayList<WorkTask>();
		for (WorkTask task : worktasks) {
			if (scheduledStatusList == null || scheduledStatusList.length == 0) {
				continue;
			}
			for (ScheduledStatus scheduledStatus : scheduledStatusList) {
				if (task.getScheduledStatus() == scheduledStatus) {
					filtered.add(task);
					break;
				}
			}
			
		}
		return filtered;
	}
	
	public static long findLastUpdateTime(List<ChatMessage> messages) {
		if (Utilities.isEmpty(messages)) {
			return 0;
		}
		long lastUpdateTime = 0;
		for (ChatMessage message : messages) {
			if (message.getInsertDateMillis() > lastUpdateTime) {
				lastUpdateTime = message.getInsertDateMillis();
			}
		}
		return lastUpdateTime;
	}
	
	public static long calculateRemainingTime(long remainingTime, long statusChanged) {
		remainingTime *= 60000;
		long remainingTimeDate = remainingTime + statusChanged;
		long difference = remainingTimeDate - System.currentTimeMillis();
		return difference / 60000;
	}

}
