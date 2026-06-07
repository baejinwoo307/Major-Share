package com.majorshare.core.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "major_share_notifications";
    private static final String CHANNEL_NAME = "Major Share 알림";
    private static final String CHANNEL_DESC = "Major Share 거래 및 예약 알림";
    private static int notificationId = 1;

    public static void showNotification(Context context, String receiverId, String title, String message) {
        showNotification(context, receiverId, title, message, null);
    }

    public static void showNotification(Context context, String receiverId, String title, String message, Intent intent) {
        // DB에 알림 저장
        if (receiverId != null) {
            com.majorshare.core.db.DatabaseHelper db = new com.majorshare.core.db.DatabaseHelper(context);
            db.insertNotification(receiverId, title, message);
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        // Android 8.0 이상 채널 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (channel == null) {
                channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(CHANNEL_DESC);
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (intent != null) {
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationId, intent, flags);
            builder.setContentIntent(pendingIntent);
        }

        notificationManager.notify(notificationId++, builder.build());
    }
}
