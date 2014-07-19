package ca.chekit.android.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.TypedValue;
import ca.chekit.android.R;
import ca.chekit.android.model.WorkStatus;

public class Utilities {
	
	public static final String yyyy_MM_ddTHH_mm_ss = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String yyyy_MMMM_dd = "yyyy MMMM dd";
	public static final String yyyy_MM_dd = "yyyy.MM.dd";
	public static final String hh_mm_a = "hh.mm a";
	
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
	 
	
	public static int getTaskIconResId(Context context, WorkStatus status) {
		switch (status) {
		case Active:
			return R.drawable.status_active;
		case Cancelled:
			return 0; // TODO
		case Closed:
			return 0; // TODO
		case CompleteLeaveSite:
			return 0; // TODO
		case Complete:
			return R.drawable.status_complete;
		case IncompleteLeaveSite:
			return R.drawable.status_incomplete_leave_site;
		case New:
			return 0; // TODO
		case OnRoute:
			return R.drawable.status_on_route;
		case OnSite:
			return R.drawable.status_on_site;
		case ProblemsActive:
			return 0; // TODO
		case ProblemsDelayed:
			return R.drawable.status_problems_delayed;
		case ProblemsLeaveSite:
			return R.drawable.status_problems_leave_site;
		case ProblemsStop:
			return R.drawable.status_problems_stop;
		case SafetyDanger:
			return R.drawable.status_safety_danger;
		case SafetyIssue:
			return R.drawable.status_safety_issue;
		case Start:
			return R.drawable.status_start;
		case VerifySite:
			return R.drawable.status_verify_site;
		default:
			return 0;		
		}
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

}
