package ca.chekit.android.screen;

import java.util.Set;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import ca.chekit.android.R;
import ca.chekit.android.util.Utilities;

import com.microsoft.windowsazure.notifications.NotificationsHandler;

public class MyHandler extends NotificationsHandler {
	
	public static final int NOTIFICATION_ID = 1;
	private NotificationManager notificationManager;
	private Context context;

	@Override
	public void onReceive(Context context, Bundle bundle) {
	    this.context = context;
	    Set<String> keys = bundle.keySet();
	    if (!Utilities.isEmpty(keys)) {
	    	for (String key : keys) {
	    		Object value = bundle.get(key);
	    		Log.d("PUSHSERVICE", key + ": " + value);
	    	}
	    }
	    
	    sendNotification(null);
	}

	private void sendNotification(String msg) {
		Log.d("PUSHMESSAGE", "" + msg);
	    notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
	    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, ChatScreen.class), 0);

	    NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
	    	.setSmallIcon(R.drawable.ic_launcher)
	    	.setContentTitle("Notification Hub Demo")
	    	.setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
	    	.setContentText(msg);

	    builder.setContentIntent(contentIntent);
	    notificationManager.notify(NOTIFICATION_ID, builder.build());
	}

}
