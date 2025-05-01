package com.example.exam_petpal.data;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.exam_petpal.models.PremiumSubscription;
import java.util.Date;

public class PremiumManager {
    private static final String PREFS_NAME = "PremiumPrefs";
    private static final String KEY_IS_PREMIUM = "is_premium";
    private static final String KEY_SUBSCRIPTION_TYPE = "subscription_type";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";
    private static final String KEY_PRICE = "price";

    private SharedPreferences prefs;
    private static PremiumManager instance;

    private PremiumManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PremiumManager getInstance(Context context) {
        if (instance == null) {
            instance = new PremiumManager(context.getApplicationContext());
        }
        return instance;
    }

    public void activatePremium(PremiumSubscription subscription) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_PREMIUM, true);
        editor.putString(KEY_SUBSCRIPTION_TYPE, subscription.getSubscriptionType());
        editor.putLong(KEY_START_DATE, subscription.getStartDate().getTime());
        editor.putLong(KEY_END_DATE, subscription.getEndDate().getTime());
        editor.putFloat(KEY_PRICE, (float) subscription.getPrice());
        editor.apply();
    }

    public void deactivatePremium() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(KEY_IS_PREMIUM, false);
        editor.clear();
        editor.apply();
    }

    public boolean isPremiumActive() {
        boolean isPremium = prefs.getBoolean(KEY_IS_PREMIUM, false);
        if (isPremium) {
            long endDate = prefs.getLong(KEY_END_DATE, 0);
            if (endDate < System.currentTimeMillis()) {
                deactivatePremium();
                return false;
            }
        }
        return isPremium;
    }

    public PremiumSubscription getCurrentSubscription() {
        if (!isPremiumActive()) {
            return null;
        }

        PremiumSubscription subscription = new PremiumSubscription();
        subscription.setActive(true);
        subscription.setSubscriptionType(prefs.getString(KEY_SUBSCRIPTION_TYPE, ""));
        subscription.setStartDate(new Date(prefs.getLong(KEY_START_DATE, 0)));
        subscription.setEndDate(new Date(prefs.getLong(KEY_END_DATE, 0)));
        subscription.setPrice(prefs.getFloat(KEY_PRICE, 0));
        return subscription;
    }
} 