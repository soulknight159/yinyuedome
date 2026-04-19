package cn.itcast.yinyue.ui.activity;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import cn.itcast.yinyue.service.MusicService;
import cn.itcast.yinyue.service.ServiceConnectionManager;
import cn.itcast.yinyue.ui.fragment.LocalMusicFragment;
import cn.itcast.yinyue.ui.fragment.MineFragment;
import cn.itcast.yinyue.ui.fragment.NetMusicFragment;
import cn.itcast.yinyue.untils.PermissionCallback;
import cn.itcast.yinyue.untils.PermissionUtils;
import cn.itcast.yinyue.R;


public class MainActivity extends AppCompatActivity {
    private static final String  TAG = "MainActivity";
    private final Fragment netMusicFragment = new NetMusicFragment();
    private final Fragment localMusicFragment = new LocalMusicFragment();
    private final Fragment mineFragment = new MineFragment();
    private Fragment currentFragment = netMusicFragment;
    ImageButton play;
    private String[] permissions = {
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.POST_NOTIFICATIONS
    };
    private ServiceConnectionManager<MusicService> connectionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        connectionManager = ServiceConnectionManager.getInstance();

        play = findViewById(R.id.playOrStop);

        play.setOnClickListener(view -> {
            if (connectionManager.getService() != null &&
                    connectionManager.isServiceBound()){
                MusicService service = connectionManager.getService();
                if (service.isPlaying()){
                    service.pauseMusic();   //暂停播放
                }else {
                    service.resumeMusic();  //继续播放
                }
            }
        });

        PermissionUtils.requestPermissions(this, permissions, new PermissionCallback() {
            @Override
            public void onGranted() {}
            @Override
            public void onDenied() {}
            @Override
            public void onPermanentlyDenied() {}
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        ViewCompat.setOnApplyWindowInsetsListener(bottomNav, (v, insets) -> {
            // 移除所有自动添加的内边距（重点是底部）
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), 0);
            return insets; // 仍然传递 insets 给子视图，不消费
        });

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, netMusicFragment)
                .add(R.id.fragment_container, localMusicFragment)
                .add(R.id.fragment_container,mineFragment)
                .hide(localMusicFragment)
                .hide(mineFragment)
                .commit();

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment targetFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_net_music) {
                targetFragment = netMusicFragment;
            } else if (itemId == R.id.nav_local_music) {
                targetFragment = localMusicFragment;
            } else if (itemId == R.id.nav_mine) {
                targetFragment = mineFragment;
            }

            if (targetFragment != null && targetFragment != currentFragment) {
                getSupportFragmentManager().beginTransaction()
                        .hide(currentFragment)
                        .show(targetFragment)
                        .commit();
                currentFragment = targetFragment;
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectionManager.bindService(this, MusicService.class);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (connectionManager != null) {
                connectionManager.unbindService(this);
//                connectionManager.release();
            }
//            Intent intent = new Intent(this, MusicService.class);
//            stopService(intent);
        } catch (Exception e) {
            Log.e(TAG, "onDestroy: ",e);
        }
    }
}