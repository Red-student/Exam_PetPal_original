package com.example.exam_petpal;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.app.AlarmManager;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Date;

import androidx.core.app.NotificationCompat;
import com.example.exam_petpal.data.PetManager;
import com.example.exam_petpal.models.Pet;
import com.example.exam_petpal.models.Event;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "pet_reminders";
    private static final String CHANNEL_NAME = "Напоминания о питомце";
    private static final String CHANNEL_DESCRIPTION = "Уведомления о событиях питомца";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            restoreAllReminders(context);
            return;
        }
        if ("DAILY_SUMMARY".equals(intent.getAction())) {
            showDailySummary(context);
            return;
        }
        String petId = intent.getStringExtra("petId");
        String eventType = intent.getStringExtra("eventType");
        String eventTitle = intent.getStringExtra("eventTitle");
        String eventDescription = intent.getStringExtra("eventDescription");

        createNotificationChannel(context);
        showNotification(context, petId, eventType, eventTitle, eventDescription);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.setLightColor(Color.BLUE);
            channel.enableVibration(true);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(Context context, String petId, String eventType, String eventTitle, String eventDescription) {
        Intent intent = new Intent(context, PetDetailActivity.class);
        intent.putExtra("petId", petId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(eventTitle)
                .setContentText(eventDescription)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());

        // Обновление статуса уведомления в Firestore
        updateNotificationStatus(petId, eventType);
    }

    private void updateNotificationStatus(String petId, String eventType) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();

        db.collection("pets").document(petId)
                .collection("notifications")
                .document(eventType)
                .update("lastNotificationTime", currentTime)
                .addOnFailureListener(e -> {
                    // Если документ не существует, создаем его
                    db.collection("pets").document(petId)
                            .collection("notifications")
                            .document(eventType)
                            .set(new NotificationStatus(currentTime));
                });
    }

    private static class NotificationStatus {
        long lastNotificationTime;

        NotificationStatus(long lastNotificationTime) {
            this.lastNotificationTime = lastNotificationTime;
        }
    }

    private void restoreAllReminders(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("pet_events_prefs", Context.MODE_PRIVATE);
        for (String key : prefs.getAll().keySet()) {
            if (!key.startsWith("events_")) continue;
            String json = prefs.getString(key, null);
            if (json == null) continue;
            try {
                JSONArray arr = new JSONArray(json);
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject obj = arr.getJSONObject(i);
                    String id = obj.getString("id");
                    String title = obj.getString("title");
                    String desc = obj.optString("description", "");
                    long dateMillis = obj.getLong("date");
                    boolean repeatWeekly = obj.optBoolean("repeatWeekly", false);
                    boolean repeatMonthly = obj.optBoolean("repeatMonthly", false);
                    long now = System.currentTimeMillis();
                    if (dateMillis < now && !repeatWeekly && !repeatMonthly) continue; // пропускаем прошедшие одноразовые
                    Intent intent = new Intent(context, ReminderReceiver.class);
                    intent.putExtra("petId", id);
                    intent.putExtra("eventType", "event_" + id);
                    intent.putExtra("eventTitle", title);
                    intent.putExtra("eventDescription", desc);
                    int requestCode = id.hashCode();
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    if (repeatWeekly) {
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, dateMillis, AlarmManager.INTERVAL_DAY * 7, pendingIntent);
                    } else if (repeatMonthly) {
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, dateMillis, AlarmManager.INTERVAL_DAY * 30, pendingIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, dateMillis, pendingIntent);
                    }
                }
            } catch (JSONException ex) { ex.printStackTrace(); }
        }
    }

    private void showDailySummary(Context context) {
        List<Pet> pets = PetManager.getInstance(context).getPets();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String today = sdf.format(new Date());
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Pet pet : pets) {
            for (Event event : (List<Event>) (pet.getEvents() != null ? pet.getEvents() : new ArrayList<Event>())) {
                String eventDate = sdf.format(event.getDate());
                if (eventDate.equals(today)) {
                    sb.append(pet.getName()).append(": ").append(event.getTitle());
                    if (event.getDescription() != null && !event.getDescription().isEmpty()) {
                        sb.append(" (" + event.getDescription() + ")");
                    }
                    sb.append("\n");
                    count++;
                }
            }
        }
        if (count == 0) return;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "petpal_events";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "События PetPal", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
        Intent openIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_pets)
                .setContentTitle("События на сегодня: " + count)
                .setContentText("Нажмите, чтобы посмотреть детали")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(sb.toString()))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationManager.notify(1001, builder.build());
    }
} 