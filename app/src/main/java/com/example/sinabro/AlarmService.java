package com.example.sinabro;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class AlarmService extends Service {

    private MediaPlayer mediaPlayer;
    private boolean isRunning;
    public static boolean flag;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Foreground 에서 실행되면 Notification 을 보여줘야 됨
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Oreo(26) 버전 이후 버전부터는 channel 이 필요함
            String channelId =  createNotificationChannel();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
            Notification notification = builder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    //.setCategory(Notification.CATEGORY_SERVICE)
                    .build();

            startForeground(1, notification);
        }

        String state = intent.getStringExtra("state");

        if (!this.isRunning && state.equals("on")) {
            // 알람음 재생 OFF, 알람음 시작 상태
            this.mediaPlayer = MediaPlayer.create(this, R.raw.bgm);
            this.mediaPlayer.start();

            this.isRunning = true;

            Log.d("AlarmService", "Alarm Start");

            flag=true;

            Bundle bun = new Bundle();

            String text="dsa";

            bun.putString("notiMessage", text);



            Intent popupIntent = new Intent(getApplicationContext(), MainActivity.class);



            popupIntent.putExtras(bun);

            PendingIntent pie= PendingIntent.getActivity(getApplicationContext(), 0, popupIntent, PendingIntent.FLAG_ONE_SHOT);

            try {

                pie.send();

            } catch (PendingIntent.CanceledException e) {



            }






        } else if (this.isRunning & state.equals("off")) {
            // 알람음 재생 ON, 알람음 중지 상태
            this.mediaPlayer.stop();
            this.mediaPlayer.reset();
            this.mediaPlayer.release();

            this.isRunning = false;

            Log.d("AlarmService", "Alarm Stop");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stopForeground(true);
            }
        }

        return START_NOT_STICKY;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel() {
        String channelId = "Alarm";
        String channelName = getString(R.string.app_name);
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
        //channel.setDescription(channelName);
        channel.setSound(null, null);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        return channelId;
    }





}
