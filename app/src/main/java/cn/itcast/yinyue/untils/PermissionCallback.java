package cn.itcast.yinyue.untils;

public interface PermissionCallback {
    // 权限全部通过
    void onGranted();

    // 权限被拒绝
    void onDenied();

    // 权限被永久拒绝
    void onPermanentlyDenied();
}
