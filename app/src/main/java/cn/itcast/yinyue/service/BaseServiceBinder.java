package cn.itcast.yinyue.service;
import android.app.Service;
import android.os.Binder;

/**
 * 通用服务 Binder：可在任意 Service 中复用，完全解耦
 * @param <T> 你的 Service 类型（自动泛型推导）
 */
public class BaseServiceBinder<T extends Service> extends Binder {
    // 持有 Service 实例
    private final T service;

    // 构造方法传入任意 Service
    public BaseServiceBinder(T service) {
        this.service = service;
    }

    // 获取 Service 实例（泛型自动返回对应类型，无需强转）
    public T getService() {
        return service;
    }
}