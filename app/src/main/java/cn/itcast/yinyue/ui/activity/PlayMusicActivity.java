package cn.itcast.yinyue.ui.activity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import cn.itcast.yinyue.R;
import cn.itcast.yinyue.service.MusicService;
import cn.itcast.yinyue.service.ServiceConnectionManager;

public class PlayMusicActivity extends AppCompatActivity {
    TextView name;
    SeekBar seekBar;
    ImageButton play;
    boolean isDragging = false;
    Handler updateSeekBarHandler=new Handler(Looper.getMainLooper());
    Runnable updateSeekBarRunnable;
    private ServiceConnectionManager<MusicService> connectionManager;   //使用封装的服务管理，加载MusicServic
    MusicService musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_play_music);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        /**
         * 初始化connectionManager
         * 绑定控件
         * 从Intent获取url和title
         * 设置标题
         */
        connectionManager = new ServiceConnectionManager<>();

        seekBar=findViewById(R.id.seekBar);
        play=findViewById(R.id.playOrStop);
        name=findViewById(R.id.name);

        Intent intentData=getIntent();
        String url = intentData.getStringExtra("url");
        String title = intentData.getStringExtra("title");
        name.setText(title);

        /**
         * 设置connectionManager的绑定监听器
         * 实现回调接口的方法
         */
        connectionManager.setOnServiceBindListener(new ServiceConnectionManager.OnServiceBindListener<>() {
            @Override
            /**
             * 服务绑定成功的回调方法
             * @param service 为实际服务
             */
            public void onServiceConnected(MusicService service) {
                //在回调方法中获取具体服务
                musicService = connectionManager.getService();
                /**
                 * 调用MusicService的方法设置MediaPlayer监听事件
                 * 实现OnMusicStateListener的回调方法
                 */
                service.setOnMusicStateListener(new MusicService.OnMusicStateListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {} //播放完成回调

                    @Override
                    //MediaPlayer准备成功回调
                    public void onPrepared(MediaPlayer mp) {
                        seekBar.setMax(service.getDuration());
                    }
                    @Override
                    //发生错误调用
                    public boolean onError(MediaPlayer mp, int what, int extra) { return false; }
                    @Override
                    //缓存更新调用
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {}
                });

                /**
                 * 设置按钮的单机事件监听
                 * getPlayState()获取MusicService服务状态
                 * IDLE空闲状态时传入地址开始播放并开始同步更新进度条
                 * service.isPlaying()状态暂停或者继续播放
                 */
                play.setOnClickListener(view1 -> {
                    if (service.getPlayState() == MusicService.IDLE){
                        service.playMusic(url);
                        updateSeekBarHandler.post(updateSeekBarRunnable);
                    }
                    if (service.isPlaying()){
                        service.pauseMusic();   //暂停播放
                    }else {
                        service.resumeMusic();  //继续播放
                    }
                });

                //设置SeekBar事件监听器
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            // 跳转到用户拖动的位置
                            service.seekTo(progress);
                        }
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // 开始拖动，暂停自动更新
                        isDragging = true;
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // 结束拖动，恢复自动更新
                        isDragging = false;
                    }
                });

            }
            @Override
            //服务绑定失败回调
            public void onServiceDisconnected() {}
        });

        //同步更新进度条
        updateSeekBarRunnable = new Runnable() {
            @Override
            public void run() {
                if(connectionManager.isServiceBound()
                        && musicService.getPlayState() == MusicService.PLAYING
                        && !isDragging){
                    seekBar.setProgress(musicService.getCurrentPosition());
                }
                //延迟100ms循环运行
                updateSeekBarHandler.postDelayed(this,100);
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicService.class);
        // 使用封装好的 connection
        bindService(intent, connectionManager.getServiceConnection(), BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connectionManager.getServiceConnection());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        updateSeekBarHandler.removeCallbacks(updateSeekBarRunnable);
    }
}