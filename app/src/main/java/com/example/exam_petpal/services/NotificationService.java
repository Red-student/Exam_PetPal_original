package com.example.exam_petpal.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.exam_petpal.MainActivity;
import com.example.exam_petpal.R;

public class NotificationService {
    private static final String CHANNEL_ID = "petpal_channel";
    private static final String CHANNEL_NAME = "PetPal Notifications";
    private static final String CHANNEL_DESCRIPTION = "Notifications for pet care events";

    private Context context;
    private NotificationManager notificationManager;
    private static NotificationService instance;

    private NotificationService(Context context) {
        this.context = context.getApplicationContext();
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    public static synchronized NotificationService getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationService(context);
        }
        return instance;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void showEventNotification(String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void showVaccineReminder(String petName, String vaccineName) {
        String title = "Напоминание о вакцинации";
        String message = String.format("Пора сделать прививку %s питомцу %s", vaccineName, petName);
        showEventNotification(title, message);
    }

    public void showFeedingReminder(String petName) {
        String title = "Время кормления";
        String message = String.format("Пора покормить %s", petName);
        showEventNotification(title, message);
    }

    public void showWalkReminder(String petName) {
        String title = "Время прогулки";
        String message = String.format("Пора погулять с %s", petName);
        showEventNotification(title, message);
    }
} 