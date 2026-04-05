package cn.itcast.yinyue.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ServiceConnectionManager<T extends Service> {
    // 服务对象
    private T service;
    private boolean isServiceBound = false;
    // 服务连接
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            // 安全强转通用 Binder
            BaseServiceBinder<T> binder = (BaseServiceBinder<T>) iBinder;
            service = binder.getService();
            isServiceBound = true;
            // 绑定成功回调
            if (onServiceBindListener != null) {
                onServiceBindListener.onServiceConnected(service);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
            service = null;

            if (onServiceBindListener != null) {
                onServiceBindListener.onServiceDisconnected();
            }
        }
    };
    private OnServiceBindListener<T> onServiceBindListener;
    private static volatile ServiceConnectionManager<?> instance;

    private ServiceConnectionManager(){}
    public static <T extends Service> ServiceConnectionManager<T> getInstance() {
        if (instance == null) {
            synchronized (ServiceConnectionManager.class) {
                if (instance == null) {
                    instance = new ServiceConnectionManager<>();
                }
            }
        }
        return (ServiceConnectionManager<T>) instance;
    }

    public void bindService(Context context, Class<T> serviceClass) {
        if (!isServiceBound) {
            Intent intent = new Intent(context, serviceClass);
            // Android 8.0+ 启动前台服务（先启动再绑定，避免服务被回收）
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
            context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            // 已绑定，直接回调成功
            if (onServiceBindListener != null) {
                onServiceBindListener.onServiceConnected(service);
            }
        }
    }

    // 解绑服务（建议只在应用退出时调用）
    public void unbindService(Context context) {
        if (isServiceBound) {
            context.unbindService(serviceConnection);
            isServiceBound = false;
        }
    }

    // 回调接口
    public interface OnServiceBindListener<T> {
        void onServiceConnected(T service);
        void onServiceDisconnected();
    }
    public void setOnServiceBindListener(OnServiceBindListener<T> listener) {
        this.onServiceBindListener = listener;
    }

    // 获取 ServiceConnection
    public ServiceConnection getServiceConnection() {
        return serviceConnection;
    }

    // 获取服务对象
    public T getService() {
        return service;
    }

    // 判断是否绑定
    public boolean isServiceBound() {
        return isServiceBound && service != null;
    }

    //解绑重置（防止内存泄漏）
    public void release() {
        service = null;
        isServiceBound = false;
        onServiceBindListener = null;
    }
}