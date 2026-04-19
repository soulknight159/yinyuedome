package cn.itcast.yinyue.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import cn.itcast.yinyue.R;
import cn.itcast.yinyue.consts.Consts;

public class NotificationHelper {

    private static volatile NotificationHelper INSTANCE;
    private final Context mContext;
    private final NotificationManagerCompat mNotificationManager;
    private NotificationHelper(Context context) {
        // 必须用 ApplicationContext 防止内存泄漏
        this.mContext = context.getApplicationContext();
        this.mNotificationManager = NotificationManagerCompat.from(mContext);
        createAllChannels();
    }

    /**
     * 单例获取
     */
    public static NotificationHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (NotificationHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NotificationHelper(context);
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 创建所有渠道（Android O 以上必须）
     */
    private void createAllChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 普通渠道
            NotificationChannel channelNormal = new NotificationChannel(
                    Consts.CHANNEL_ID_NORMAL,
                    Consts.CHANNEL_NAME_NORMAL,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channelNormal.setDescription(Consts.CHANNEL_DESC_NORMAL);
            channelNormal.enableVibration(true);
            channelNormal.setVibrationPattern(new long[]{0, 100, 200});

            // 注册到系统
            mNotificationManager.createNotificationChannel(channelNormal);
        }
    }

    public Notification createForegroundNotification(String title, String content, PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(mContext, Consts.CHANNEL_ID_NORMAL)
                .setSmallIcon(R.drawable.ic_mine_t) // 你的图标
                .setContentTitle(title)
                .setContentText(content)
                .setOngoing(true)       // 常驻通知
                .setAutoCancel(false)   // 点击不消失
                .setPriority(NotificationCompat.PRIORITY_LOW) // 不打扰
                .build();
    }

    /**
     * 显示普通通知
     */
    public void showNormalNotification(int notificationId, Notification notification) {
        // 权限判断（Android 13+）
        if (mNotificationManager.areNotificationsEnabled()) {
            mNotificationManager.notify(notificationId, notification);
        }
    }

    /**
     * 取消单个通知
     */
    public void cancelNotification(int notificationId) {
        mNotificationManager.cancel(notificationId);
    }

    /**
     * 取消所有通知
     */
    public void cancelAllNotifications() {
        mNotificationManager.cancelAll();
    }

    /**
     * 判断通知总开关是否打开
     */
    public boolean areNotificationsEnabled() {
        return mNotificationManager.areNotificationsEnabled();
    }
}
