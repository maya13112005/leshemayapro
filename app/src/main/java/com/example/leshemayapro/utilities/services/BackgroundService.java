package com.example.leshemayapro.utilities.services;

import static android.app.PendingIntent.getActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.leshemayapro.activities.MainActivity;
import com.example.leshemayapro.classes.NotificationHelper;
import com.example.leshemayapro.classes.PrefManager;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class BackgroundService extends Service
{
    private static final String TAG = "BackgroundService";
    CountDownTimer countDownTimer;
    Long timeMillis;
    public static final String COUNTDOWN_BR = "com.example.leshemayapro";
    Intent bi = new Intent(COUNTDOWN_BR);

    @Nullable
    @Override
    public IBinder onBind (Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId)
    {
        Log.d("FOREGROUND_SERVICE", "Start foreground service.");
        Log.d(TAG, "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n\n\n\n\n\n\n\n\n\nAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        if (intent == null)
            return super.onStartCommand(null, flags, startId);
        timeMillis = intent.getLongExtra("hours plus minutes in millis", 0);
        switch (intent.getAction())
        {
            case "ACTION_START_FOREGROUND_SERVICE":
                startForegroundService();
                Toast.makeText(this, "Foreground service is started.", Toast.LENGTH_SHORT).show();
                break;
            case "ACTION_STOP_FOREGROUND_SERVICE":
                stopForegroundService();
                Toast.makeText(this, "Foreground service is stopped.", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForegroundService ()
    {
        Log.d("FOREGROUND_SERVICE", "Start foreground service.");
        // DatabaseReference hitRef = FirebaseManager.getDataRef("processor/isHit");
        PrefManager prefManager = new PrefManager(this);
        countDownTimer = new CountDownTimer(prefManager.getPrefLong("hours plus minutes in millis", 0), 1000) {
            @Override
            public void onTick(long millisUntilFinished)
            {
                long seconds = (millisUntilFinished / 1000) % 60;
                long minutes = (millisUntilFinished / (1000 * 60)) % 60;
                long hours = (millisUntilFinished / (1000 * 60 * 60)) % 60;
                String time = (hours + " : " + minutes + ":" + seconds);
                NotificationHelper.createNotification(time + " remaining", getApplicationContext());
                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                bi.putExtra("countdown", millisUntilFinished);
                bi.putExtra("countdownTimerRunning", true);
                bi.putExtra("countdownTimerFinished", false);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish()
            {
                Log.i(TAG, "Timer finished");
                bi.putExtra("countdownTimerFinished", true);
                stopForegroundService();
                sendBroadcast(bi);
                stopForeground(STOP_FOREGROUND_REMOVE);
                stopSelf();
            }
        };
        countDownTimer.start();

        String name = "vegan me";
        String CHANNEL_ID = "vegan_me_channel_id";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        Intent dismissIntent = new Intent();
        dismissIntent.setAction("action_view");

        PendingIntent pendingIntentDismiss;
        pendingIntentDismiss = getActivity(this, 3, dismissIntent, PendingIntent.FLAG_MUTABLE);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent;
        pendingIntent = getActivity(this, 3, intent, PendingIntent.FLAG_MUTABLE);
        NotificationCompat.Builder notification = new NotificationCompat
                .Builder(this, CHANNEL_ID)
                .setContentTitle("Vegan Me Running")
                .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Dismiss", pendingIntentDismiss)
                .setContentIntent(pendingIntent)
                .setContentText("Click Dismiss to stop the App");

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        notificationManager.createNotificationChannel(mChannel);
        startForeground(1, notification.build());
    }

//    private void handleHitEvent (@NonNull DataSnapshot snapshot)
//    {
//        FightActivity.score += snapshot.getValue(Boolean.class) ? 1 : 0;
//        Log.d(TAG, "handleHitEvent:     " + snapshot.getValue(Boolean.class));
//        if (FightActivity.score == Integer.parseInt(FightActivity.match.getRoundsCap()))
//        {
//            notifyLostMessage();
//        }
//        NotificationHelper.createNotification("you got hit",this);
//        FightActivity.binding.score.setText(FightActivity.score + " / " + FightActivity.match.getRoundsCap());
//    }
//
//    private void notifyLostMessage ()
//    {
//        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(FightActivity.binding.getRoot().getContext());
//        builder.setTitle(R.string.you_lost)
//                .setMessage(R.string.lose_messege)
//                .setNegativeButton(R.string.ok, (dialogInterface, i) ->
//                        commons.activityLauncher(this, new Intent(this, MainActivity.class)))
//                .setCancelable(false)
//                .show();
//        NotificationHelper.createNotification("You Lost!\nYou lost the match.\n", this);
//    }

    private void stopForegroundService ()
    {
        Log.d("FOREGROUND_SERVICE", "Stop foreground service.");
        // Stop foreground service and remove the notification.
        stopForeground(STOP_FOREGROUND_REMOVE);
        countDownTimer.cancel();
        // Stop the foreground service.
        stopSelf();
    }
}