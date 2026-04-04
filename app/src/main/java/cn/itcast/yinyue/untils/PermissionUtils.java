package cn.itcast.yinyue.untils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionUtils {

    /**
     * 申请权限（外部调用这个）
     */
    public static void requestPermissions(
            AppCompatActivity activity,
            String[] permissions,
            PermissionCallback callback
    ) {
        // 检查是否已有权限
        List<String> deniedList = getDeniedPermissions(activity, permissions);

        if (deniedList.isEmpty()) {
            callback.onGranted();
            return;
        }

        // 注册权限请求
        ActivityResultLauncher<String[]> launcher = activity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), result -> {

                    boolean allGranted = true;
                    for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                        if (!entry.getValue()) {
                            allGranted = false;
                            break;
                        }
                    }

                    if (allGranted) {
                        callback.onGranted();
                    } else {
                        // 判断是否永久拒绝
                        if (hasPermanentlyDenied(activity, permissions)) {
                            callback.onPermanentlyDenied();
                        } else {
                            callback.onDenied();
                        }
                    }
                });

        launcher.launch(deniedList.toArray(new String[0]));
    }

    /**
     * 获取未授权的权限
     */
    public static List<String> getDeniedPermissions(Context context, String[] permissions) {
        List<String> denied = new ArrayList<>();
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(context, p) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                denied.add(p);
            }
        }
        return denied;
    }

    /**
     * 是否有永久拒绝的权限
     */
    public static boolean hasPermanentlyDenied(Activity activity, String[] permissions) {
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(activity, p) != android.content.pm.PackageManager.PERMISSION_GRANTED
                    && !activity.shouldShowRequestPermissionRationale(p)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 跳转到应用设置页面
     */
    public static void goToAppSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     * 显示权限设置弹窗（通用）
     */
    public static void showPermissionSettingDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("权限提示")
                .setMessage("部分权限已被禁用，请前往设置开启")
                .setPositiveButton("去设置", (dialog, which) -> goToAppSettings(context))
                .setNegativeButton("取消", null)
                .show();
    }
}