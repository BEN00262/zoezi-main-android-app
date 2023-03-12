package com.zoeziMitzanimedia.androidapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.FileProvider;

import java.io.File;

public class NotificationHelper extends ContextWrapper {
    private final String CHANNEL_ID = getApplicationContext().getPackageName();
    private long [] VIBRATE_PATTERN = { 0, 500};

    public NotificationHelper(Context base) {
        super(base);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "file download notification";
            String description = "This is used to indicate that a file has been downloaded in the Zoezi platform";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(VIBRATE_PATTERN);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createNotification(File file){
        Uri selectedUri;
        Intent intent = new Intent(Intent.ACTION_VIEW);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            selectedUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider",file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }else{
            selectedUri = Uri.fromFile(file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        }
        intent.setDataAndType(selectedUri, "image/*");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentTitle("File Downloaded")
                .setAutoCancel(true)
                .setContentText("Requested File has been downloaded")
                .setContentIntent(pendingIntent)
                .setVibrate(VIBRATE_PATTERN)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setColor(getResources().getColor(R.color.white));
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify((int) System.currentTimeMillis(),builder.build());
    }
}

