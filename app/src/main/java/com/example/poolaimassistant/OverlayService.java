package com.example.poolaimassistant;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

/**
 * Owns the overlay windows: a mostly touch-transparent drawing layer and the floating menu.
 */
public class OverlayService extends Service implements OverlayMenu.Listener {

    private static final String CHANNEL_ID = "overlay_service_channel";

    private WindowManager windowManager;
    private OverlayView overlayView;
    private OverlayMenu overlayMenu;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(1, buildNotification());

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        attachOverlay();
        overlayMenu = new OverlayMenu(this, this);
        overlayMenu.show();
    }

    private void attachOverlay() {
        overlayView = new OverlayView(this);

        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;

        windowManager.addView(overlayView, params);
    }

    private Notification buildNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_menu_compass)
                .setContentTitle("Pool Aim Assistant")
                .setContentText("Overlay active")
                .setOngoing(true)
                .build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Overlay Service",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayView != null && overlayView.getWindowToken() != null) {
            windowManager.removeView(overlayView);
        }
        if (overlayMenu != null) {
            overlayMenu.dismiss();
        }
    }

    @Override
    public void onShowDirectChanged(boolean enabled) {
        overlayView.setShowDirect(enabled);
    }

    @Override
    public void onShowBankChanged(boolean enabled) {
        overlayView.setShowBank(enabled);
    }

    @Override
    public void onThicknessChanged(float px) {
        overlayView.setThickness(px);
    }

    @Override
    public void onClearTarget() {
        overlayView.clearTarget();
    }

    @Override
    public void onOverlayVisibleChanged(boolean visible) {
        overlayView.setOverlayVisible(visible);
    }
}
