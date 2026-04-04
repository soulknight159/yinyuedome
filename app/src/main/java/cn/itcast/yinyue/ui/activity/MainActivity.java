package cn.itcast.yinyue.ui.activity;

import android.Manifest;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import cn.itcast.yinyue.ui.fragment.LocalMusicFragment;
import cn.itcast.yinyue.ui.fragment.MineFragment;
import cn.itcast.yinyue.ui.fragment.NetMusicFragment;
import cn.itcast.yinyue.untils.PermissionCallback;
import cn.itcast.yinyue.untils.PermissionUtils;
import cn.itcast.yinyue.R;


public class MainActivity extends AppCompatActivity {

    private final Fragment netMusicFragment = new NetMusicFragment();
    private final Fragment localMusicFragment = new LocalMusicFragment();
    private final Fragment mineFragment = new MineFragment();
    private Fragment currentFragment = netMusicFragment;
    private String[] permissions = {
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.INTERNET,
    };

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
}