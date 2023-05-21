package com.example.leshemayapro.classes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.example.leshemayapro.R;
import com.example.leshemayapro.activities.MainActivity;

import java.util.Random;

public final class NotificationHelper
{
    public static String CHANNEL_ID = "vegan_me_channel_id";
    public static String name = "Vegan Me";
    public static String description = "Notifications for Vegan Me";

    public static void createNotification (String text, Context context)
    {
        int notificationId = 123; // new Random().nextInt();
        Intent intent = new Intent(context, Recipe.class);
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_MUTABLE);

        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_foreground);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat
                .Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setColor(Color.CYAN)
                .setLargeIcon(icon)
                .setContentTitle("Vegan me")
                .setContentText(text)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setContentIntent(pendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setWhen(0)
                .setOngoing(false);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createChannel(notificationManager);
        notificationBuilder.setChannelId(CHANNEL_ID);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

    public static void createChannel (NotificationManager notificationManager)
    {
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(mChannel);
    }
}